Sauron [![Build Status](https://travis-ci.org/pathikrit/sauron.png?branch=master)](http://travis-ci.org/pathikrit/sauron)
--------

Probably the simplest [Scala lens macro](http://stackoverflow.com/questions/3900307/cleaner-way-to-update-nested-structures) out there.
Code speaks more than a thousand words:

```scala
case class User(id: Int, name: String, email: String, registeredOn: DateTime)
```

There is zero overhead. The `lens` macro simply expands to this during compilation:

```scala
```

Usage: In your `build.sbt`, add the following entries:

```scala
resolvers += Resolver.bintrayRepo("pathikrit", "maven")

libraryDependencies += "com.github.pathikrit" %% "sauron" % "0.1.0"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```

The latest published versions can be found here:
http://dl.bintray.com/pathikrit/maven/com/github/pathikrit
