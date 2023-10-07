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
    val dir = "input/"
    val graph_path = "NetGameSimfileName.ngs"
    val graph_per_path = "NetGameSimfileName.ngs.perturbed"
    val graph = NetGraph.load(fileName=s"$dir$graph_path","")
    val graphPer = NetGraph.load(fileName=s"$dir$graph_per_path","")
    graph match{
      case Some(graph)=>
        logger.info("Normal Graph opened")
        graphPer match{
          case Some(graphPer)=>{
            logger.info("Perturbated graph opened")
            val myResultA: mutable.Map[(Int, Int), Double] = mutable.Map()
            val myResultB: mutable.Map[(Int, Int), Double] = mutable.Map()
            val ranking = scala.collection.mutable.Set[MyResult]()
            val perturbatedNodes = graphPer.sm.nodes()
            val nodes = graph.sm.nodes()
            logger.info("\n\n ------------ Sharding ------------ \n\n\n")

            //resultAList.foreach(item => {
            //  val bufList = generateCombinations(item, resultBList)
            //  writeToFile(bufList)
            //})
            nodes.forEach(node => {
              val input_nodes = generateCombinatioV2(node, perturbatedNodes.asScala, graph, graphPer)
              writeToFile(input_nodes)
              increaseIndex()
            })
            logger.info("\n\n\n ------------ Map Reduce ------------ \n\n\n ")
            val conf = new Configuration()
            val job = Job.getInstance(conf, "MyMapReduceJob")
            job.setMapperClass(classOf[MyMapper])
            job.setReducerClass(classOf[MyReducer])
            job.setOutputKeyClass(classOf[IntWritable])
            job.setOutputValueClass(classOf[DoubleWritable])
            job.setInputFormatClass(classOf[TextInputFormat])
            job.setOutputFormatClass(classOf[TextOutputFormat[IntWritable, DoubleWritable]])
            // Set the input path to the directory containing your data files
            val fs = FileSystem.get(conf)
            val output = new Path("mapper_output/")
            if (fs.exists(output)) {
              fs.delete(output, true)
            }
            FileInputFormat.addInputPath(job, new Path("mapper_input/"))
            FileOutputFormat.setOutputPath(job,output)
            // Submit the job and run it
            job.waitForCompletion(false)


            val reducerOutputPath = "mapper_output/part-r-00000"
            val yamlFilePath = "input/NetGameSimfileName.ngs.yaml"
            val inputText = Source.fromFile(reducerOutputPath).mkString
            val yamlText = Source.fromFile(yamlFilePath).mkString
            Source.fromFile(reducerOutputPath).close()
            Source.fromFile(yamlFilePath).close()
            val result = Parser.parseYaml(inputText,yamlText)
            val BTL = result._1 + result._2
            val GTL = result._3 + result._4
            val RTL = BTL + GTL
            val ACC = result._4/RTL
            val BTLR = result._2/RTL
            val VPR = (GTL - BTL)/(2 * RTL) + 0.5
            logger.info(s"Bad Traceability Links(BTL): $BTL")
            logger.info(s"Good Traceability Links (GTL): $GTL")
            //logger.info(s"$RTL")
            logger.info(s"Accuracy: $ACC")
            logger.info(s"BRTL: $BTLR")
            logger.info(s"Algorithm’s precision ratio: $VPR")
          }case None =>{
            logger.error("Error opening perturbated graph")
          }
        }
      case None =>{
        println("Error opening normal graph")
        return
      }
    }
  }

}