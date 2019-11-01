package com.etrans.demo.listenerbus

import org.bson.Document

trait MongoEvent {
  val bulkType:String = ""
}

case class RealTimeEvent(collection: String, doc: Document, bulkeType: String) extends MongoEvent

case class HistoryEvent(Collection: String, doc: Document, bulkeType: String) extends MongoEvent

case class ReportEvent(Collection: String, doc: Document, bulkeType: String) extends MongoEvent

case class MonitorEvent(Collection: String, doc: Document, bulkeType: String) extends MongoEvent

case class OtherEvent(Collection: String, doc: Document, bulkeType: String) extends MongoEvent


trait Listener {
  def onRealTime(realTimeEvent: RealTimeEvent):Unit

  def onHistory(historyEvent: HistoryEvent):Unit

  def onReport(reportEvent: ReportEvent):Unit

  def onMonitor(monitorEvent: MonitorEvent):Unit

  def onOther(otherEvent: OtherEvent):Unit

  final def save(mongoEvent: MongoEvent):Unit = {
    mongoEvent.toString match {
      case "insert" => {}
      case "update" => {}
      case "delete" => {}
      case "replace"=> {}
      case _ => {}
    }
  }
}

class MongoLister extends Listener {
  override def onRealTime(realTimeEvent: RealTimeEvent): Unit = {
    save(realTimeEvent)
  }

  override def onHistory(historyEvent: HistoryEvent): Unit = {
    save(historyEvent)
  }

  override def onReport(reportEvent: ReportEvent): Unit = {
    save(reportEvent)
  }

  override def onMonitor(monitorEvent: MonitorEvent): Unit = {
    save(monitorEvent)
  }

  override def onOther(otherEvent: OtherEvent): Unit = {
    save(otherEvent)
  }
}

