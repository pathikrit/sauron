package com.github.pathikrit.sauron.suites

import org.scalatest._, Matchers._

class SauronSuite extends FunSuite {
  import com.github.pathikrit.sauron._

  case class A(a1: String, a2: B)
  case class B(b1: Option[Int], b2: List[C], b3: C)
  case class C(c1: Int, c2: List[D], c3: D)
  case class D(d1: List[String], d2: Option[Boolean], d3: A, d4: D)

  test("lensing") {
    case class Person(name: String, address: Address)
    case class Address(street: Street, street2: List[Street], city: String, state: String, zip: String, country: Option[String])
    case class Street(name: String)

    val p1 = Person("Rick", Address(Street("Rock St"), Nil, "MtV", "CA", "94041", Some("USA")))
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
    val lens2: Address ~~> Street = lens(_: Address)(_.street)
    val lens3: Street ~~> String = lens(_: Street)(_.name)

    val lens4: Person ~~> String = lens1 andThenLens lens2 andThenLens lens3
    lens4(p1)(_.toLowerCase) shouldEqual p3

    val lens5: Person ~~> String = lens3 composeLens lens2 composeLens lens1
    lens5(p1)(_.toLowerCase) shouldEqual p3

    val p5: Person = lens(p1)(_.address.street.name).setTo("Rick St")
    p5.address.street.name shouldEqual "Rick St"
    //lens(p1)(_.address.street2.each.name)(_.toUpperCase)

    def f(s: String) = s.toUpperCase

    p1.copy(
      address = p1.address.copy(
        street2 = p1.address.street2.map(el => el.copy(name = f(el.name)))
      )
    )

    p1.copy(
      address = p1.address.copy(
        country = p1.address.country.map(_.toUpperCase)
      )
    )

    // should not typecheck


    "lens(p1)(_.address.zip)(_.toUpperCase)" should compile
    "lens(p1)(_.address.zip.length)(_ + 1)" shouldNot compile
    "lens(p1)(_.toString)(_.toUpperCase)" shouldNot compile
  }
}
