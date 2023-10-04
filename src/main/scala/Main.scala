import FileManager.{increaseIndex, writeToFile}
import MapReduce.{MyMapper, MyReducer}
import NetGraphAlgebraDefs.*
import SimRank.*
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{DoubleWritable, IntWritable, Text}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, TextInputFormat}
import org.apache.hadoop.mapreduce.lib.output.{FileOutputFormat, TextOutputFormat}

import scala.jdk.CollectionConverters.*
import scala.collection.*
import scala.collection.mutable.ListBuffer
object Main {
  case class MapContainer(map1: mutable.Map[ (Int, Int), Double], map2: mutable.Map[(Int, Int), Double])

  def generateCombinations(item: ((Int, Int), Double), list2: List[((Int, Int), Double)]) : ListBuffer[(mutable.Map[(Int, Int), Double], mutable.Map[(Int, Int), Double])]  = {
    val combinationsList: ListBuffer[(mutable.Map[(Int, Int), Double], mutable.Map[(Int, Int), Double])] = ListBuffer()
    val x = item._1
    val z = item._2
    list2.foreach { (node) =>
      val combination = (mutable.Map(item._1 -> item._2), mutable.Map(node._1 -> node._2))
      combinationsList += combination
    }
    return combinationsList
  }

  def generateCombinatioV2(item1: NodeObject, nodeSetPer: Set[NodeObject], graph: NetGraph, graphPer: NetGraph):  Set[((Int,Int, Set[NodeObject]), (Int,Int, Set[NodeObject]))] = {
    val combinations = mutable.Set[((Int,Int, Set[NodeObject]), (Int,Int, Set[NodeObject]))]()
    val pred1 = graph.sm.predecessors(item1).asScala
    nodeSetPer.foreach(item2 => {
      val pred2 = graphPer.sm.predecessors(item2).asScala
      val combination = ((0,item1.id,pred1),(1,item2.id,pred2))
      combinations += combination
    })
    return combinations

  }



  def main(args: Array[String]): Unit = {
    val dir = "input/"
    val graph_path = "NetGameSimfileName.ngs"
    val graph_per_path = "NetGameSimfileName.ngs.perturbed"
    val graph = NetGraph.load(fileName=s"$dir$graph_path","")
    val graphPer = NetGraph.load(fileName=s"$dir$graph_per_path","")
    graph match{
      case Some(graph)=>{
        graphPer match{
          case Some(graphPer)=>{
            val myResultA: mutable.Map[(Int, Int), Double] = mutable.Map()
            val myResultB: mutable.Map[(Int, Int), Double] = mutable.Map()
            val ranking = scala.collection.mutable.Set[MyResult]()
            val perturbatedNodes = graphPer.sm.nodes()
            val nodes = graph.sm.nodes()
            print("\n ------------ Preprocessing Vanilla Graph ------------ \n")
            nodes.forEach(node => {
              val result = rank(node, graph)
              //print("Node id: " + node.id + " result: " + result + "\n")
              ranking.add(new MyResult(0,node.id, result))
              myResultA.addOne((0,node.id), result)
              //print("node " + node.id + "\n\tmaxProperties: " + node.maxProperties + "\n\tprops: " + node.props + "\n\tstoredValue: " + node.storedValue + "\n\tchildren: " + node.children + "\n\tcurrentDepth" + node.currentDepth + "\n\tmaxBranchingFactor: " + node.maxBranchingFactor + "\n\tmaxDepth: " + node.maxDepth + "\n\tpropValueRange: " + node.propValueRange + "\n\n")
            })
            val resultAList = myResultA.toList
            print("\nThe preprocessing on the original graph has ended >>>\n")
            print("\n ------------ Preprocessing Perturbated Graph ------------ \n")
            perturbatedNodes.forEach(node => {
              val result = rank(node, graphPer)
              //print("Node id: " + node.id + " result: " + result + "\n")
              ranking.add(new MyResult(1, node.id, result))
              myResultB.addOne((1,node.id), result)
              //print("node " + node.id + "\n\tmaxProperties: " + node.maxProperties + "\n\tprops: " + node.props + "\n\tstoredValue: " + node.storedValue + "\n\tchildren: " + node.children + "\n\tcurrentDepth" + node.currentDepth + "\n\tmaxBranchingFactor: " + node.maxBranchingFactor + "\n\tmaxDepth: " + node.maxDepth + "\n\tpropValueRange: " + node.propValueRange + "\n\n")
            })
            val resultBList = myResultB.toList
            print("\nThe preprocessing on the perturbated graph has ended >>>\n")
            print("\n\n ------------ Sharding ------------ \n\n\n")

            //resultAList.foreach(item => {
            //  val bufList = generateCombinations(item, resultBList)
            //  writeToFile(bufList)
            //})
            nodes.forEach(node => {
              val input_nodes = generateCombinatioV2(node, perturbatedNodes.asScala, graph, graphPer)
              writeToFile(input_nodes)
              increaseIndex()
            })
            print("\n\n\n ------------ Map Reduce ------------ \n\n\n ")
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
          }case None =>{
            println("Error")
            return
          }
        }
      }
      case None =>{
        println("Error")
        return
      }
    }
  }

}