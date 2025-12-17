🧊 FridgeGeniusAI (SaveEat ✨)
AI·딥러닝 기반 스마트 냉장고 관리 & 음식 낭비 감소 Android 애플리케이션
1. Project Overview

FridgeGeniusAI는 단순히 냉장고 속 재료를 기록하는 앱이 아닙니다.
본 프로젝트는 딥러닝과 생성형 AI를 활용해 “무엇을 언제 소비해야 하는지”를 판단하는 지능형 의사결정 지원 시스템을 목표로 설계되었습니다.

현대 사회에서 음식물 쓰레기는 환경적·경제적·윤리적 측면에서 중요한 문제로 대두되고 있습니다.
그러나 기존 냉장고 관리 앱은 대부분 다음과 같은 한계를 갖고 있습니다.

재료를 사용자가 직접 하나하나 입력해야 함

유통기한 정보는 제공되지만 우선순위 판단은 사용자 몫

“그래서 지금 뭘 먹어야 하는지”에 대한 AI 기반 판단 부재

FridgeGeniusAI는 이 문제를 ‘AI가 판단하고, 사용자가 결정하는 구조’로 재설계한 프로젝트입니다.


2. Problem Statement
❗ 기존 냉장고 관리 방식의 한계
1) 정보는 있지만 판단은 사용자 책임

유통기한은 보이지만 어떤 재료가 더 위험한지 직관적으로 알기 어려움

결과적으로 소비 결정이 지연됨

2) 입력 과정의 피로도

텍스트 기반 입력은 번거롭고 누락되기 쉬움

사용 지속성 저하



3) 형식적인 AI 활용

단순 레시피 추천 챗봇 수준

실제 행동 변화로 이어지지 않음
→ “알지만 행동하지 않는” 문제 발생

3. Project Objectives

FridgeGeniusAI는 아래 5가지 핵심 목표를 중심으로 설계되었습니다.

유통기한 기반 위험도 정량화

지금 당장 소비해야 할 재료 자동 판단

실제 조리가 가능한 AI 레시피 생성

딥러닝 기반 사진 입력 자동화

사용자의 행동 변화를 유도하는 UX 설계



4. System Architecture
[User Input]
 ├ Text Input
 ├ Image Input (ML Kit)
        ↓
[Ingredient Processing]
 ├ Room Database
 ├ Expiration → Risk Score Model
        ↓
[AI Decision Layer]
 ├ Top-Risk Ingredient Extraction
 ├ GPT-based Recipe Generation
        ↓
[UX Output]
 ├ Home Mission
 ├ Recipe Recommendation
 ├ Insight Analysis

 <img width="299" height="452" alt="image" src="https://github.com/user-attachments/assets/f6e6eae2-19e6-4316-bcd2-63d5049c5056" />

👉 AI 판단은 단일 기능이 아니라, 전체 UX 흐름의 중심 레이어로 작동합니다.



5. Core AI & Deep Learning Features
🧠 5.1 Expiration-Based Risk Scoring Model

각 재료의 남은 유통기한을 기준으로 0~100 위험도 점수 계산

단순 선형 계산이 아닌 구간별 가중치 모델 적용

상태	점수 범위	의미
안전	0~39	여유 있음
임박	40~69	주의 필요
위험	70~100	즉시 소비 권장

👉 이 점수는 모든 AI 판단의 기준 지표로 활용됩니다.

🤖 5.2 Top-Risk Ingredient Auto Selection

위험도 점수 기준 자동 정렬

사용자 설정 없이 “오늘 꼭 소비해야 할 재료 TOP 3” 제공

👉 결정 피로도를 줄이기 위한 핵심 AI 기능



🍳 5.3 AI Recipe Generation (OpenAI GPT)

OpenAI GPT API 활용

재료 기반 상황 인식형 프롬프트 설계

생성 조건

실제 조리 가능

가정용 재료/도구 기준

단계별 조리 과정 포함

AI 출력 예시

레시피 이름

예상 조리 시간

난이도

사용 재료

조리 단계

추가 팁 / 대체 재료

👉 단순 생성이 아닌 행동을 유도하는 AI 출력을 목표로 설계



📸 5.4 Image-Based Ingredient Recognition

Google ML Kit Image Labeling 사용

온디바이스 딥러닝 기반 이미지 분류

신뢰도(Confidence) 제공

⚠️ 오인식 가능성을 고려하여
→ 사용자가 직접 선택·확정하는 UX 구조

👉 AI 판단 + 사용자 통제의 균형



📊 5.5 Insights & Usage Analysis

AI 기반 낭비 감소 지표 제공

예상 절약 비용 계산

위험 재료 관리 성과 시각화

기능 사용 패턴 분석

👉 사용자가 “이 앱으로 어떤 변화가 있었는지”를 인식할 수 있도록 설계



6. UI / UX Design Philosophy

AI는 앞에, 사용자는 뒤에 두지 않는다

AI 판단은 제안, 최종 결정은 사용자

색상/아이콘 기반 위험도 시각화

버튼 최소화, 행동 유도형 UI



7. Tech Stack
📱 Android

Kotlin

Jetpack Compose

Material Design 3

🧠 AI / ML

OpenAI API (GPT)

Google ML Kit (Image Labeling)

💾 Data

Room Database

Flow / StateFlow

⚙️ Architecture

MVVM

Hilt (DI)

Coroutines



8. Key Differentiators

AI가 기능이 아닌 프로젝트 중심

딥러닝 결과를 UX로 보완

실사용 시나리오 기반 설계

확장 가능한 구조

평가용이 아닌 서비스 지향 프로젝트



9. Demo

실제 사용 흐름 기준 녹화

기능 단위별 시연

AI 판단 변화 확인 가능



10. Future Work

위험도 예측 모델 고도화

WorkManager 기반 소비 알림

개인 소비 패턴 학습

Gemini API 추가 연동

그래프 기반 인사이트 제공



11. Conclusion

FridgeGeniusAI는 AI와 딥러닝을 실생활 문제 해결에 적용한 실무형 프로젝트입니다.

본 프로젝트는 단순히 기술을 나열하지 않고,

왜 AI가 필요한지

AI가 어떤 판단을 내리고

그 판단이 어떻게 사용자 행동을 바꾸는지

를 중심으로 설계되었습니다.

FridgeGeniusAI는
기술을 보여주기 위한 프로젝트가 아니라,
문제를 해결하기 위해 AI를 사용하는 프로젝트입니다.
