package xyz.jyotman

import xyz.jyotman.Checker.check

class Permission(val own: Boolean = false,
                 val any: Boolean = false,
                 val ownAttributes: Option[List[String]] = None,
                 val anyAttributes: Option[List[String]] = None) {

  def any(attributesToCheck: List[String]): Boolean =
    anyAttributes.isDefined && any && check(attributesToCheck,
                                            anyAttributes.get)

  def own(attributesToCheck: List[String]): Boolean = {
    if (own)
      ownAttributes.isDefined && check(attributesToCheck, ownAttributes.get)
    else
      any && any(attributesToCheck)
  }

  def merge(permission: Permission): Permission = {
    if (permission.own)
      copy(own = permission.own, ownAttributes = permission.ownAttributes)
    else
      copy(any = permission.any, anyAttributes = permission.anyAttributes)
  }

  def +(that: Permission): Permission = {
    Permission(this.own || that.own,
               this.any || that.any,
               mergeAttr(this.ownAttributes, that.ownAttributes),
               mergeAttr(this.anyAttributes, that.anyAttributes))
  }

  private def mergeAttr(attr1: Option[List[String]],
                        attr2: Option[List[String]]): Option[List[String]] = {
    val list = attr1.getOrElse(List()) ++ attr2.getOrElse(List())
    if (list.contains("*")
        || (attr1.isDefined && attr1.getOrElse(List()).isEmpty)
        || (attr2.isDefined && attr2.getOrElse(List()).isEmpty))
      Option(List("*"))
    else
      list.distinct.filterNot(a =>
        a.startsWith("!") && list.contains(a.substring(1))) match {
        case l if l.nonEmpty => Some(l)
        case _ => None
      }
  }

  def copy(own: Boolean = own,
           any: Boolean = any,
           ownAttributes: Option[List[String]] = ownAttributes,
           anyAttributes: Option[List[String]] = anyAttributes): Permission =
    Permission(own, any, ownAttributes, anyAttributes)

  override def toString: String =
    s"own=$own, any=$any, ownAttributes=$ownAttributes, anyAttributes=$anyAttributes"
}

object Permission {

  def apply(own: Boolean = false,
            any: Boolean = false,
            ownAttributes: Option[List[String]] = None,
            anyAttributes: Option[List[String]] = None): Permission =
    new Permission(own, any, ownAttributes, anyAttributes)

  val EmptyPermission = apply()
}
