resolvers ++= Seq(
  "scct-github-repository" at "http://mtkopone.github.com/scct/maven-repo"
)

libraryDependencies ++= Seq(
  "edu.berkeley.cs" %% "chisel" % "latest.release"
)
