package com.github.pathikrit

import scala.reflect.macros.blackbox

package object sauron {

  def lens[A, B](obj: A)(path: A => B): (B => B) => A = macro lensImpl[A, B]

  def lensImpl[A, B](c: blackbox.Context)(obj: c.Expr[A])(path: c.Expr[A => B]): c.Tree = {
    import c.universe._

    def split(accessor: c.Tree): List[c.TermName] = accessor match {    // (_.p.q.r) -> List(p, q, r)
      case q"$pq.$r" => split(pq) :+ r
      case _: Ident => Nil
      case _ => c.abort(c.enclosingPosition, s"Unsupported path element: $accessor")
    }

    val f = TermName(c.freshName("f"))

    def nest(prefix: c.Tree, suffix: List[TermName]): c.Tree = suffix match {
      case p :: ps => q"$prefix.copy($p = ${nest(q"$prefix.$p", ps)})" //
      case Nil => q"$f($prefix)"                                       //Reached the end, apply f
    }

    val code = path.tree match {
      case q"($_) => $accessor" =>
        val fParamTree = ValDef(Modifiers(), f, TypeTree(), EmptyTree)
        q"{$fParamTree => ${nest(obj.tree, split(accessor))}}"
      case _ => c.abort(c.enclosingPosition, s"Path must have shape: _.a.b.c.(...), got: ${path.tree}")
    }

    println("\n------")
    println(code)

    code
  }
}
