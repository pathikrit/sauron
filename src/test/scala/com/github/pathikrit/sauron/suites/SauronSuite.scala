package com.github.pathikrit.sauron.suites

import org.scalatest._, Matchers._

class SauronSuite extends FunSuite {
  import com.github.pathikrit.sauron._

  test("basic lensing") {
    case class Street(name: String)
    case class Address(street: Street)
    case class Person(address: Address)

    val p1 = Person(Address(Street("s1")))
    def changeName(n: String) = n+n

    val p2 = modify(p1)(_.address.street.name).apply(changeName)
    p2 shouldEqual p1.copy(address = p1.address.copy(street = p1.address.street.copy(name = changeName(p1.address.street.name))))
    "modify(p1)(_.address2.street.name).apply(changeName)" shouldNot compile
  }
}
