package xyz.jyotman

import xyz.Types.Data
import xyz.jyotman.Permission.EmptyPermission

class Cerberus(data: Data) {

  def can(role: Role, action: Action, resource: Resource): Permission = {
    if (!data.contains(role))
      throw new Exception(raw"""Role "$role" does not exist""")
    else if (data(role).contains(resource) && data(role)(resource).contains(
               action))
      data(role)(resource)(action)
    else
      EmptyPermission
  }

  def can(roles: Seq[Role],
          action: Action,
          resource: Resource): Permission = {
    val roleMap = data.filterKeys(roles.contains)
    roleMap
      .filter(d => {
        d._2.contains(resource) && d._2(resource).contains(action)
      })
      .map(d => d._2(resource)(action))
      .fold(Permission.EmptyPermission){ (a, b)=>
        a + b
      }
  }

  def roles: Iterable[Role] = data.keys
}

object Cerberus {

  def apply(data: Data): Cerberus = new Cerberus(data)
}
