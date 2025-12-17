package com.losad.fridgegenius.data.remote

import com.losad.fridgegenius.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * ✅ 의존성 꼬임 방지용: Android 기본 HttpURLConnection + org.json 사용
 * - Retrofit/OkHttp 없이도 안정적으로 동작
 */
object OpenAiRecipeClient {

    private const val ENDPOINT = "https://api.openai.com/v1/chat/completions"

    fun generateRecipeText(prompt: String): String {
        val apiKey = BuildConfig.OPENAI_API_KEY
        if (apiKey.isBlank()) {
            throw IllegalStateException("OPENAI_API_KEY가 비어있습니다. BuildConfig 설정을 확인하세요.")
        }

        val conn = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 60_000
            doOutput = true
            setRequestProperty("Authorization", "Bearer $apiKey")
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        val body = JSONObject().apply {
            put("model", "gpt-4o-mini") // ✅ 가성비/안정 모델
            put("temperature", 0.7)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put(
                        "content",
                        """
                        너는 한국어로 답하는 '냉장고 재료 기반 레시피 셰프'다.
                        - 레시피는 현실적으로 만들 수 있어야 한다.
                        - 사용자가 가진 재료를 최우선으로 쓰되, 부족한 재료는 '추가로 있으면 좋은 재료'로 분리해서 제안한다.
                        - 출력 포맷을 반드시 지켜라.
                        """.trimIndent()
                    )
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
        }.toString()

        conn.outputStream.use { os ->
            os.write(body.toByteArray(Charsets.UTF_8))
        }

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream

        val responseText = stream.bufferedReader().use(BufferedReader::readText)

        if (code !in 200..299) {
            // OpenAI 에러 원문을 최대한 보여줘서 디버깅 쉽게
            throw IllegalStateException("OpenAI API 오류(code=$code): $responseText")
        }

        val json = JSONObject(responseText)
        val choices = json.optJSONArray("choices") ?: throw IllegalStateException("응답 choices가 없습니다: $responseText")
        val first = choices.getJSONObject(0)
        val message = first.getJSONObject("message")
        val content = message.getString("content")

        return content.trim()
    }
}
