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

    def compileError(msg: String) = c.abort(c.enclosingPosition, msg)

    /**
     * a.b.c -> List(a, b, c)
     */
    def reverse(tree: c.Tree): List[c.TermName] = tree match {
      case q"$ab.$c" => reverse(ab) :+ c
      case t: Ident => Nil
      case _ => compileError(s"Unsupported path element: $tree")
    }

    path.tree match {
      case q"($arg) => $pathBody" => reverse(pathBody) match {
        case b :: c :: d :: e :: f :: g :: h :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c.$d.$e.$f.$g.$h)($modifier))"
        case b :: c :: d :: e :: f :: g :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c.$d.$e.$f.$g)($modifier))"
        case b :: c :: d :: e :: f :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c.$d.$e.$f)($modifier))"
        case b :: c :: d :: e :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c.$d.$e)($modifier))"
        case b :: c :: d :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c.$d)($modifier))"
        case b :: c :: Nil => q"$obj.copy($b = lens($obj.$b)(_.$c)($modifier))"
        case b :: Nil => q"$obj.copy($b = lens($obj.$b)(x => x)($modifier))"
        case Nil => q"$modifier($obj)"
        case p => compileError(s"Too long of accessor path: ${p.length}")
      }
      case _ => compileError(s"Path must have shape: _.a.b.c.(...), got: ${path.tree}")
    }
  }
}
