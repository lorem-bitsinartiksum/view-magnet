package metric

import BillboardStatus
import FieldAverage
import Gender
import Metric
import MetricCount
import Reality
import Weather
import org.influxdb.InfluxDB
import org.influxdb.dto.Point
import org.influxdb.dto.Query
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MetricService @JvmOverloads constructor (private val influxDB: InfluxDB,
                        private val dbName: String = "viewmagnet_influx") {

    private val metricsMeasurement: String = "metrics"

    private val billboardStatusMeasurement: String = "billboard_status"

    init {
        this.influxDB.query(Query("CREATE DATABASE " + dbName, dbName))
    }

    fun createQueryMetricCount(tag: String, tag_value: String): String {
        val query = " SELECT count(age) as count FROM metrics WHERE "+ tag + " = '" + tag_value + "'"
        return query
    }

    fun createQueryFieldAverageForAd(field: String, ad_id: String): String {
        val query = " SELECT mean(" + field + ") as average FROM metrics WHERE ad_id = '" + ad_id + "'"
        return query
    }

    fun createQueryLastRecord(measurement: String): String {
        val query = "SELECT * FROM "+measurement+" ORDER BY DESC LIMIT 1"
        return query
    }

    fun createMetric(metric: Metric) {
        influxDB.write(dbName, "", Point.measurement(metricsMeasurement)
            .time(metric.timestamp, TimeUnit.MILLISECONDS)
            .tag("company_id", metric.company_id.toString())
            .tag("ad_id", metric.ad_id.toString())
            .tag("billboard_id", metric.billboard_id.toString())
            .addField("age", metric.age.toString())
            .tag("gender", metric.gender.toString())
            .addField("weather", metric.weather.toString())
            .addField("temperature", metric.temperature.toString())
            .addField("sound_level", metric.sound_level.toString())
            .addField("reality", metric.reality.toString())
            .build())
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
                MetricCount(mutableList[1].toString().toDouble().toInt()
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
                FieldAverage(mutableList[1].toString().toDouble()
                )
            }[0]
    }

    fun createBillboardStatus(billboardStatus: BillboardStatus) {
        influxDB.write(dbName, "", Point.measurement(billboardStatusMeasurement)
            .time(billboardStatus.timestamp, TimeUnit.MILLISECONDS)
            .tag("billboard_id", billboardStatus.billboard_id.toString())
            .tag("health", billboardStatus.health.toString())
            .tag("ad_id", billboardStatus.ad_id.toString())
            .addField("weather", billboardStatus.weather.toString())
            .addField("temperature", billboardStatus.temperature)
            .addField("sound_level", billboardStatus.sound_level)
            .build())
    }

    fun getLastMetricRecord(): Metric {
        val query = Query(
            createQueryLastRecord(metricsMeasurement),
            dbName
        )
        val results = influxDB.query(query)
            .results
        if (results.first().series == null) {
            return Metric()
        }
        return results.first().series.first().values
            .map { mutableList ->
                Metric(convertDateToLong(mutableList[0].toString()),
                    mutableList[1].toString().toLong(),
                    mutableList[2].toString().toLong(),
                    mutableList[3].toString().toLong(),
                    mutableList[4].toString().toLong(),
                    Gender.valueOf(mutableList[5].toString()),
                    Reality.valueOf(mutableList[6].toString()),
                    mutableList[7].toString().toLong(),
                    mutableList[8].toString().toLong(),
                    Weather.valueOf(mutableList[9].toString())
                )
            }[0]
    }

    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        df.timeZone = TimeZone.getTimeZone("UTC")
        return df.parse(date).time
    }


}