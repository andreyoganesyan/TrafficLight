package misc

import scala.collection.mutable

object StatisticsCollector {
  val waitingTimeMap: mutable.Map[Long, List[Double]] = new mutable.TreeMap[Long, List[Double]]() withDefaultValue Nil

  def addTimeRecord(laneId: Long, time: Double): Unit = waitingTimeMap.update(laneId, time :: waitingTimeMap(laneId))
}
