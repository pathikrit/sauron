package com.github.pathikrit

import scala.language.experimental.macros

package object sauron {
  class Lens[T, U](obj: T, leafModifier: (T, U => U) => T) {
    def apply(modifier: U => U): T = leafModifier(obj, modifier)
  }

  def modify[T, U](obj: T)(path: T => U): Lens[T, U] = macro LensMacro.modifyImpl[T, U]
}
