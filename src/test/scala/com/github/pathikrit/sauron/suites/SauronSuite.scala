package com.github.pathikrit.sauron.suites

import org.scalatest._, Matchers._

class SauronSuite extends FunSuite {
  test("lensing") {
    import com.github.pathikrit.sauron._

    case class Person(name: String, address: Address)
    case class Address(street: Street, city: String, state: String, zip: String, country: String)
    case class Street(name: String)

    val p1 = Person("Rick", Address(Street("Rock St"), "MtV", "CA", "94041", "USA"))
    def addHouseNumber(number: Int)(st: String) = s"$number $st"

    val p2 = lens(p1)(_.address.street.name)(addHouseNumber(1901))
    p2 shouldEqual p1.copy(address = p1.address.copy(street = p1.address.street.copy(name = addHouseNumber(1901)(p1.address.street.name))))

    "lens(p1)(_.address.zip)(_.toUpperCase)" should compile
    "lens(p1)(_.address.zip.length)(_ + 1)" shouldNot compile
    "lens(p1)(_.toString)(_.toUpperCase)" shouldNot compile

    val personToCity = lens(_: Person)(_.address.city)
    val p3 = personToCity(p1)(_.toLowerCase)
    p3.address.city shouldEqual "mtv"

    val streetUpdater = lens(p1)(_.address.street.name)
    streetUpdater(_.toLowerCase).address.street.name shouldEqual "rock st"
    streetUpdater(_.toUpperCase).address.street.name shouldEqual "ROCK ST"
  }
}
