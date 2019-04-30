package com.uebercomputing.rdd

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

/**
  */
object RddProcessor {

  val DefaultEventInputUrl = "file:///datasets/github/data"

  def process(sc: SparkContext, inputUrl: String): (Long, Long) = {
    val records = sc.textFile(inputUrl)
    val total = records.count()
    val prs = records.filter(r => r.contains("PullRequestEvent"))
    val totalPrs = prs.count()
    (total, totalPrs)
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().
      appName("RddProcessor").
      getOrCreate()

    val inputUrl = if (args.size > 0) {
      args(0)
    } else {
      DefaultEventInputUrl
    }
    process(spark.sparkContext, inputUrl)
  }
}
