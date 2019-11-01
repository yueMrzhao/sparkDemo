package com.etrans.demo.expr

class Trait {

}

trait UseTrait {
  def log(msg: String): Unit = {
    println(s"log: $msg")
  }

  def logReserse(msg: String)
}

class SubUseClass extends UseTrait {
  def subLog: Unit = {
    log("hello,everyone!")
  }

  override def log(msg: String): Unit = { // 子重新父类以实现的方法时，要带上override
    println(s"sublog: $msg")
  }

  def logReserse(msg: String): Unit = {
    println(s"subLogReserse:${msg.reverse}")
  }
}

trait SubUseTrait extends UseTrait {
  def subLog: Unit = {
    log("hello,everyone!")
  }

  override def log(msg: String): Unit = { // 子重新父类以实现的方法时，要带上override
    println(s"sublog: $msg")
  }

  def logReserse(msg: String): Unit = {
    println(s"subLogReserse:${msg.reverse}")
  }
}

trait SubUseTraiter extends SubUseTrait {
  override def log(msg:String): Unit = {
    println(s"subloger: $msg")
  }
}

abstract class Account {
  def check
}

class Test1 extends Account with SubUseTrait {
  def test(): Unit ={
    logReserse("hello,everyone!")
  }

  def check: Unit = {
    log("remain: $6000")
  }
}



object Test extends App {
  val subUseClass = new SubUseClass
  subUseClass.subLog

  var test = new Test1
  test.test
  test.check

  var test1 = new Test1 with SubUseTraiter // 对象混用 trait 会覆盖之前的继承
//  test.test
  test1.check
}
