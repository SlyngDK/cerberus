package xyz.jyotman

import scala.collection.immutable.HashMap

object Dsl {

  implicit class AssignResourceToRole(role: Role) {
    def can(resourcesWithActions: Map[Resource, Map[Action, Permission]])
      : Map[Role, Map[Resource, Map[Action, Permission]]] =
      HashMap(role -> resourcesWithActions)
  }

  implicit class JoinResourceAndActionAnyPermission(action: Action) {
    def any(resource: Resource): Map[Resource, Map[Action, Permission]] =
      HashMap(resource -> HashMap(action -> Permission(any = true)))
  }

  implicit class JoinResourceAndActionOwnPermission(action: Action) {
    def own(resource: Resource): Map[Resource, Map[Action, Permission]] =
      HashMap(resource -> HashMap(action -> Permission(own = true)))
  }

  implicit class JoinResource(
      resourceWithActions: Map[Resource, Map[Action, Permission]]) {
    def also(resourceWithActions2: Map[Resource, Map[Action, Permission]])
      : Map[Resource, Map[Action, Permission]] = {
      val resource = resourceWithActions2.head._1
      if (resourceWithActions.contains(resource)) {
        val actionToBeAdded = resourceWithActions2.head._2.head._1
        val existingActionsWithPermission = resourceWithActions(resource)
        if (existingActionsWithPermission.contains(actionToBeAdded)) {
          val updatedPermission = existingActionsWithPermission(actionToBeAdded)
            .merge(resourceWithActions2.head._2.head._2)
          resourceWithActions.updated(
            resource,
            resourceWithActions(resource).updated(actionToBeAdded,
                                                  updatedPermission))
        } else
          resourceWithActions.updated(
            resource,
            resourceWithActions2.head._2 ++ resourceWithActions(resource))
      } else
        resourceWithActions ++ resourceWithActions2
    }
  }

  implicit class JoinRole(
      role: Map[Role, Map[Resource, Map[Action, Permission]]]) {
    def and(role2: Map[Role, Map[Resource, Map[Action, Permission]]])
      : Map[Role, Map[Resource, Map[Action, Permission]]] = role ++ role2
  }

  implicit class JoinAttributes(attribute: String) {
    def &(attribute2: String): List[String] =
    List(attribute, attribute2)
  }

  implicit class JoinAttributesSeq(attributes: List[String]) {
    def &(attribute2: String): List[String] =
    attributes :+ attribute2
  }

  implicit class AddAttributes(
      resourceWithAction: Map[Resource, Map[Action, Permission]]) {
    def attributes(
        attributes: List[String]): Map[Resource, Map[Action, Permission]] = {
      val resource = resourceWithAction.head._1
      val action = resourceWithAction.head._2.head._1
      val permission = resourceWithAction.head._2.head._2
      val permissionWithAttributes =
        if (permission.any)
          permission.copy(anyAttributes = Some(attributes))
        else
          permission.copy(ownAttributes = Some(attributes))
      resourceWithAction.updated(
        resource,
        resourceWithAction(resource).updated(action, permissionWithAttributes))
    }

    def allAttributes(): Map[Resource, Map[Action, Permission]] = {
      val resource = resourceWithAction.head._1
      val action = resourceWithAction.head._2.head._1
      val permission = resourceWithAction.head._2.head._2
      val permissionWithAttributes =
        if (permission.any)
          permission.copy(anyAttributes = Some(List("*")))
        else
          permission.copy(ownAttributes = Some(List("*")))
      resourceWithAction.updated(
        resource,
        resourceWithAction(resource).updated(action, permissionWithAttributes))
    }
  }

}
