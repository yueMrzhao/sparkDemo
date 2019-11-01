package com.etrans.demo.listenerbus

import java.util.concurrent.CopyOnWriteArrayList

import org.apache.spark.internal.Logging

private[listenerbus] trait ListenerBus[L <: AnyRef,E] extends Logging{

  private val listeners = new CopyOnWriteArrayList[L]()

  final def register(listener: L): Unit = {
    listeners.add(listener)
  }

  final def postToAll(event: E) = {

    val iterator = listeners.iterator()
    while(iterator.hasNext){
      val next = iterator.next()
      doPostEvent(next, event)
    }

  }

  protected def doPostEvent(listener: L, event: E):Unit


}

class MonoListenerBus extends ListenerBus[MongoLister, MongoEvent] {
  protected override def doPostEvent(listener: MongoLister, event: MongoEvent): Unit = {
    event match {
      case real: RealTimeEvent =>
        listener.onRealTime(real)
      case history: HistoryEvent =>
        listener.onHistory(history)
      case report: ReportEvent =>
        listener.onReport(report)
      case monitor: MonitorEvent =>
        listener.onMonitor(monitor)
      case other: OtherEvent =>
        listener.onOther(other)
    }
  }
}
