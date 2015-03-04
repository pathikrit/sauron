package com.github.pathikrit.sauron.suites

import org.scalatest._, Matchers._

class SauronSuite extends FunSuite {
  test("lensing") {
    import com.github.pathikrit.sauron._

    case class Person(name: String, address: Address)
    case class Address(street: Street, city: String, state: String, zip: String, country: String)
    case class Street(name: String)

    val p1 = Person("Rick", Address(Street("Rock St"), "MtV", "CA", "94041", "USA"))

//    val lens2 = {f: (String => String) => p1.copy(address = p1.address.copy(street = p1.address.street.copy(name = f(p1.address.street.name))))

    val lensP1 = lens(p1)(_.address.street.name)


//    def addHouseNumber(number: Int)(st: String) = s"$number $st"
//
//    val p2 = lens(p1)(_.address.street.name)(addHouseNumber(1901))
//    p2 shouldEqual p1.copy(address = p1.address.copy(street = p1.address.street.copy(name = addHouseNumber(1901)(p1.address.street.name))))
//
//    "lens(p1)(_.address.zip)(_.toUpperCase)" should compile
//    "lens(p1)(_.address.zip.length)(_ + 1)" shouldNot compile
//    "lens(p1)(_.toString)(_.toUpperCase)" shouldNot compile

//    val personToCity = lens(_: Person)(_.address.city)(_.toLowerCase)
//    val p3 = personToCity(p1)
//    p3.address.city shouldEqual "mtv"
  }


}
