package com.uebercomputing

import org.apache.spark.sql.DataFrameWriter
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession

object DataExplorer {

  val RecordsUrl = "file:///datasets/github/data"

  def process(spark: SparkSession): (Long,Long) = {
    val records = spark.read.json(RecordsUrl)
    records.printSchema()

    records.cache()
    val totalEventCount = records.count()

    records.select("type").distinct().
      show(numRows = 100, truncate = false)

    val prs = records.where(records("type") === "PullRequestEvent")
    val pullRequestEventCount = prs.count()

    import spark.implicits._

    val typeCounts = records.groupBy($"type").count.orderBy($"count".desc)
    typeCounts.show(truncate = false)

    records.unpersist()

    val texts = spark.read.text(RecordsUrl)
    val prsText = texts.where($"value".contains("PullRequestEvent"))
    val reparteds = prsText.repartition(2)
    reparteds.write.text("file:///datasets/github/prs")

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