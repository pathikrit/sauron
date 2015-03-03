package com.github.pathikrit

import scala.reflect.macros.blackbox

package object sauron {

  def lens[A, B](obj: A)(path: A => B)(modifier: B => B): A = macro lens2Impl[A, B]

  /**
   * lens(a)(_.b.c)(f) = a.copy(b = lens(a.b)(_.c)(f)
   * lens(x)(_)(f) = f(x)
   */
  def lens2Impl[A, B](c: blackbox.Context)(obj: c.Expr[A])(path: c.Expr[A => B])(modifier: c.Expr[B => B]): c.Tree = {
    import c.universe._

    def collectPathElements(tree: c.Tree): List[c.TermName] = tree match {
      case q"$parent.$child" => collectPathElements(parent) :+ child
      case t: Ident => Nil
      case _ => c.abort(c.enclosingPosition, s"Unsupported path element: $tree")
    }

    val code = path.tree match {
      case q"($arg) => $pathBody" => collectPathElements(pathBody) match {
        case b :: c :: d :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c.$d)($modifier))"
        case b :: c :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c)($modifier))"
        case b :: Nil => q"$obj.copy($b = lens($obj.$b)(identity)($modifier))"
        case Nil => q"$modifier($path($obj))"
      }
      case _ => q"$modifier($obj)"
    }
    println("\n---------------------------")
    println(code)
    println("---------------------------\n")
    code
  }
}
