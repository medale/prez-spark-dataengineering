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

# Narrow vs. wide transformations
https://jaceklaskowski.gitbooks.io/mastering-apache-spark/spark-rdd-transformations.html

# Splittable data formats

# Shuffle partitions

# Schemas

# Parquet

https://www.gharchive.org/
wget http://data.gharchive.org/2019-04-28-0.json.gz
wget http://data.gharchive.org/2019-04-28-1.json.gz
wget http://data.gharchive.org/2019-04-28-13.json.gz

store under data directory
run spark-shell from parent of data directory
(gz of .json file with one json per line)

```scala
val records = spark.read.json("data")
//slow - needs to figure out JSON schema
records.cache
records.count
//235728

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
+-----------------------------+------+                                          
|type                         |count |
+-----------------------------+------+
|PushEvent                    |119478|
|GollumEvent                  |1126  |
|ReleaseEvent                 |999   |
|CommitCommentEvent           |417   |
|CreateEvent                  |32198 |
|PullRequestReviewCommentEvent|4811  |
|IssueCommentEvent            |14749 |
|DeleteEvent                  |8585  |
|IssuesEvent                  |7783  |
|ForkEvent                    |7466  |
|PublicEvent                  |624   |
|MemberEvent                  |970   |
|WatchEvent                   |22337 |
|PullRequestEvent             |14185 |
+-----------------------------+------+


val texts = spark.read.text("data")
//fast - no schema
//texts: org.apache.spark.sql.DataFrame = [value: string]

val prsText = texts.where($"value".contains("PullRequestEvent"))
prsText.count
prsText.rdd.partitions.size
prsText.write.text("prs")

//prs directory with
//part-00000-ea8360a9-79f0-494b-a4ca-c7e05c1c7104-c000.txt  _SUCCESS
//part-00001-ea8360a9-79f0-494b-a4ca-c7e05c1c7104-c000.txt

val prs = spark.read.json("prs")
prs.printSchema

prs.groupBy("payload.action").count.show()
+--------+-----+                                                                
|  action|count|
+--------+-----+
|reopened|   82|
|  closed| 6926|
|  opened| 7177|
+--------+-----+

val schemaStr = prs.schema.treeString
import java.nio.file._
import java.nio.charset._

Files.write(Paths.get("prs.schema"), schemaStr.getBytes(StandardCharsets.UTF_8))

.describe method
```
