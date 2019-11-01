package com.etrans.demo.expr

class CollectionUseCase {

}

object CollectionUseCase extends App {
  val set = Set(1,2,1)
  val list = List(1,2,1,4,5,3,1,1)


  // 粗写
  var list1 = list.map((a:Int)=> {
    (a+1)
  })

  // 简写
  list1 = list.map((a) => {a+1}) // 只有一个参数的时候可以吧 类型定义去掉

  // 简写
  list1 = list.map( a => {a+1})  // 只有一个参数的时候可以吧 括号去掉

  // 简写
  list1 = list.map(_+1)          // 只有一个参数可以用_替代

  list.map(_*2).foreach(println(_))
  set.map(_*2).foreach(println(_))

  val student = ("lucy","23")
  println(s"name = ${student._1} age = ${student._2}")

  val map = Map(1->"a",2->"b",3->"c");
  var value0 = map.getOrElse(1, None) // 直接返回值或者默认值
  var valueget = map.get(1) // map get方法返回 Option包装的对象


  println("map  0 is "+ value0)
  map.map(s => ("map :" + s._2,s._1)).foreach(s =>
    {println(s"k = ${s._1}, v = ${s._2}")}
  )

  // filter 操作
  val l = List(1,2,3,4,5,6,7,8,9)

  // 找出所有偶数
  l.filter(_%2==0).foreach(println(_))

  // zip 用法
  val a = List(1,2,3,4)
  val b = List(3,4,2)

  a.zip(b).foreach(println(_))  // zip 结果的长度等于最短的长度，谁放前面，则他的元素在第一位
  b.zip(a).foreach(println(_))

  var pationtuple = l.partition(_%3==0) // 整除3的放一个，另外的放其他，只能分两个list
  println(pationtuple)

  // fatten 能把 List(List("a","b"),List("a","b","c")) 整合成一个List("a","b","a","b","c")
  var listfatten = List(List("a","b"),List("a","b","c"))
  listfatten.flatten.foreach(println(_))

  // a.zip(b).flatten 编译不通过  a.zip(b) 结果结果为 List(tuple,tuple)
  // list.flatten.foreach(println(_)) 编译报错，只有List(lista,listb)才行，List() 不行

  // fatmap 是先遍历元素 map 再 fatten
  var listInt = List(List(1,2),List(1,2,3))
  val fatmaplist = listInt.flatMap(elem => elem.map(_*2))



}
