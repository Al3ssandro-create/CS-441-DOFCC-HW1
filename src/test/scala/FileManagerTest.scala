package com.lsc
import NetGraphAlgebraDefs.NodeObject
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.ListBuffer
import java.io.File
import scala.io.Source
class FileManagerTest extends AnyFlatSpec with Matchers {

  "FileManager" should "write content to a file and increase index" in {
        val testData = Set(
        ((1, 1, ListBuffer(NodeObject(0,0,0,0,0,0,0,0,0))), (2, 2, ListBuffer(NodeObject(0,0,0,0,0,0,0,0,0)))),
        ((3, 3, ListBuffer(NodeObject(0,0,0,0,0,0,0,0,0))), (4, 4, ListBuffer(NodeObject(0,0,0,0,0,0,0,0,0))))
    )

        // Initially, the index should be 0
        FileManager.index should be (0)

        FileManager.writeToFile(testData)

        // Index should be increased
        FileManager.increaseIndex()
        FileManager.index should be (1)

        // Verify the file was written correctly
        val filePath = s"${FileManager.path_to_directory}shard_file_0.txt"
        val file = new File(filePath)
        file.exists() should be (true)

        val content = Source.fromFile(file).getLines().mkString("\n")
        content should be (testData.mkString("\n"))

        // Cleanup
        file.delete()
    }
}
