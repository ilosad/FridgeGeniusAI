package com.losad.fridgegenius.ml

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageLabelAnalyzer {

    fun analyze(
        context: Context,
        uri: Uri,
        onResult: (List<Pair<String, Float>>) -> Unit
    ) {
        val image = InputImage.fromFilePath(context, uri)

        val labeler = ImageLabeling.getClient(
            ImageLabelerOptions.DEFAULT_OPTIONS
        )

        labeler.process(image)
            .addOnSuccessListener { labels ->
                val result = labels.map {
                    it.text to it.confidence
                }
                onResult(result)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
