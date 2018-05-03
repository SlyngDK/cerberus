import org.scalatest.FlatSpec
import xyz.Types.Data
import xyz.jyotman._
import xyz.jyotman.Dsl._

class CerberusSpec extends FlatSpec {

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

  "Cerberus" should "return permission without rights" in {
    val permission = cerberus.can(UserRole, DeleteAction, ProjectResource)
    assert(permission.any === false)
    assert(permission.own === false)
    assert(permission.anyAttributes.isEmpty)
    assert(permission.ownAttributes.isEmpty)

    assertResult(Permission.EmptyPermission)(permission)
  }

  it should "return permission to read project attr 'title' and 'description'" in {
    val permission = cerberus.can(UserRole, ReadAction, ProjectResource)
    assert(permission.any === true)
    assert(permission.own === true)
    assert(permission.anyAttributes === Some(List("title", "description", "!createdOn")))
    assert(permission.ownAttributes === Some(List("title", "description")))
    val permission2 = cerberus.can(Seq(UserRole), ReadAction, ProjectResource)
    assert(permission2.any === true)
    assert(permission2.own === true)
    assert(permission2.anyAttributes === Some(List("title", "description", "!createdOn")))
    assert(permission2.ownAttributes === Some(List("title", "description")))
  }

  it should "return permission to read any project attr" in {
    val permission = cerberus.can(Seq(UserRole, CuratorRole), ReadAction, ProjectResource)
    assert(permission.any === true)
    assert(permission.own === true)
    assert(permission.anyAttributes === Some(List("*")))
    assert(permission.ownAttributes === Some(List("title", "description")))
  }

  it should "return permission to update project" in {
    val permission = cerberus.can(Seq(UserRole, CuratorRole), UpdateAction, ProjectResource)
    assert(permission.any === true)
    assert(permission.own === false)
    assert(permission.anyAttributes === None)
    assert(permission.ownAttributes === None)
  }

  it should "return permission to delete project" in {
    val permission = cerberus.can(Seq(UserRole, CuratorRole), DeleteAction, ProjectResource)
    assert(permission.any === true)
    assert(permission.own === false)
    assert(permission.anyAttributes === Some(List("*")))
    assert(permission.ownAttributes === None)
  }
}
