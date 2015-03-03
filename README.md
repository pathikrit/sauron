Sauron [![Build Status](https://travis-ci.org/pathikrit/sauron.png?branch=master)](http://travis-ci.org/pathikrit/sauron)
--------

Probably the simplest [Scala lens library](http://stackoverflow.com/questions/3900307/cleaner-way-to-update-nested-structures) possible:

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

Usage: In your `build.sbt`, add the following entries:

```scala
resolvers += Resolver.bintrayRepo("pathikrit", "maven")

libraryDependencies += "com.github.pathikrit" %% "sauron" % "0.1.0"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```

The latest published versions can be found here: http://dl.bintray.com/pathikrit/maven/com/github/pathikrit

Trivia:
* Why Sauron? Something to do with lenses and the fact that Sauron has an all-seeing eye...
* The [source code of this library](src/main/scala/com/github/pathikrit/sauron/package.scala) is shorter than this README :)
