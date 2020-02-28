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
    UNKNOWN,
    THUNDERSTORM,
    DRIZZLE,
    RAIN,
    SNOW,
    MIST,
    SMOKE,
    HAZE,
    DUST,
    FOG,
    SAND,
    ASH,
    SQUALL,
    TORNADO,
    CLEAR,
    CLOUDS;
}
