import scala.sys.process._
import sbtrelease.ReleaseStateTransformations._

releaseCrossBuild := false

// thanks https://github.com/plokhotnyuk/jsoniter-scala/blob/master/release.sbt
lazy val updateReleaseVersionInReadme: ReleaseStep = { st: State =>
  val extracted = Project.extract(st)
  val newVersion = extracted.get(version)
  val oldVersion = "git describe --abbrev=0".!!.trim.tail
  val readme = "README.md"
  val oldContent = IO.read(file(readme))
  val newContent = oldContent.replaceAll('"' + oldVersion + '"', '"' + newVersion + '"')
    .replaceAll('-' + oldVersion + '-', '-' + newVersion + '-')
  IO.write(file(readme), newContent)
  s"git add $readme" !! st.log
  st
}

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("+test"),
  setReleaseVersion,
  updateReleaseVersionInReadme,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)