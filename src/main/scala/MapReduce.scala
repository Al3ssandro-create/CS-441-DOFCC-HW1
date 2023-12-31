package com.lsc
import NetGraphAlgebraDefs.NodeObject
import org.apache.hadoop.io.{DoubleWritable, IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.{Mapper, Reducer}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*
object MapReduce {
  /*
  * My implementation of the map method, this method first parse for each line of the file the content, and then compute and write the result as value in
  * the <key,value> tuple that will be passed to the reducer the key is going to indicates the id of the nodes we have compared with the others
  */
  class MyMapper extends Mapper[LongWritable, Text, IntWritable, DoubleWritable] {
    override def map(
                      key: LongWritable,
                      value: Text,
                      context: Mapper[LongWritable, Text, IntWritable, DoubleWritable]#Context
                    ): Unit = {
      val logger = LoggerFactory.getLogger(getClass)
      val line: String = value.toString
      val tuplePattern = """\((\d+),(\d+),ListBuffer\((.*?)\)\)""".r
      val parsedData = for {
        tuplePattern(first, second, nodes) <- tuplePattern.findAllMatchIn(line)
        nodeObjectsList = nodes.split(", ").map(Parser.parse).toList
      } yield {
        (first.toInt, second.toInt, nodeObjectsList)
      }

      val firstOption: Option[(Int, Int, List[NodeObject])] = parsedData.nextOption()
      val secondOption: Option[(Int, Int, List[NodeObject])] = parsedData.nextOption()
      val first = firstOption.getOrElse((0, -1, List.empty[NodeObject]))
      val second = secondOption.getOrElse(1, -1, List.empty[NodeObject])
      //logger.info(s"${first._1}${first._2}${second._2} and " +Parser.result(first._3,second._3))
      context.write(new IntWritable(first._2), new DoubleWritable(Parser.result(first._3,second._3)))
    }
  }
  /*
  * This is my implementation of the reducer, the task is simple it just have to retrieve the maximum value between the <key,value> tuples
  * that have the same key.
  */
  class MyReducer extends Reducer[IntWritable, DoubleWritable, IntWritable, DoubleWritable] {
    override def reduce(
                         key: IntWritable,
                         values: java.lang.Iterable[DoubleWritable],
                         context: Reducer[IntWritable, DoubleWritable, IntWritable, DoubleWritable]#Context
                       ): Unit = {
      val logger = LoggerFactory.getLogger(getClass)
      //logger.error("#################################key: " + key + " #####################################")
      val values_to_reduce = values.asScala
      val max_value = values_to_reduce.max 
      // Il mark ha detto che non gli interessa esattamente sapere cosa sia stato rimodsso/modificato/aggiunto
      // ovviamente se uno riesce a dirlo con certezza ci sta, altrimenti quello che vuole e' solo vedere
      // quanti nodi vengono buttati nel risultato finale di hadoop
      // se hanno un riscontro nel fime .yaml sono dei good TL, sono presenti e non hanno un riscontro, Bad TL
      // ovviamente tutto quello che non viene messo nel file e non e' nel .yaml e anche lui un Good TL
      // se invece non viene messo nel file ma e' nel .yaml allora Bad TL.

      if(max_value.get < 0.5){
        context.write(key, max_value)
      }
    }
  }
}
