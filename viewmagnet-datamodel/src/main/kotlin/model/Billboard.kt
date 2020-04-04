package model

data class Location(val lat: Double, val lon: Double)

data class BillboardStatus(
    val billboardId: String,
    val billboardLocation: Location,
    val health: Health,
    val adId: String,
    val env: BillboardEnvironment,
    val interest: List<Float>
)

data class BillboardEnvironment(
    val weather: Weather,
    val tempC: Float,
    val windSpeed: Float,
    val sunrise: Long,
    val sunset: Long,
    val timezone: Int,
    val country: String,
    val soundDb: Float
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

enum class Object{
    BACKGROUND,
    AEROPLANE,
    BICYCLE,
    BIRD,
    BOAT,
    BOTTLE,
    BUS,
    CAR,
    CAT,
    CHAIR,
    COW,
    DININGTABLE,
    DOG,
    HORSE,
    MOTORBIKE,
    PERSON,
    POTTEDPLANT,
    SHEEP,
    SOFA,
    TRAIN,
    TVMONITOR;
}