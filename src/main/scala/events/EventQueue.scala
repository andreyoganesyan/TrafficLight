package events

class EventQueue(events: List[Event]) {

  def addEvent(event: Event): EventQueue = {

    def addEventToList(eventList: List[Event], reversedHeads: List[Event] = Nil): List[Event] = eventList match {
      case Nil => (event :: reversedHeads).reverse
      case head :: tail =>
        if (head.time <= event.time)
          addEventToList(tail, head :: reversedHeads)
        else
          reversedHeads.reverse ::: event :: eventList
    }

    new EventQueue(addEventToList(events))
  }

  def nextEventTime: Double = events.head.time

  def dequeueEvent: (Event, EventQueue) = (events.head, new EventQueue(events.tail))

  def isEmpty: Boolean = events.isEmpty

  def nonEmpty: Boolean = events.nonEmpty

}
