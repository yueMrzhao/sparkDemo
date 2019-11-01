package com.etrans.demo.listenerbus

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

import scala.util.DynamicVariable

class LiveListenerBus {

  private val queues = new CopyOnWriteArrayList[MonoEventQueue]()

  private val started = new AtomicBoolean(false)

  def start = {
    if (started.compareAndSet(false,true)){
      var iterator = queues.iterator()
      while (iterator.hasNext){
        iterator.next().start
      }
    }
  }

  def stop = {
    if (started.compareAndSet(true,false)){
      var iterator = queues.iterator()
      while (iterator.hasNext){
        iterator.next().stop
      }
    }
  }

  def addToQueue(queue: String, listener : MongoLister) :Unit = synchronized{
    if (started.get()){
      var iterator = queues.iterator()
      var isContain = false
      while (iterator.hasNext){
        var next = iterator.next()
        if (next.name == queue) {
          isContain = true
          next.register(listener)
        }
      }

      if (!isContain) {
        val mongoQueue = new MonoEventQueue(queue)
        mongoQueue.register(listener)
        queues.add(mongoQueue)
      }
    }
  }

  def post(event: MongoEvent): Unit ={
    if (started.get){
      var iterator = queues.iterator()
      while (iterator.hasNext){
        iterator.next().post(event)
      }
    }
  }

}

private[listenerbus] object LiveListenerBus {
  val withinListenerThread: DynamicVariable[Boolean] = new DynamicVariable[Boolean](false)
}
