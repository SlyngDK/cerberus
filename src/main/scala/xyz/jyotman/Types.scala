package xyz

import xyz.jyotman.{Action, Permission, Resource, Role}

package object Types {
  type Data = Map[Role, Map[Resource, Map[Action, Permission]]]
}