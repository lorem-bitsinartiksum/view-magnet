import org.influxdb.InfluxDB
import org.influxdb.dto.Point
import org.influxdb.dto.Query
import java.util.concurrent.TimeUnit

class MetricService(private val influxDB: InfluxDB,
                        private val dbName: String = "viewmagnet_influx") {

    fun createQueryAdMetricCount(ad_id: String): String {
        val query = " SELECT count(age) as count FROM metrics WHERE ad_id = '" + ad_id + "'"
        return query
    }

    init {
        this.influxDB.query(Query("CREATE DATABASE " + dbName, dbName))
    }

    fun create(metric: Metric): Int {
        influxDB.write(dbName, "", Point.measurement("metrics")
            .time(metric.timestamp, TimeUnit.MILLISECONDS)
            .tag("company_id", metric.company_id.toString())
            .tag("ad_id", metric.ad_id.toString())
            .tag("billboard_id", metric.billboard_id.toString())
            .addField("age", metric.age)
            .tag("gender", metric.gender.toString())
            .addField("weather", metric.weather.toString())
            .addField("temperature", metric.temperature)
            .addField("sound_level", metric.sound_level)
            .addField("reality", metric.reality.toString())
            .build())
        return 201
    }

    fun getAdMetricCount(ad_id: String): MetricCount {
        val query = Query(
            createQueryAdMetricCount(ad_id),
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

}