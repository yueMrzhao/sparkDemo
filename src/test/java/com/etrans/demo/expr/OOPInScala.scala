package com.etrans.demo.expr

/**
  * 加上private[this]修饰或者主构造函数没有加var 之后，外部不能访问
  * 所有附属构造器必须调用主构造或者附属构造器
  */
class Person {
  println("this is the primary constructor!")
  var name :String = _
  val age = 27
  private[this] val gender = "male"

  override def toString: String = {
    s"name = $name, age = $age years old, gender = $gender"
  }
}

class Man(name:String,var age :Int) {    // 主构造函数
  println("this is the primary constructor!") // 初始化会执行除方法体的所有语句
  var gender = "male"
  val University:String = "BJB"

  override def toString: String = {
    s"name = $name, age = $age years old, gender = $gender"
  }

  def this(name:String, age :Int, gender:String) {
    this(name,age)
    this.gender = gender
  }

  def this (name:String, age:Int, gender:String, ss:String){
    this(name,age,"female")
  }

}

class Student(name:String,age:Int,var major:String) extends Man(name,age) {
  override def toString: String = s"name = $name, age = $age, major = $major, University = $University"

  override val University = "primary school"

}

object OOPInScala {
  def main(args: Array[String]): Unit = {
    val person = new Person
    person.name = "jackson"
    println(person.toString)

    val man = new Man("tom", 38)
    man.age = 15
    println(man.toString)

    var studentman = new Student("jack", 10, "math")
    println(studentman.toString)

  }


}
