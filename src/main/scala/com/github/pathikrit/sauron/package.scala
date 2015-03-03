package com.github.pathikrit

import scala.reflect.macros.blackbox

package object sauron {

  def lens[A, B](obj: A)(path: A => B)(modifier: B => B): A = macro lensImpl[A, B]

  /**
   * lens(a)(_.b.c)(f) = a.copy(b = lens(a.b)(_.c)(f)
   * lens(x)(_)(f) = f(x)
   */
  def lensImpl[A, B](c: blackbox.Context)(obj: c.Expr[A])(path: c.Expr[A => B])(modifier: c.Expr[B => B]): c.Tree = {
    import c.universe._

    /**
     * a.b.c -> List(a, b, c)
     */
    def reverse(tree: c.Tree): List[c.TermName] = tree match {
      case q"$ab.$c" => reverse(ab) :+ c
      case t: Ident => Nil
      case _ => c.abort(c.enclosingPosition, s"Unsupported path element: $tree")
    }


    path.tree match {
      case q"($arg) => $pathBody" => {
        reverse(pathBody) match {
          case b :: c :: d :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c.$d)($modifier))"
          case b :: c :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c)($modifier))"
          case b :: Nil => q"$obj.copy($b = lens($obj.$b)(identity)($modifier))"
          case Nil => q"$modifier($path($obj))"
        }
      }
      case _ => q"$modifier($obj)"
    }
  }
}
