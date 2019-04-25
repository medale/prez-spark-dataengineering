% Speaker Notes: Data Engineering with Apache Spark
% Markus Dale, medale@asymmetrik.com
% May 2019

# Setup
* Open Spark API: https://spark.apache.org/docs/latest/api/scala/index.html

# Intro, Slides And Code
* Bio:
     * mostly Java, big data with Hadoop
     * big data with Spark, Databricks, Scala
     * Now Asymmetrik - Scala, Spark, Elasticsearch, Akka...
     * Data Engineer
* Slides: https://github.com/medale/
* Scala Spark Code Examples: https://github.com/medale/
* Also https://github.com/medale/spark-mail

# Goals

https://www.gharchive.org/
wget http://data.gharchive.org/2019-04-23-11.json.gz
wget http://data.gharchive.org/2019-04-23-12.json.gz

store under data directory
run spark-shell from parent of data directory
(gz of .json file with one json per line)

```scala
val records = spark.read.json("data")
//slow - needs to figure out JSON schema
records.cache
records.count

//huge
records.printSchema

records.select("type").distinct.show
+--------------------+                                                          
|                type|
+--------------------+
|           PushEvent|
|         GollumEvent|
|        ReleaseEvent|
|  CommitCommentEvent|
|         CreateEvent|
|PullRequestReview...|
|   IssueCommentEvent|
|         DeleteEvent|
|         IssuesEvent|
|           ForkEvent|
|         PublicEvent|
|         MemberEvent|
|          WatchEvent|
|    PullRequestEvent|
+--------------------+
records.groupBy("type").count.show(numRows=100, truncate=false)
+-----------------------------+-----+
|type                         |count|
+-----------------------------+-----+
|PushEvent                    |89193|
|GollumEvent                  |822  |
|ReleaseEvent                 |748  |
|CommitCommentEvent           |344  |
|CreateEvent                  |24596|
|PullRequestReviewCommentEvent|4264 |
|IssueCommentEvent            |12106|
|DeleteEvent                  |7139 |
|IssuesEvent                  |5948 |
|ForkEvent                    |5709 |
|PublicEvent                  |492  |
|MemberEvent                  |786  |
|WatchEvent                   |16604|
|PullRequestEvent             |11612|
+-----------------------------+-----+

val texts = spark.read.text("data")
//fast - no schema
//texts: org.apache.spark.sql.DataFrame = [value: string]

val issues = texts.where($"value".contains("IssuesEvent"))
issues.count
issues.rdd.partitions.size
issues.write.parquet("issues")

//issues directory with
//part-00000-ea8360a9-79f0-494b-a4ca-c7e05c1c7104-c000.txt  _SUCCESS
//part-00001-ea8360a9-79f0-494b-a4ca-c7e05c1c7104-c000.txt

val i2 = spark.read.json("issues")
i2.printSchema

.describe method
```
