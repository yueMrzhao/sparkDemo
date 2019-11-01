package com.etrans.demo.expr

class Abstract {

}

abstract class People {
  def speak
  val name:String
  var age:Int
}

class Worker extends People {
  def speak(){
    println("man begin speak!")
  }

  val name = "rockey"
  var age  = 16

}

object Abstract extends App {
  val worker = new Worker
  worker.speak()
  println(s"name= ${worker.name}, age = ${worker.age}")

}
