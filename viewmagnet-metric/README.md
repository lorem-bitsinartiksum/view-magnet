# Metric Service

Service for creating metric records on influxdb by subscribing related topics

* InfluxDB should be running with default config on the background
* [Download latest InfluxDB DockerFile image](https://github.com/docker-library/docs/tree/master/influxdb/)
* Run `sudo docker run -p 8086:8086 -v $PWD:/var/lib/influxdb influxdb`
* `MetricServiceTest` has tests that publish related topics so that MetricService writes data into measurements
* There are 4 measurements: 
    * `person`
    * `billboard_status`
    * `ad_pool`
    * `ad_duration`
* Use command line interface `influx` to connect and query to database
    *  `influx -precision rfc3339 -database viewmagnet_influx`