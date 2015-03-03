package com.github.pathikrit.sauron.suites

import org.scalatest._, Matchers._

class SauronSuite extends FunSuite {
  test("basic lensing") {
    import com.github.pathikrit.sauron._

    case class Street(name: String)
    case class Address(street: Street, city: String, state: String, zip: String, country: String)
    case class Person(name: String, address: Address)

    val p1 = Person("Rick", Address(Street("Rock St"), "MtV", "CA", "94041", "USA"))
    def addHouseNumber(st: String) = s"1901 $st"

    val p2 = Lens(p1)(_.address.street.name)(addHouseNumber)
    p2 shouldEqual p1.copy(address = p1.address.copy(street = p1.address.street.copy(name = addHouseNumber(p1.address.street.name))))
  }
}
