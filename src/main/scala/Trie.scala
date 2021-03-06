package com.shotexa
package mutable

import collection.mutable.{Map => MutMap}

class Trie(elements: String*) {

  class Node(
      var isWord: Boolean = false,
      val children: MutMap[Char, Node] = MutMap.empty,
      val value: Option[Char], // in case of a root
      val parent: Option[Node] = None // in case of root
  )

  elements.foreach(add)
  lazy val root = new Node(value = None)

  def add(word: String): Trie = {
    var current = root

    for (char <- word)
      current = current.children.getOrElseUpdate(
        char,
        new Node(
          parent = Some(current),
          value = Some(char)
        )
      )

    current.isWord = true

    this
  }

  def remove(word: String): Trie = {
    var current = Option(root)

    for (char <- word if current.nonEmpty)
      current = current.get.children.get(char)

    if (current.isEmpty) this
    else if (current.get.children.size > 0) {
      current.get.isWord = false
      this
    } else {
      var backCurrent: Node = current.get
      backCurrent.isWord = false
      while (backCurrent.isWord == false) {
        val parentNode = backCurrent.parent.get
        val thisValue  = backCurrent.value.get
        backCurrent = parentNode
        backCurrent.children.remove(thisValue)
      }

      this
    }

  }

  def contains(word: String): Boolean = {
    var current = root

    for (char <- word) {
      current.children.get(char) match {
        case Some(node) => current = node
        case None       => return false
      }
    }

    current.isWord
  }

  def substringsOf(word: String): Set[String] = {
    var current = Option(root)
    val set     = Set.newBuilder[Int]

    for ((char, index) <- word.zipWithIndex if current.nonEmpty) {
      if (current.exists(_.isWord)) set += index
      current = current.get.children.get(char)

    }
    if (current.exists(_.isWord)) set += word.length

    set.result().map(word.slice(0, _))

  }

  def stringsStartingWith(prefix: String): Set[String] = {
    var current = Option(root)
    for (char <- prefix if current.nonEmpty)
      current = current.get.children.get(char)

    if (current.isEmpty) Set.empty[String]

    val set = Set.newBuilder[String]

    def walk(node: Node, path: Array[Char]): Unit = {
      if (node.isWord) set += prefix + path.mkString
      for ((k, v) <- node.children) walk(v, path :+ k)
    }

    walk(current.get, Array.empty[Char])

    set.result()
  }


  override def toString(): String = {

    val set = Set.newBuilder[String]

    def walk(node: Node, path: Array[Char]): Unit = {
      if (node.isWord) set += path.mkString
      for ((k, v) <- node.children) walk(v, path :+ k)
    }
    walk(root, Array.empty[Char])

    set.result().mkString("Trie(", ", ", ")")

  }

}
