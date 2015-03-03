package com.github.pathikrit

import scala.reflect.macros.blackbox

package object sauron {

  def lens[A, B](obj: A)(path: A => B)(modifier: B => B): A = macro lensImpl[A, B]

  def lensImpl[A, B](c: blackbox.Context)(obj: c.Expr[A])(path: c.Expr[A => B])(modifier: c.Expr[B => B]): c.Tree = {
    import c.universe._

    def split(accessor: c.Tree): List[c.TermName] = accessor match {    // (_.p.q.r) -> List(p, q, r)
      case q"$pq.$r" => split(pq) :+ r
      case _: Ident => Nil
      case _ => c.abort(c.enclosingPosition, s"Unsupported path element: $accessor")
    }

    def join(pathTerms: List[TermName]): c.Tree = (q"(x => x)" /: pathTerms) {    // List(p, q, r) -> (_.p.q.r)
      case (q"($arg) => $pq", r) => q"($arg) => $pq.$r"
    }

    path.tree match {
      case q"($_) => $accessor" => split(accessor) match {
        case p :: ps => q"$obj.copy($p = lens($obj.$p)(${join(ps)})($modifier))"  // lens(a)(_.b.c)(f) = a.copy(b = lens(a.b)(_.c)(f)
        case Nil => q"$modifier($obj)"                                            // lens(x)(_)(f) = f(x)
      }
      case _ => c.abort(c.enclosingPosition, s"Path must have shape: _.a.b.c.(...), got: ${path.tree}")
    }
  }
}
