# Cerberus

<p align="center">
  <a href="https://yarnpkg.com/">
    <img alt="Yarn" src="https://raw.githubusercontent.com/jyotman/cerberus/master/cerberus_logo.png" width="350">
  </a>
</p>

[![Build Status](https://travis-ci.org/jyotman/cerberus.svg?branch=master)](https://travis-ci.org/jyotman/cerberus)

Lightweight Role and Attribute based Access Control for Scala.

Easy to use DSL for assigning permissions to different roles.

Read more about [RBAC](https://en.wikipedia.org/wiki/Role-based_access_control) and [ABAC](https://en.wikipedia.org/wiki/Attribute-based_access_control).

## Installation

    resolvers += Resolver.bintrayRepo("slyngdk","maven")

    libraryDependencies += "dk.slyng" %% "cerberus" % "0.1.0"
    
## Basic Example

```scala
import xyz.Types.Data
import xyz.jyotman.Cerberus
import xyz.jyotman.Dsl._
    
case object UserRole extends Role()
  case object CuratorRole extends Role()

  case object ProjectResource extends Resource()
  case object ProfileResource extends Resource()

  case object CreateAction extends Action()
  case object ReadAction extends Action()
  case object UpdateAction extends Action()
  case object DeleteAction extends Action()

  val data: Data =
    (UserRole can (
      (ReadAction any ProjectResource attributes "title" & "description" & "!createdOn") also
        (ReadAction own ProjectResource attributes "title" & "description") also
        (CreateAction own ProjectResource) also
        (UpdateAction own ProfileResource)
      )) and
      (CuratorRole can (
        (ReadAction any ProjectResource allAttributes() ) also
          (UpdateAction any ProjectResource) also
          (DeleteAction any ProjectResource allAttributes())
        ))
    
val cerberus = Cerberus(data)

cerberus.can(UserRole, CreateAction, ProjectResource).any // false
cerberus.can(UserRole, CreateAction, ProjectResource).own // true
cerberus.can(UserRole, ReadAction, ProjectResource).any(List("createdOn")) // false
cerberus.can(UserRole, ReadAction, ProjectResource).any(List("title", "description")) // true

// Checking multiple roles joined permissions
val permission = cerberus.can(Seq(UserRole, CuratorRole), ReadAction, ProjectResource)
assert(permission.any === true)
assert(permission.own === true)
assert(permission.anyAttributes === Some(List("*")))
assert(permission.ownAttributes === Some(List("title", "description")))
```
    
## Documentation

Work in Progress

### Inspired by [Access Control](https://github.com/onury/accesscontrol).
