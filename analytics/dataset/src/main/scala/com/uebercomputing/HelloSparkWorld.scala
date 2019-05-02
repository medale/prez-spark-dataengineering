package com.uebercomputing

import org.apache.spark.sql.SparkSession

/**
  *
  */
object HelloSparkWorld {

  val RecordsUrl = "file:///datasets/github/data"

  def process(spark: SparkSession): (Long,Long) = {
    val records = spark.read.json(RecordsUrl)
    records.cache()
    val totalEventCount = records.count()

    val prs = records.where(records("type") === "PullRequestEvent")
    val pullRequestEventCount = prs.count()

    records.unpersist()
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
