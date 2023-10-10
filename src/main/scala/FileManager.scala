package com.lsc
import NetGraphAlgebraDefs.NodeObject

import java.io.*
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object FileManager {
  var index = 0
  /*
  * This method will allow me to write shard to different files it receive as input a complex tuple that will then trasformed to string and passed to the mapper as file*/
  def writeToFile(list:  collection.Set[((Int, Int, ListBuffer[NodeObject]), (Int, Int, ListBuffer[NodeObject]))], path_to_directory : String): Unit = {
    // Convert the ListBuffer to a stringpath_to_directory
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
  /*
  * Increase the index of the object in order to write different files
  */
  def increaseIndex(): Unit = {
    index = index+1
  }
}
