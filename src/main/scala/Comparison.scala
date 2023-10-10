import com.lsc.Parser

import scala.io.Source
/*
* This class is the class able to compute all the needed statistic
*/
object Comparison {
  def main(args: Array[String]): Unit =
      val reducerOutputPath = "./output/part-r-00000"
      val yamlFilePath = "./input/NetGameSimfileName.ngs.yaml"
      val inputText = Source.fromFile(reducerOutputPath).mkString
      val yamlText = Source.fromFile(yamlFilePath).mkString
      Source.fromFile(reducerOutputPath).close()
      Source.fromFile(yamlFilePath).close()
      val result = Parser.parseYaml(inputText, yamlText)
      val BTL = result._1 + result._2
      val GTL = result._3 + result._4
      val RTL = BTL + GTL
      val ACC = result._4 / RTL
      val BTLR = result._2 / RTL
      val VPR = (GTL - BTL) / (2 * RTL) + 0.5
      println(s"Bad Traceability Links(BTL): $BTL")
      println(s"Good Traceability Links (GTL): $GTL")
      //logger.info(s"$RTL")
      println(s"Accuracy: $ACC")
      println(s"BRTL: $BTLR")
      println(s"Algorithm’s precision ratio: $VPR")
}
