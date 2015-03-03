package com.github.pathikrit

import scala.annotation.tailrec
import scala.reflect.macros.blackbox

package object sauron {
  class Lens[A, B](obj: A, leafModifier: (A, B => B) => A) {
    def apply(modifier: B => B): A = leafModifier(obj, modifier)
  }

  object Lens {
    def apply[A, B](obj: A)(path: A => B): Lens[A, B] = macro modifyImpl[A, B]

    /**
     * Lens(a)(_.b.c) => new Lens(a, (a, f) => a.copy(b = a.b.copy(c = f(a.b.c))))
     */
    def modifyImpl[A, B](c: blackbox.Context)(obj: c.Expr[A])(path: c.Expr[A => B]): c.Tree = {
      import c.universe._

      /**
       * _.a.b.c => List(a, b, c)
       */
      def collectPathElements(tree: c.Tree): List[c.TermName] = tree match {
        case q"$parent.$child" => collectPathElements(parent) :+ child
        case t: Ident => Nil
        case _ => c.abort(c.enclosingPosition, s"Unsupported path element: $tree")
      }

      /**
       * (a, List(d, c, b), k) => a.copy(b = a.b.copy(c = a.b.c.copy(d = k))
       */
      def generateCopies(rootPathEl: c.TermName, reversePathEls: List[c.TermName], newVal: c.Tree): c.Tree = {
        reversePathEls match {
          case Nil => newVal
          case pathEl :: tail =>
            val selectCopy = generateSelects(rootPathEl, (TermName("copy") :: tail).reverse)
            val copy = q"$selectCopy($pathEl = $newVal)"
            generateCopies(rootPathEl, tail, copy)
        }
      }

      /**
       * (x, List(a, b, c)) => x.a.b.c
       */
      def generateSelects(rootPathEl: c.TermName, pathEls: List[c.TermName]): c.Tree = {
        @tailrec
        def go(els: List[c.TermName], result: c.Tree): c.Tree = els match {
          case Nil => result
          case pathEl :: tail => go(tail, q"$result.$pathEl")
        }
        go(pathEls, Ident(rootPathEl))
      }

      val pathEls = path.tree match {
        case q"($arg) => $pathBody" => collectPathElements(pathBody)
        case _ => c.abort(c.enclosingPosition, s"Path must have shape: _.a.b.c.(...), got: ${path.tree}")
      }

      val rootPathEl = TermName(c.freshName()) // the root object (same as obj)
      val fn = TermName(c.freshName()) // the function that modifies that last path element

      // new value of the last path element is an invocation of $fn on the current last path element value
      val select = generateSelects(rootPathEl, pathEls)
      val mod = q"$fn($select)"

      val copies = generateCopies(rootPathEl, pathEls.reverse, mod)

      val rootPathElParamTree = ValDef(Modifiers(), rootPathEl, TypeTree(), EmptyTree)
      val fnParamTree = ValDef(Modifiers(), fn, TypeTree(), EmptyTree)
      q"new Lens($obj, ($rootPathElParamTree, $fnParamTree) => $copies)"
    }
  }
}
