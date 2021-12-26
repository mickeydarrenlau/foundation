dependencies {
  api("org.postgresql:postgresql:42.3.1")
  api("org.jetbrains.exposed:exposed-jdbc:0.36.2")
  api("org.jetbrains.exposed:exposed-java-time:0.36.2")
  api("com.zaxxer:HikariCP:5.0.0")
  compileOnly(project(":foundation-core"))
}
