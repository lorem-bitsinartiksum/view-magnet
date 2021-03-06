package metric

import FieldAverage
import MetricCount
import model.*
import org.influxdb.InfluxDB
import org.influxdb.dto.Point
import org.influxdb.dto.Query
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MetricService @JvmOverloads constructor (private val mode: Mode,
                                               private val influxDB: InfluxDB,
                                               private val dbName: String = "viewmagnet_influx") {

    private val personMeasurement: String = "person"

    private val billboardStatusMeasurement: String = "billboard_status"

    private val adPoolMeasurement: String = "ad_pool"

    private val adDurationMeasurement: String = "ad_duration"

    init {
        this.influxDB.query(Query("CREATE DATABASE " + dbName, dbName))
    }

    fun createQueryMetricCount(tag: String, tag_value: String): String {
        val query = " SELECT count(person_age) as count FROM metrics WHERE " + tag + " = '" + tag_value + "'"
        return query
    }

    fun createQueryFieldAverageForAd(field: String, ad_id: String): String {
        val query = " SELECT mean(" + field + ") as average FROM metrics WHERE ad_id = '" + ad_id + "'"
        return query
    }

    fun createQueryLastRecord(measurement: String): String {
        val query = "SELECT * FROM " + measurement + " ORDER BY DESC LIMIT 1"
        return query
    }

    fun createPersonMetrics(personList: List<Person>, billboardId: String, timestamp: Long, adId: String) {
        personList.forEach {
            influxDB.write(
                dbName, "", Point.measurement(personMeasurement)
                    .time(timestamp, TimeUnit.MILLISECONDS)
                    .tag("ad_id", adId)
                    .addField("billboard_id", billboardId)
                    .tag("person_age", it.age.toString())
                    .tag("person_gender", it.gender.toString())
                    .tag("mode", mode.toString())
                    .build()
            )
        }

    }

    fun getMetricCount(tag: String, ad_id: String): MetricCount {
        val query = Query(
            createQueryMetricCount(tag, ad_id),
            dbName
        )
        val results = influxDB.query(query)
            .results
        if (results.first().series == null) {
            return MetricCount(0)
        }
        return results.first().series.first().values
            .map { mutableList ->
                MetricCount(
                    mutableList[1].toString().toDouble().toInt()
                )
            }[0]
    }

    fun getFieldAverage(field: String, ad_id: String): FieldAverage {
        val query = Query(
            createQueryFieldAverageForAd(field, ad_id),
            dbName
        )
        val results = influxDB.query(query)
            .results
        if (results.first().series == null) {
            return FieldAverage(0.0)
        }
        return results.first().series.first().values
            .map { mutableList ->
                FieldAverage(
                    mutableList[1].toString().toDouble()
                )
            }[0]
    }

    fun createBillboardStatus(billboardStatus: BillboardStatus, billboardId: String, timestamp: Long) {
        influxDB.write(
            dbName, "", Point.measurement(billboardStatusMeasurement)
                .time(timestamp, TimeUnit.MILLISECONDS)
                .tag("billboard_id", billboardId)
                .tag("health", billboardStatus.health.toString())
                .tag("ad_id", billboardStatus.adId)
                .addField("weather", billboardStatus.env.weather.toString())
                .addField("temp_C", billboardStatus.env.tempC)
                .addField("wind_speed", billboardStatus.env.windSpeed)
                .addField("sunrise", billboardStatus.env.sunrise)
                .addField("sunset", billboardStatus.env.sunset)
                .addField("timezone", billboardStatus.env.timezone)
                .addField("country", billboardStatus.env.country)
                .addField("sound_dB", billboardStatus.env.soundDb)
                .tag("mode", mode.toString())
                .build()
        )
    }

    fun getLastPersonRecord(): Person? {
        val query = Query(
            createQueryLastRecord(personMeasurement),
            dbName
        )
        val results = influxDB.query(query)
            .results
        if (results.first().series == null) {
            return null
        }
        return results.first().series.first().values
            .map { mutableList ->
                Person(
                    Gender.valueOf(mutableList[5].toString()),
                    Age.valueOf(mutableList[4].toString())
                )
            }[0]
    }

    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        df.timeZone = TimeZone.getTimeZone("UTC")
        return df.parse(date).time
    }

    fun getLastBillboardStatusRecord(): BillboardStatus? {
        val query = Query(
            createQueryLastRecord(billboardStatusMeasurement),
            dbName
        )
        val results = influxDB.query(query)
            .results
        if (results.first().series == null) {
            return null
        }
        return results.first().series.first().values
            .map { mutableList ->
                BillboardStatus(
                    Health.valueOf(mutableList[4].toString()),
                    mutableList[1].toString(),
                    BillboardEnvironment(
                        Weather.valueOf(mutableList[11].toString()),
                        mutableList[9].toString().toFloat(),
                        mutableList[12].toString().toFloat(),
                        mutableList[7].toString().toDouble().toLong(),
                        mutableList[8].toString().toDouble().toLong(),
                        mutableList[10].toString().toDouble().toInt(),
                        mutableList[3].toString(),
                        mutableList[6].toString().toFloat()
                    )
                )
            }[0]
    }

    fun createAdPool(billboardId: String, pool: Set<Pair<Ad, Similarity>>, timestamp: Long) {
        pool.forEachIndexed { index, element ->
            val pointBuilder = Point.measurement(adPoolMeasurement)
            pointBuilder.time(timestamp, TimeUnit.MILLISECONDS)
            pointBuilder.tag("billboard_id", billboardId)
            pointBuilder.tag("ad_id", element.first.id)
            pointBuilder.addField("similarity", element.second)
            pointBuilder.tag("mode", mode.toString())
            influxDB.write(dbName, "", pointBuilder.build())
        }
    }

    fun getLastAdPoolRecord(): Pair<String, Similarity>? {
        val query = Query(
            createQueryLastRecord(adPoolMeasurement),
            dbName
        )
        val results = influxDB.query(query)
            .results
        if (results.first().series == null) {
            return null
        }
        return results.first().series.first().values
            .map { mutableList ->
                mutableList[1].toString() to mutableList[4].toString().toFloat()
            }[0]
    }

    fun createAdDuration(durationMs: Long, billboardId: String, timestamp: Long, adId: String) {
        influxDB.write(
            dbName, "", Point.measurement(adDurationMeasurement)
                .time(timestamp, TimeUnit.MILLISECONDS)
                .tag("billboard_id", billboardId)
                .tag("ad_id", adId)
                .addField("duration_ms", durationMs)
                .tag("mode", mode.toString())
                .build()
        )

    }

    fun getLastAdDurationRecord(): Long? {
        val query = Query(
            createQueryLastRecord(adDurationMeasurement),
            dbName
        )
        val results = influxDB.query(query)
            .results
        if (results.first().series == null) {
            return null
        }
        return results.first().series.first().values
            .map { mutableList ->
                mutableList[3].toString().toDouble().toLong()
            }[0]
    }

}