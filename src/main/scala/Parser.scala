import NetGraphAlgebraDefs.NodeObject
object Parser {
  def parse(base: String): NodeObject = {
    //println(base)
    val pattern = """\((.*?)\)""".r
    pattern.findFirstMatchIn(base).map(_.group(1)) match
      case Some(value) => {
        val param = value.split(",")
        /*println("0 " + param(0))
        println("1 " + param(1))
        println("2 " + param(2))
        println("3 " + param(3))
        println("4 " + param(4))
        println("5 " + param(5))
        println("6 " + param(6))
        println("7 " + param(7))
        println("8 " + param(8))*/
        new NodeObject(param(0).toInt, param(1).toInt, param(2).toInt, param(3).toInt, param(4).toInt, param(5).toInt, param(6).toInt, param(7).toInt, param(8).toDouble)
      }
      case None => new NodeObject(0, 0, 0, 0, 0, 0, 0, 0, 0.0)

  }
  def result(first : Set[NodeObject], second : Set[NodeObject]): Double = {
    val maxLenght = math.min(first.size, second.size)
    val comparison: Seq[Double] = (0 until maxLenght).map { i =>
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

}