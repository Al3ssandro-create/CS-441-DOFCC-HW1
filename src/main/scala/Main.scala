package com.lsc
import FileManager.{increaseIndex, writeToFile}
import MapReduce.{MyMapper, MyReducer}
import NetGraphAlgebraDefs.*
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{DoubleWritable, IntWritable, Text}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, TextInputFormat}
import org.apache.hadoop.mapreduce.lib.output.{FileOutputFormat, TextOutputFormat}
import org.slf4j.LoggerFactory

import scala.jdk.CollectionConverters.*
import scala.collection.*
import scala.collection.mutable.ListBuffer
import scala.io.Source
object Main {
  /*
  * This method allow to create all the combination of nodes possible in our case
  * we are going to obtain 300 x 300 different combination 300 for each file
  */
  def generateCombinatioV2(item1: NodeObject, nodeSetPer: Set[NodeObject], graph: NetGraph, graphPer: NetGraph):  Set[((Int,Int, ListBuffer[NodeObject]), (Int,Int, ListBuffer[NodeObject]))] = {
    val combinations = mutable.Set[((Int,Int, ListBuffer[NodeObject]), (Int,Int, ListBuffer[NodeObject]))]()
    val pred1 = ListBuffer.from(graph.sm.predecessors(item1).asScala).prepend(item1)
    nodeSetPer.foreach(item2 => {
      val pred2 = ListBuffer.from(graphPer.sm.predecessors(item2).asScala).prepend(item2)
      val combination = ((0,item1.id,pred1),(1,item2.id,pred2))
      combinations += combination
    })
    combinations

  }



  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(getClass)
    //This part is commented but is necessary to create new shards, so if you need new shard de-comment this part and the one below

    /*val graph_path = "NetGameSimfileName.ngs"
    val graph_per_path = "NetGameSimfileName.ngs.perturbed"
    val graph = NetGraph.load(graph_path,args(1))
    val graphPer = NetGraph.load(graph_per_path, args(1))
    graph match{
      case Some(graph)=> {
        logger.info("Normal Graph opened")
        graphPer match {
          case Some(graphPer) => {
            logger.info("Perturbated graph opened")
            val myResultA: mutable.Map[(Int, Int), Double] = mutable.Map()
            val myResultB: mutable.Map[(Int, Int), Double] = mutable.Map()
            val ranking = scala.collection.mutable.Set[MyResult]()
            val perturbatedNodes = graphPer.sm.nodes()
            val nodes = graph.sm.nodes()
            logger.info("\n\n ------------ Sharding ------------ \n\n\n")
            nodes.forEach(node => {
              val input_nodes = generateCombinatioV2(node, perturbatedNodes.asScala, graph, graphPer)
              writeToFile(input_nodes, args(2))
              increaseIndex()
            })
            logger.info("\n\n\n ------------ Map Reduce ------------ \n\n\n ")*/
            val conf = new Configuration()

            val job = Job.getInstance(conf, "MyMapReduceJob")
            job.setJarByClass(this.getClass)
            job.setMapperClass(classOf[MyMapper])
            job.setReducerClass(classOf[MyReducer])
            job.setOutputKeyClass(classOf[IntWritable])
            job.setOutputValueClass(classOf[DoubleWritable])
            job.setInputFormatClass(classOf[TextInputFormat])
            job.setOutputFormatClass(classOf[TextOutputFormat[IntWritable, DoubleWritable]])
            // Set the input path to the directory containing your data files
            val fs = FileSystem.get(conf)
            /*If runned locally you can delete the directory every time, for local testing decomment it

            if (fs.exists(output)) {
              fs.delete(output, true)
            } */
            FileInputFormat.addInputPath(job, new Path(args(0)))
            FileOutputFormat.setOutputPath(job, new Path(args(1)))
            // Submit the job and run it
            if(job.waitForCompletion(false)){
              logger.info("The job succeed, you can grab a beer now!")
            }else{
              logger.error("The job failed, check the code!")
            }
          /*}
          case None => {
            logger.error("Error opening perturbated graph")
          }
        }
      }case None =>{
        println("Error opening normal graph")
        return
      }
    }*/
  }

}