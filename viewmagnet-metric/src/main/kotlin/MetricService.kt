import org.influxdb.InfluxDB
import org.influxdb.dto.Point
import org.influxdb.dto.Query
import java.util.concurrent.TimeUnit

class MetricService(private val influxDB: InfluxDB,
                        private val dbName: String = "metrics") {

    private val aggregateQuery = """ SELECT count(human_count) as count, sum(human_count) as sum, min(human_count) as min, max(human_count) as max FROM metrics """

    init {
        this.influxDB.query(Query("CREATE DATABASE "+dbName, ""))
    }

    fun create(statistic: Metric): Int {
        influxDB.write(dbName, "", Point.measurement("metrics")
            .time(statistic.timestamp, TimeUnit.MILLISECONDS)
            .addField("ad_id", statistic.ad_id)
            .addField("billboard_id", statistic.billboard_id)
            .addField("human_count", statistic.human_count)
            .addField("timestamp", statistic.timestamp)
            .build())
        return 200
    }

    fun aggregated(): Total {
        val query = Query(
            aggregateQuery,
            dbName
        )
        val results = influxDB.query(query)
            .results
        if (results.first().series == null) {
            return Total(0.0, 0.0, 0.0, 0.0)
        }
        return results.first().series.first().values
            .map { mutableList ->
                Total(mutableList[1].toString().toDouble(),
                    mutableList[2].toString().toDouble(),
                    mutableList[3].toString().toDouble(),
                    mutableList[4].toString().toDouble()
                )
            }[0]
    }

}