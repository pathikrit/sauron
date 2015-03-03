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

    /**
     * _.p.q.r -> List(p, q, r)
     */
    def split(tree: c.Tree): List[c.TermName] = tree match {
      case q"$pq.$r" => split(pq) :+ r
      case _: Ident => Nil
      case _ => c.abort(c.enclosingPosition, s"Unsupported path element: $tree")
    }

    /**
     * List(p, q, r) -> _.p.q.r
     */
    def join(path: List[TermName]): c.Tree = (q"(x => x)" /: path) {
      case (q"($arg) => $qr", p) => q"($arg) => $qr.$p"
    }

    p.tree match {
      case q"($_) => $path" => split(path) match {
        case p :: ps => q"$x.copy($p = lens($x.$p)(${join(ps)})($f))"
        case Nil => q"$f($x)"
      }
      case _ => c.abort(c.enclosingPosition, s"Path must have shape: _.a.b.c.(...), got: ${p.tree}")
    }
  }
}
