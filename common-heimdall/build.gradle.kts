plugins {
  id("gay.pizza.foundation.concrete-base")
}

dependencies {
  api(project(":common-all"))
  api("org.postgresql:postgresql:42.5.3")
  api("org.jetbrains.exposed:exposed-jdbc:0.41.1")
  api("org.jetbrains.exposed:exposed-java-time:0.41.1")
  api("com.zaxxer:HikariCP:5.0.1")
}
