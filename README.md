Sauron [![Build Status](https://travis-ci.org/pathikrit/sauron.png?branch=master)](http://travis-ci.org/pathikrit/sauron)
--------

[Lens library](http://stackoverflow.com/questions/3900307/cleaner-way-to-update-nested-structures) in only [35-lines of Scala](src/main/scala/com/github/pathikrit/sauron/package.scala):

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

Consult [the tests](src/test/scala/com/github/pathikrit/sauron/suites/SauronSuite.scala) for more examples.

Usage: In your `build.sbt`, add the following entries:

```scala
resolvers += Resolver.bintrayRepo("pathikrit", "maven")

libraryDependencies += "com.github.pathikrit" %% "sauron" % "0.1.0"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```

The latest published versions can be found here: http://dl.bintray.com/pathikrit/maven/com/github/pathikrit

Note: Significant changes were introduced in [v0.2.0](https://github.com/pathikrit/sauron/pull/3).
[v0.1.0](https://github.com/pathikrit/sauron/tree/3bde2a2f27094390465cb05ff7692066a3d98d55) used to have an interesting recursive macro
