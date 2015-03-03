package com.github.pathikrit

import scala.language.experimental.macros

package object sauron {
  class Lens[A, B](obj: A, leafModifier: (A, B => B) => A) {
    def apply(modifier: B => B): A = leafModifier(obj, modifier)
  }

  def lens[A, B](obj: A)(path: A => B): Lens[A, B] = macro LensMacro.modifyImpl[A, B]
}
