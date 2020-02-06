package model


data class BillboardStatus(
    val id: String,
    val health: Health,
    val adId: String,
    val env: BillboardEnvironment
)

data class BillboardEnvironment(
    val weather: Weather,
    val tempC: Int,
    val soundDb: Int
)

data class ModeChanged(val newMode: Mode)

enum class Health {
    UP,
    DOWN
}

enum class Weather {
    SUNNY,
    CLOUDY,
    WINDY,
    FOGGY,
    STORMY,
    SNOWY,
    RAINY,
    UNKNOWN;
}