package com.github.pathikrit.sauron.suites

import org.scalatest._, Matchers._

class SauronSuite extends FunSuite {
  import com.github.pathikrit.sauron._

  test("basic lensing") {
    case class Street(name: String)
    case class Address(street: Street, city: String, state: String, zip: String, country: String)
    case class Person(name: String, address: Address)

    val p1 = Person("Rick", Address(Street("Rock St"), "MtV", "CA", "94041", "USA"))
    def addHouseNumber(number: Int)(st: String) = s"$number $st"

    val p2 = Lens(p1)(_.address.street.name).apply(addHouseNumber(1901))
    p2 shouldEqual p1.copy(address = p1.address.copy(street = p1.address.street.copy(name = addHouseNumber(1901)(p1.address.street.name))))
  }
}
