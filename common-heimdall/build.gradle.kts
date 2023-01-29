plugins {
  id("gay.pizza.foundation.concrete-library")
}

dependencies {
  api("org.postgresql:postgresql:42.5.1")
  api("org.jetbrains.exposed:exposed-jdbc:0.41.1")
  api("org.jetbrains.exposed:exposed-java-time:0.41.1")
  api("com.zaxxer:HikariCP:5.0.1")
}
