dependencies {
  implementation("org.postgresql:postgresql:42.3.1")
  implementation("org.jetbrains.exposed:exposed-jdbc:0.36.2")
  implementation("org.jetbrains.exposed:exposed-java-time:0.36.2")
  implementation("com.zaxxer:HikariCP:5.0.0")
  compileOnly(project(":foundation-core"))
}
