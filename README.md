# prez-spark-dataengineering

Repository for Data Engineering with Apache Spark for Baltimore Scala Meetup, May 2nd, 2019.

## Data source - https://www.gharchive.org/

* Hourly download since 1/1/2015 using GitHub Event API - all events. About 20-40MB/hour * 37900 hours ~1TB.

We'll be working with the following subset:

```bash
wget http://data.gharchive.org/2019-04-28-0.json.gz
wget http://data.gharchive.org/2019-04-28-1.json.gz
wget http://data.gharchive.org/2019-04-28-13.json.gz
```

## Mission: Allow data scientists to explore pull requests for anomalies
* [GitHub Developer PullRequestEvent](https://developer.github.com/v3/activity/events/types/#pullrequestevent)
* Out of all events, store just pull requests using efficient storage format
* Allow for fast time-based access when specifying yyyy, mm, dd, hh (or prefix combination)
* Along the way, learn about Spark batch processing

## Presentation and Code
* [Data Engineering with Apache Spark](https://github.com/medale/prez-spark-dataengineering/blob/master/presentation/SparkDataEngineering.pdf)
* [Speaker notes for presentation](https://github.com/medale/prez-spark-dataengineering/blob/master/presentation/SpeakerNotes.pdf)