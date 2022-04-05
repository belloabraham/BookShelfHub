package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response

data class ResponseBody(
    val attributeScores: AttributeScores,
    val languages: List<String>,
    val detectedLanguages: List<String>
)

data class AttributeScores(
    val TOXICITY: TOXICITY
)

data class TOXICITY(
    val spanScores: List<SpanScore>,
    val summaryScore: SummaryScore
)

data class SpanScore(
    val begin: Int,
    val end: Int,
    val score: Score
)

data class SummaryScore(
    val value: Double,
    val type: String
)

data class Score(
    val value: Double,
    val type: String
)