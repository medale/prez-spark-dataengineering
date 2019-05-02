---
header-includes:
 - \usepackage{fvextra}
 - \DefineVerbatimEnvironment{Highlighting}{Verbatim}{breaklines,commandchars=\\\{\}}
 - \usepackage{fontspec}
 - \usepackage{setspace}
title: Data Engineering with Apache Spark
author: Markus Dale, medale@asymmetrik.com
date: May 2019
---

# Intro, Slides And Code
* Slides: https://github.com/medale/prez-spark-dataengineering/blob/master/presentation/SparkDataEngineering.pdf
* Scala Spark Code Examples: https://github.com/medale/prez-spark-dataengineering

# Data Science Mission

\Large
* ID malicious GitHub Pull Requests?
* Source: https://www.gharchive.org/

# Data Engineering Mission
* https://www.gharchive.org/
     * 2/12/2011-12/31/2014 Timeline API (now deprecated).
     * From 1/1/2015 to now Events API.
     * Since 2015: About 20-40MB/hour * 37900 hours ~1TB 
* Store: Fast, time-based access when specifying yyyy, mm, dd, hh (or prefix combination)
* Side effect: Learn more about Spark batch processing
     
# Data engineering 

![](graphics/DataEngineering.png)

# Apache Spark - Big data tooling

![Swiss Army Knife for Big Data](graphics/SwissArmyKnife.png)

# Apache Spark: Data engineering on small dataset

![Laptop](graphics/Laptop.png)

# Apache Spark: Data engineering for larger dataset (Vertical Scaling)

![Beefed-up Server](graphics/VerticalScaling.png){height=80%}

# Apache Spark: Data engineering for large datasets (Horizontal Scaling)

![Multiple cooperating Servers](graphics/HorizontalScaling.jpg){height=80%}

# Cluster Manager - Manage cores, memory, special capabilities

![](graphics/ClusterManagers.png)

# Anatomy of a Spark Application

![](graphics/SparkApplication.png)
\tiny Source: Apache Spark website  
     
# Hello, Spark World!

\scriptsize
```scala
import org.apache.spark.sql.SparkSession

object HelloSparkWorld {

  val RecordsUrl = "file:///datasets/github/data"

  def process(spark: SparkSession): (Long,Long) = {
    val records = spark.read.json(RecordsUrl)
    val totalEventCount = records.count()
    val prs = records.where(records("type") === "PullRequestEvent")
    val pullRequestEventCount = prs.count()
    (totalEventCount, pullRequestEventCount)
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("HelloSparkWorld").getOrCreate()
    val (total, prs) = process(spark)
    println(s"Total events: ${total}, pr events: ${prs}.")
    spark.stop()
  }
}
```

# SparkSession - Gateway to the Cluster

\Large
```scala
def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().
        appName("HelloSparkWorld").
        getOrCreate()
    ...
    spark.stop()
```

# API - SparkSession Object

![](graphics/ApiSparkSession.png)

# API - SparkSession Class 

```scala
//spark.read - DataFrameReader
val records: DataFrame = spark.read.json(RecordsUrl)
```

# spark.read: DataFrameReader - Input
\Large
* csv
* json
* parquet
* text - DataFrame with "value" column
* textFile - Dataset\[String\]
* Third party: 
     * https://spark-packages.org: Avro, Redshift, MongoDB...
     * Spark Cassandra Connector (DataStax github)

# DataFrame = Dataset\[Row\]

\large
```scala
val records: DataFrame = spark.read.json(RecordsUrl)
```

# DataFrame Schema

\scriptsize
```scala
scala> records.printSchema
root
 |-- actor: struct (nullable = true)
 |    |-- display_login: string (nullable = true)
 |    |-- id: long (nullable = true)
   ...
 |-- created_at: string (nullable = true)
 |-- id: string (nullable = true)
 |-- payload: struct (nullable = true)
 |    |-- comment: struct (nullable = true)
 |    |    |-- body: string (nullable = true)    
   ...
 |-- public: boolean (nullable = true)
 |-- repo: struct (nullable = true)
 |    |-- id: long (nullable = true)
 |    |-- url: string (nullable = true)
 |-- type: string (nullable = true)
```

