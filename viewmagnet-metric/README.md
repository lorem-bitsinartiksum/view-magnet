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
    
## Grafana

* Install [Grafana](https://grafana.com/docs/grafana/latest/)
* Login on http://localhost:3000/
* Add InfluxDB as data source to Grafana
* Import dashboards as json in `grafana-dashboards` folder
* Add API key with viewer role

####There are 3 dashboards
* person
    * http://localhost:3000/d/I1eAGIrWz/person?var-ad=&var-mode=SIM
* billboard_status
    * http://localhost:3000/d/pMoo_G9Zz/billboard_status?var-billboard=&var-mode=SIM
* ad_pool
    * http://localhost:3000/d/z-RU4S9Wz/ad_pool?var-billboard=&var-mode=SIM
    
* **Requires Authorization HTTP header as "Authorization: Bearer <api_key>"**
* **`ad`, `billboard` and `mode` path variables that can also be given as empty**