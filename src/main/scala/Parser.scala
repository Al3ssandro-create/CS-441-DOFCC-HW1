package com.lsc
import NetGraphAlgebraDefs.NodeObject
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import scala.collection.JavaConverters._
object Parser {
  /*
  * This method allow to parse the data contains in the shard into the right one recreating the Node initially sent*/
  def parse(base: String): NodeObject = {
    //println(base)
    val pattern = """\((.*?)\)""".r
    pattern.findFirstMatchIn(base).map(_.group(1)) match
      case Some(value) => {
        val param = value.split(",")
        NodeObject(param(0).toInt, param(1).toInt, param(2).toInt, param(3).toInt, param(4).toInt, param(5).toInt, param(6).toInt, param(7).toInt, param(8).toDouble)
      }
      case None => new NodeObject(0, 0, 0, 0, 0, 0, 0, 0, 0.0)

  }
  /*
  * Compute the result of the similarity between two nodes
  */
  def result(first : List[NodeObject], second : List[NodeObject]): Double = {
    if(first(0) == second(0)){
      1.0

    }else {
      val maxLenght = math.min(first.size, second.size)
      val comparison: Seq[Double] = (1 until maxLenght).map { i =>
        val elem1 = first.drop(i).headOption
        val elem2 = second.drop(i).headOption
        (elem1, elem2) match {
          case (Some(element1), Some(element2)) =>
            compare(element1, element2)
          case _ =>
            0
        }
      }
      val tot = comparison.sum / maxLenght
      tot
    }
  }

  def compare(first: NodeObject, second: NodeObject): Double = {
    val comparisons = List(
      first.children == second.children,
      first.propValueRange == second.propValueRange,
      first.maxDepth == second.maxDepth,
      first.maxBranchingFactor == second.maxBranchingFactor,
      first.maxProperties == second.maxProperties,
      first.storedValue == second.storedValue
    )
    val tot = comparisons.foldLeft(0) { (acc, isEqual) =>
      if (isEqual) acc + 1 else acc
    }
    tot / 6.0
  }
  /*
  * Parse the given YAML
  */
  def parseYaml(reducerText : String, yamlText : String): (Double,Double,Double,Double) = {
    val logger = LoggerFactory.getLogger(getClass)

    logger.info("Extracting result from the reducer")
    val numbersFromFile = reducerText.split("\n").map(_.split("\t")(0).toInt).toSet
    logger.info("Extracting result of the yaml")
    val yaml = new Yaml()

    val parsedYaml = yaml.load(yamlText.replaceAll("\t"," " * 4)).asInstanceOf[java.util.Map[String, Any]].asScala

    val nodesMap = parsedYaml("Nodes").asInstanceOf[java.util.Map[String, Any]].asScala
    val modifiedValues = nodesMap("Modified").asInstanceOf[java.util.List[Int]].asScala.toSet
    val removedValues = nodesMap("Removed").asInstanceOf[java.util.List[Int]].asScala.toSet
    val allYamlNumbers = modifiedValues ++ removedValues
    logger.info("Comparing files")
    val CTL : Double = allYamlNumbers.diff(numbersFromFile).size
    val WTL : Double = numbersFromFile.diff(allYamlNumbers).size
    val DTL : Double = (1 to 300).toSet.diff(numbersFromFile ++ allYamlNumbers).size
    val ATL : Double = numbersFromFile.intersect(allYamlNumbers).size
    (CTL, WTL, DTL, ATL)
  }

}