package model


data class BillboardStatus(
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
    Thunderstorm,
    Drizzle,
    Rain,
    Snow,
    Mist,
    Smoke,
    Haze,
    Dust;
    Fog;
    Sand;
    Ash;
    Squall;
    Tornado;
    Clear;
    Clouds;
    Unknown;
}
