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


class MetricService @JvmOverloads constructor (private val influxDB: InfluxDB,
                                               private val dbName: String = "viewmagnet_influx") {

    private val personMeasurement: String = "person"

    private val billboardStatusMeasurement: String = "billboard_status"

    private val adPoolMeasurement: String = "ad_pool"

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
                    .tag("billboard_id", billboardId)
                    .addField("person_age", it.age.toString())
                    .tag("person_gender", it.gender.toString())
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
                .addField("sound_dB", billboardStatus.env.soundDb)
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
                    Gender.valueOf(mutableList[4].toString()),
                    Age.valueOf(mutableList[3].toString())
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
                    Health.valueOf(mutableList[3].toString()),
                    mutableList[1].toString(),
                    BillboardEnvironment(
                        Weather.valueOf(mutableList[6].toString()),
                        mutableList[5].toString().toDouble().toInt(),
                        mutableList[4].toString().toDouble().toInt()
                    )
                )
            }[0]
    }

    fun createAdPool(adIdList: Set<String>, timestamp: Long) {
        val pointBuilder = Point.measurement(adPoolMeasurement)
        pointBuilder.time(timestamp, TimeUnit.MILLISECONDS)
        adIdList.forEachIndexed { index, s ->
            pointBuilder.addField("ad_pool_item_" + index, s)
        }
        influxDB.write(dbName, "", pointBuilder.build())
    }

    fun getLastAdPoolRecord(): AdPoolChanged? {
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
                val poolSet = mutableSetOf<String>()
                mutableList.forEachIndexed { index, e ->
                    if (index != 0) {
                        poolSet.add(e.toString())
                    }
                }
                AdPoolChanged(poolSet
                )
            }[0]
    }

}