# GitHub Data

```bash
cd /datasets/github/data
wget http://data.gharchive.org/2019-04-28-0.json.gz
wget http://data.gharchive.org/2019-04-28-1.json.gz
wget http://data.gharchive.org/2019-04-28-13.json.gz
```

# Preliminary Exploration

```bash
# creates 2019-04-28-0.json
gunzip 2019-04-28-0.json.gz

# 95865
wc -l 2019-04-28-0.json

# open in editor - 1 JSON per line
```

# One JSON per line

![](graphics/OneJsonPerLine.png)

# Pretty Print?

```bash

# default 1000 lines - xaa, xab
split 2019-04-28-0.json
mkdir temp
cd temp

# 1 file per line
split -1 ../xaa

# xac's a PullRequestEvent
python -m json.tool < xac > pretty.json
```

# Open pretty.json in Atom - PullRequestEvent 

![](graphics/PullRequestEvent.png)

# Starting Spark Standalone Cluster Manager

\small
```bash
# Start on master
$SPARK_HOME/sbin/start-master.sh --host 192.168.1.232

# Start one or more workers
$SPARK_HOME/sbin/start-slave.sh spark://192.168.1.232:7077
```

# Spark Standalone Cluster Manager UI - idle

![](graphics/SparkStandaloneUi.png)

# Running spark-shell in cluster

```bash
spark-shell --master spark://192.168.1.232:7077 \
 --driver-memory 1g \
 --executor-memory 2g \
 --total-executor-cores 4 \
 --executor-cores 2 \
 --jars /tmp/dataset-0.9.0-SNAPSHOT-fat.jar
```

# Spark Shell Startup

![](graphics/SparkShell.png)

# Spark Standalone Cluster Manager - 1 running application

![](graphics/SparkStandaloneSparkShell.png)

# HelloSparkWorld in spark-shell

```scala
scala> import com.uebercomputing.HelloSparkWorld
import com.uebercomputing.HelloSparkWorld

scala> HelloSparkWorld.process(spark)
res0: (Long, Long) = (147374,6699)
```

# Spark Application UI - Jobs, stages, tasks

![](graphics/UiHelloWorldJobs.png)

# Job - n lazy transformations, 1 action

\small
```scala
//job 0 - list files, infer schema
val records = spark.read.json("file:///datasets/github/data")
//transformation
records.cache()
//action - job 1
val totalEventCount = records.count()

//transformation - datasets are immutable!
val prs = records.where(records("type") === "PullRequestEvent")
//action - job 2    
val pullRequestEventCount = prs.count()
```

# Job 0 - Stages, tasks, partitions

![](graphics/UiJobsHeader.png){height=50%}
![](graphics/UiJobs01.png){height=50%}

# Job 0 - Stage 0, tasks, partitions

![](graphics/UiStagesHeader.png){height=50%}
![](graphics/UiStage0.png){height=50%}

# Input partitions - splittable file?

![](graphics/SparkRdd.png)

# Job 1 - count

![](graphics/UiJobsHeader.png){height=50%}
![](graphics/UiJobs01.png){height=50%}

# Job 1 - count Stages 1 and 2

![](graphics/UiStagesHeader.png){height=50%}
![](graphics/UiStages12.png){height=50%}

# Job 1 - Stages 1 and 2 DAG

![](graphics/UiJob1Dag.png){height=95%}

# Resilient Distributed Datasets (RDDs)

![](graphics/SparkRdd.png)

# RDDs - Not deprecated!

![](graphics/CodeRddOverview.png)

# And now for something completely different: Colon Cancer
* Screening saves lives! ![](graphics/Chemo.png){width=100px}
     * Colonoscopy - talk to your doc
     * [Dave Barry: A journey into my colon — and yours](https://www.miamiherald.com/living/liv-columns-blogs/dave-barry/article1928847.html)
* [Colorectal Cancer Alliance](https://www.ccalliance.org/)

# Questions?

![](graphics/Farley.png){width=200px}

* medale@asymmetrik.com
* https://github.com/medale/prez-spark-dataengineering/blob/master/presentation/SparkDataEngineering.pdf