package com.uebercomputing

import org.apache.spark.sql.DataFrameWriter
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession

object DataExplorer {

  val RecordsUrl = "file:///datasets/github/data"

  def process(spark: SparkSession): (Long, Long) = {
    val records = spark.read.json(RecordsUrl)
    records.printSchema()

    records.cache()
    val totalEventCount = records.count()

    records.select("type").distinct().
      show(numRows = 100, truncate = false)

    val prsCommon = records.where(records("type") === "PullRequestEvent")
    val pullRequestEventCount = prsCommon.count()

    import spark.implicits._

    val typeCounts = records.groupBy($"type").count.orderBy($"count".desc)
    typeCounts.show(truncate = false)

    records.unpersist()

    val texts = spark.read.text(RecordsUrl)
    val prsText = texts.where($"value".contains("PullRequestEvent"))
    val reparteds = prsText.repartition(2)
    reparteds.write.text("file:///datasets/github/prs")

    val prs = spark.read.json("file:///datasets/github/prs")

    import org.apache.spark.sql.functions._

    val ymdhPrs = prs.withColumn("year", year($"created_at")).
      withColumn("month", month($"created_at")).
      withColumn("day", dayofmonth($"created_at")).
      withColumn("hour", hour($"created_at"))

    ymdhPrs.write.partitionBy("year","month","day","hour").
      parquet("file:///datasets/github/prs-ymdh")

    (totalEventCount, pullRequestEventCount)
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().
      appName("HelloSparkWorld").
      getOrCreate()
    val (total, prs) = process(spark)
    println(s"Total event records: ${total}, pr events: ${prs}.")
    spark.stop()
  }
}