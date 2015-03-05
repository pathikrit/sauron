Sauron [![Build Status](https://travis-ci.org/pathikrit/sauron.png?branch=master)](http://travis-ci.org/pathikrit/sauron) [![Download](https://api.bintray.com/packages/pathikrit/maven/sauron/images/download.svg)](https://bintray.com/pathikrit/maven/sauron/_latestVersion)
--------

[Lens library](http://stackoverflow.com/questions/3900307/cleaner-way-to-update-nested-structures) in less than [40-lines of Scala](src/main/scala/com/github/pathikrit/sauron/package.scala):

```scala
import com.github.pathikrit.sauron._

case class Person(address: Address)
case class Address(street: Street)
case class Street(name: String)

val person = Person(Address(Street("1 Functional Rd.")))

lens(person)(_.address.street.name)(_.toUpperCase)
```

There is zero overhead. The `lens` macro simply expands to this during compilation:
```scala
person.copy(address = person.address.copy(
  street = person.address.street.copy(
    name = (person.address.street.name).toUpperCase)
  )
)
```

Reusable lenses:
```scala
val f1 = lens(person)(_.address.street.name)

val p1: Person = f1(_.toLowerCase)
val p2: Person = f1(_.toUpperCase)
```

The above lens only updates a particular person. You can make even more generic lenses that can update any `Person`:
```scala
val f = lens(_: Person)(_.address.street.name)

val p3: Person = f(p1)(_.toUpperCase)
val p4: Person = f(p2)(_.toLowerCase)
```

Lens composition:
```scala
val lens1: Person ~~> Address = lens(_: Person)(_.address)
val lens2: Address ~~> String = lens(_: Address)(_.street.name)
val lens3: Person ~~> String = lens1 composeLens lens2

val p5: Person = lens3(person)(_.toLowerCase)
```

Consult [the tests](src/test/scala/com/github/pathikrit/sauron/suites/SauronSuite.scala) for more examples.

Usage: In your `build.sbt`, add the following entries:

```scala
resolvers += Resolver.bintrayRepo("pathikrit", "maven")

libraryDependencies += "com.github.pathikrit" %% "sauron" % "1.0.0"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```

This library is inspired by the clever work done by @adamw in his [quicklens](https://github.com/adamw/quicklens) library.
