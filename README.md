Sauron [![Build Status](https://travis-ci.org/pathikrit/sauron.png?branch=master)](http://travis-ci.org/pathikrit/sauron)
--------

Probably the simplest [Scala lens macro](http://stackoverflow.com/questions/3900307/cleaner-way-to-update-nested-structures) out there.
Code speaks more than a thousand words:

```scala
import com.github.pathikrit.sauron._

case class Street(name: String)
case class Address(street: Street, city: String, state: String, zip: String, country: String)
case class Person(name: String, address: Address)

val p1 = Person("Rick", Address(Street("Rock St"), "MtV", "CA", "94041", "USA"))
def addHouseNumber(number: Int)(st: String) = s"$number $st"

val p2 = Lens(p1)(_.address.street.name)(addHouseNumber(1901))
assert(p2.address.street.name == "1901 Rock St")
```

There is zero overhead. The `lens` macro simply expands to this during compilation:
```scala
p1.copy(address = p1.address.copy(street = p1.address.street.copy(name = addHouseNumber(1901)(p1.address.street.name))))
```

Usage: In your `build.sbt`, add the following entries:

```scala
resolvers += Resolver.bintrayRepo("pathikrit", "maven")

libraryDependencies += "com.github.pathikrit" %% "sauron" % "0.1.0"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```

The latest published versions can be found here:
http://dl.bintray.com/pathikrit/maven/com/github/pathikrit
