package com.uebercomputing

import java.nio.file.Files
import java.nio.file.Paths

import com.holdenkarau.spark.testing.DatasetSuiteBase
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSpec
import org.scalatest.Matchers

class HelloSparkWorldIntegrationTest extends FunSpec
  with Matchers with DatasetSuiteBase with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    val directory = HelloSparkWorld.RecordsUrl.drop("file://".length)
    val inputPath = Paths.get(directory)
    val good = Files.exists(inputPath) && Files.isReadable(inputPath)
    if (!good) {
      cancel(s"${directory} does not exist or is not readable")
    }
    //otherwise spark does not get initialized
    super.beforeAll()
  }

  describe("process") {
    it("should get correct totals") {
      val (total, totalPrs) = HelloSparkWorld.process(spark)
      assert(total === 147374)
      assert(totalPrs === 6699)
    }
  }

}
