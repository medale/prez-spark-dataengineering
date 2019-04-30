package com.uebercomputing.rdd

import java.nio.file.Files
import java.nio.file.Paths

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSpec
import org.scalatest.Matchers

/**
  * Expects /datasets/github/data with:
  *
  * wget http://data.gharchive.org/2019-04-28-0.json.gz
  * wget http://data.gharchive.org/2019-04-28-1.json.gz
  * wget http://data.gharchive.org/2019-04-28-13.json.gz
  */
class RddProcessorIntegrationTest extends FunSpec with Matchers with SharedSparkContext with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    val directory = RddProcessor.DefaultEventInputUrl.drop("file://".length)
    val inputPath = Paths.get(directory)
    val good = Files.exists(inputPath) && Files.isReadable(inputPath)
    if (!good) {
      cancel(s"${directory} does not exist or is not readable")
    }
    //otherwise sc does not get initialized
    super.beforeAll()
  }

  describe("process") {
    it("should get correct totals") {
      val (total, totalPrs) = RddProcessor.process(sc, RddProcessor.DefaultEventInputUrl)
      assert(total === 147374)
      assert(totalPrs === 6699)
    }
  }
}
