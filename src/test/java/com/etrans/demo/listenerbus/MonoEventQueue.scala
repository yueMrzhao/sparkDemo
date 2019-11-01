package com.etrans.demo.listenerbus

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

import org.apache.spark.internal.Logging


class MonoEventQueue(val name: String) extends MonoListenerBus with Logging {

  import MonoEventQueue._

  private val queue = new LinkedBlockingQueue[MongoEvent](100000)

  private val started = new AtomicBoolean(false)

  private val dispatchThread = new Thread(s"mongo-group-save-${name}") {
    setDaemon(true)
    override def run() = {dispatch()}
  }

  private def dispatch(): Unit = LiveListenerBus.withinListenerThread.withValue(true){
    var next = queue.take()

    while (next != MONGOEVENT ){
      postToAll(next)
      next = queue.take()
    }

  }

  def start = {
    if (started.compareAndSet(false,true)){
      dispatchThread.start()
    }
  }

  private[listenerbus] def stop = {
    if (!started.get()) {
      throw new IllegalStateException(s"throw Atempted to stop thread ${name} has not started yet")
    }

    if (started.compareAndSet(true,false)){
      post(MONGOEVENT)
    }

    dispatchThread.join
  }

  def post(event: MongoEvent):Unit = {
    if (!started.get()) {
      return
    }

    queue.offer(event)
  }

}

private object MonoEventQueue{
  val MONGOEVENT = new MongoEvent() {}

}
