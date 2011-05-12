import jgo.compiler._
import parser.BlockLang
import parser.combinatorExten.TracePrintingParsers
import lexer.Scanner

import message._
import interm.codeseq._

object BlockTest {
  def main(args: Array[String]) {
    test("{ }")
    test("{ ; }")
    test("{ { } }")
    test("{ { }; { } }")
    test("{ var x int }")
    test("""
{
  var x, y int
  var s1, s2 string
  var a [10]int
  x, y = y, x
  s1 = s1 + s2
  x, y = x / y, x % y
  var u uint
  x << u
  x >> u
}""")
    test("""
{
  var x, y int //literals not yet supported in the grammar
  var s string
  if x < y {
    s = s + s
  }
  x++
  y--
}""")
    test("""
{
  var x, y int //literals not yet supported in the grammar
  var s string
  if x < y {
    s = s + s
  } else {
    s = s + s + s
  }
  x++
  y--
}""")
    test("""
{
  var x, y int //literals not yet supported in the grammar
  var s string
  y++
  y++
  y++
  for x < y {
    s = s + s
    x++
  }
  x++
  y--
}""")
    test("""
{
  var x, y int //literals not yet supported in the grammar
  var s string
  y++
  y++
  y++
  for x < y {
    if x + x > y {
      s1 := s + s
      s := s1
    } else {
      s = s + s + s
    }
    x++
  }
  x++
  y--
}""")
    test("""
{
  var x, y int //literals not yet supported in the grammar
  b0 := x < y
  
  b1 := x < y && x < y
  b2 := x < y || x < y
  
  ba := x < y && x < y && x < y
  bb := x < y || x < y || x < y
  
  b3 := (x < y && x < y) || x < y
  b4 := (x < y || x < y) && x < y
  
  b5 := x < y && (x < y || x < y)
  b6 := x < y || (x < y && x < y)
  
  b7 := (x < y || x < y) && (x < y || x < y)
  b8 := (x < y && x < y) || (x < y && x < y)
  
  b := x < y && x > y || x == y || x != y && !(x == y)
  
  //b2 := !!!!!!!!b
}""")
    test("""
{
  var x, y int //literals not yet supported in the grammar
  var z, w int64
  var u uint
  var f float64
  var s string
  var c chan int
  var sl []int
  var ar [10]int
  f++ //can't ++ a float
  x + z //can't add values of differing types
  x << y //can't shift by signed value
  s[x:y]  
  sl[x:y] 
  ar[x:y] 
  s[x]    
  sl[x]   
  ar[x]   
  x = <-c
  x
  c <- y
  x <- y //x is not a chan
  <-x    //not a chan
  var (
    cSend  chan<- int  = c
    cRecv  <-chan int  = c
    cSendC chan<- chan int
    cc     chan chan int
  )
  <-<-cc
  cc <- <-cc
  cSendC <- c
  c = <-cSendC //bad
  cSend <- x
  x = <-cSend //bad
  cRecv <- x //bad
  x = <-cRecv
}""")
  }
  
  def test(in: String) {
    try {
      println("testing: " + in)
      println()
      
      val sc = Scanner(in)
      print("tokenization: ")
      sc foreach { t => print(t + " ") }
      println()
      println()
      
      val bl = new BlockLang(sc) //with TracePrintingParsers
      bl.result match {
        case ns: bl.NoSuccess =>
          println("syntax error:\n" + ns)
        
        case bl.Success(outM, _) =>
          if (outM.isDefined)
            println("\n" + outM.get.listing)
          else {
            println("compilation errors:")
            outM.errors foreach { err => println(err.longString) }
          }
      }
    }
    catch {
      case e =>
        println("exception: " + e)
        e.printStackTrace()
    }
  }
}
