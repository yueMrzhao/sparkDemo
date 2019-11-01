package com.etrans.demo.expr

class ApplyUse {

  def speak: Unit = {
    println("say what?")
  }
}

object ApplyUse {

  def apply(): ApplyUse = new ApplyUse()

  var count:Int = 0

  def static: Unit = {
    println("this is the static method!")
  }

  def incr: Unit = {
    for (i <- 1 to 10) {
      count = count + 1
    }
  }
}

object Test2 extends App {
  val applyUse = ApplyUse()
  ApplyUse.static
  applyUse.speak
  ApplyUse.incr
  println(ApplyUse.count)
}