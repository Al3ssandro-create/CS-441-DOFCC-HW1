package com.lsc
import NetGraphAlgebraDefs.NodeObject
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ParserSpec extends AnyFlatSpec with Matchers {

  "parse" should "correctly extract node properties from a string" in {
    val input = "(1,2,3,4,5,6,7,8,9.0)"
    val result = Parser.parse(input)
    result should equal(NodeObject(1, 2, 3, 4, 5, 6, 7, 8, 9.0))
  }

  "compare" should "calculate correct similarity ratio between two nodes" in {
    val node1 = NodeObject(1, 2, 3, 4, 5, 6, 7, 8, 9.0)
    val node2 = NodeObject(1, 2, 3, 4, 5, 6, 7, 8, 9.0)
    val result = Parser.compare(node1, node2)
    result should be(1.0) // since they are identical
  }
  "result" should "calculate correct result for two lists of nodes" in {
    val nodeA = NodeObject(1, 2, 3, 4, 5, 6, 7, 8, 9.0)
    val nodeB = NodeObject(1, 2, 3, 4, 5, 6, 7, 8, 9.0)

    val list1 = List(nodeA)
    val list2 = List(nodeB)

    val resultVal = Parser.result(list1, list2)
    resultVal should be(1.0) // since nodeA is equal to nodeB

  }

}
