import NetGraphAlgebraDefs.NodeObject

import java.io.*
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object FileManager {
  var index = 0
  val path_to_directory = "mapper_input/"

  def writeToFile(list:  collection.Set[((Int, Int, collection.Set[NodeObject]), (Int, Int, collection.Set[NodeObject]))]): Unit = {
    // Convert the ListBuffer to a string
    val directory = new File(path_to_directory)
    if (!directory.exists()) {
      directory.mkdirs()
    }
    val fileName: String = "shard_file_" + index + ".txt"
    val filePath = s"$path_to_directory$fileName"
    val writer = new PrintWriter(new File(filePath))
    val dataAsString: String = list.mkString("\n")
    try{
      writer.write(dataAsString)
    }finally{
      writer.close()
    }
    print("\nData as been written to: "+filePath + " >>>\n")
  }
  def increaseIndex(): Unit = {
    index = index+1
  }
}
