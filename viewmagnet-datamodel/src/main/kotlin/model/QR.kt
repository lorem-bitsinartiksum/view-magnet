package model

data class QR(
    val billboardId: String,
    val adId: String,
    val interactionTimes: List<Long>
)