enablePlugins(ScalaJSPlugin)

name := "Bot"
scalaVersion := "2.11.8" // or any other Scala version >= 2.10.2

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2"

// https://mvnrepository.com/artifact/com.lihaoyi/scalatags_2.11
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.5"

libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.4.3"



skip in packageJSDependencies := false
jsDependencies +=
  "org.webjars" % "jquery" % "2.1.4" / "2.1.4/jquery.js"
  
  
  