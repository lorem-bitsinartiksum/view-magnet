package model

data class QR(
    override val id: String,
    val billboardId: String,
    val adId: String,
    val interactionTimes: List<String>
) : Persistable