package com.github.pathikrit

import scala.reflect.macros.blackbox

package object sauron {

  def lens[A, B](obj: A)(pathSelector: A => B)(modifier: B => B): A = macro lensImpl[A, B]

  /**
   * lens(a)(_.b.c)(f) = a.copy(b = lens(a.b)(_.c)(f)
   * lens(x)(_)(f) = f(x)
   */
  def lensImpl[A, B](c: blackbox.Context)(obj: c.Expr[A])(pathSelector: c.Expr[A => B])(modifier: c.Expr[B => B]): c.Tree = {
    import c.universe._

    val (x, p, f) = (obj, pathSelector, modifier)
    def compileError(msg: String) = c.abort(c.enclosingPosition, msg)

    /**
     * _.p.q.r -> List(p, q, r)
     */
    def reverse(tree: c.Tree): List[c.TermName] = tree match {
      case q"$pq.$r" => reverse(pq) :+ r
      case _: Ident => Nil
      case _ => compileError(s"Unsupported path element: $tree")
    }

    p.tree match {
      case q"($_) => $path" => reverse(path) match {
        case p1 :: p2 :: p3 :: p4 :: p5 :: p6 :: Nil => q"$x.copy($p1 = lens($x.$p1)(_.$p2.$p3.$p4.$p5.$p6)($f))"
        case p1 :: p2 :: p3 :: p4 :: p5 :: Nil => q"$x.copy($p1 = lens($x.$p1)(_.$p2.$p3.$p4.$p5)($f))"
        case p1 :: p2 :: p3 :: p4 :: Nil => q"$x.copy($p1 = lens($x.$p1)(_.$p2.$p3.$p4)($f))"
        case p1 :: p2 :: p3 :: Nil => q"$x.copy($p1 = lens($x.$p1)(_.$p2.$p3)($f))"
        case p1 :: p2 :: Nil => q"$x.copy($p1 = lens($x.$p1)(_.$p2)($f))"
        case p1 :: Nil => q"$x.copy($p1 = lens($x.$p1)(x => x)($f))"
        case Nil => q"$f($x)"
        case _ => compileError(s"Too long of accessor path: ${p.tree}")
      }
      case _ => compileError(s"Path must have shape: _.a.b.c.(...), got: ${p.tree}")
    }
  }
}
