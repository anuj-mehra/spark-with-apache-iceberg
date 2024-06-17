import sbt.Keys.{skip, _}
import sbt.{AutoPlugin, Def, Task}

object NoPublish extends AutoPlugin{

  override lazy val projectSettings: Seq[Def.Setting[Task[Boolean]]] = Seq(skip in publish := true)
}