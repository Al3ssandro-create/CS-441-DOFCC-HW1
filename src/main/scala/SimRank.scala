import NetGraphAlgebraDefs._

import scala.util._
object SimRank {

  val threshold = 0.6
  def rank(nodeA : NodeObject, graph : NetGraph): Double = {
    val predecessor = graph.sm.successors(nodeA)
    val successor = graph.sm.predecessors(nodeA)
    var result = 0.0
    predecessor.forEach(node => {
      result += (0.1 * node.maxProperties + 0.3 * node.maxDepth + 0.5 * node.storedValue + 0.3 * node.maxBranchingFactor + 0.05 * node.propValueRange) / (node.maxProperties + node.maxDepth + node.storedValue + node.maxBranchingFactor + node.propValueRange)
    })
    successor.forEach(node => {
      result += (0.1 * node.maxProperties + 0.3 * node.maxDepth + 0.5 * node.storedValue + 0.3 * node.maxBranchingFactor + 0.05 * node.propValueRange) / (node.maxProperties + node.maxDepth + node.storedValue + node.maxBranchingFactor + node.propValueRange)
    })
    return result
  }
}
