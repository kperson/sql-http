lazy val commonSettings = Seq(
  organization := "com.github.kperson",
  version := "1.0.0",
  scalaVersion := "2.13.5",
  parallelExecution in Test := false,
  fork in Test := true
)
lazy val common = (project in file("common")).settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq (
      "mysql"                   % "mysql-connector-java"            % "8.0.23",
      "org.mariadb.jdbc"        % "mariadb-java-client"             % "2.7.2",
      "org.postgresql"          % "postgresql"                      % "42.2.19",
      "org.json4s"             %% "json4s-jackson"                  % "3.6.11",
      "org.sql2o"               % "sql2o"                           % "1.6.0",
      "com.zaxxer"              % "HikariCP"                        % "4.0.3",
      "org.scalatest"          %% "scalatest"                       % "3.2.7"     % Test,
      "com.dimafeng"           %% "testcontainers-scala-scalatest"  % "0.39.3"    % Test,
      "com.dimafeng"           %% "testcontainers-scala-mysql"      % "0.39.3"    % Test,
      "com.dimafeng"           %% "testcontainers-scala-mariadb"    % "0.39.3"    % Test,
      "com.dimafeng"           %% "testcontainers-scala-postgresql" % "0.39.3"    % Test,
      "ch.qos.logback"          % "logback-classic"                 % "1.2.3"     % Test
    )
  )

lazy val lambda = (project in file("lambda")).settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq (
      "com.amazonaws"           % "aws-lambda-java-core"        % "1.2.1",
      "com.amazonaws"           % "aws-java-sdk-secretsmanager" % "1.11.1000"
    )
  ).dependsOn(common)


lazy val http = (project in file("http"))
.settings(commonSettings: _*)
.enablePlugins(JavaAppPackaging)
.dependsOn(common)