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

    val streetUpdater = lens(p1)(_.address.street.name)
    val p3 = streetUpdater(_.toLowerCase)
    p3.address.street.name shouldEqual "rock st"
    streetUpdater(_.toUpperCase).address.street.name shouldEqual "ROCK ST"

    val personToCity: Person ~~> String = lens(_: Person)(_.address.city)
    val p4 = personToCity(p1)(_.toLowerCase)
    p4.address.city shouldEqual "mtv"

    val lens1: Person ~~> Address = lens(_: Person)(_.address)
    val lens2: Address ~~> String = lens(_: Address)(_.street.name)
    val lens3: Person ~~> String = compose(lens1, lens2)

    lens3(p1)(_.toLowerCase) shouldEqual p3

    "lens(p1)(_.address.zip)(_.toUpperCase)" should compile
    "lens(p1)(_.address.zip.length)(_ + 1)" shouldNot compile
    "lens(p1)(_.toString)(_.toUpperCase)" shouldNot compile
  }
}
