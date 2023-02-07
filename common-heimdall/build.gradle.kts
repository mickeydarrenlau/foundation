plugins {
  id("gay.pizza.foundation.concrete-base")
}

dependencies {
  api(project(":common-all"))
  api(libs.postgresql)
  api(libs.exposed.jdbc)
  api(libs.exposed.java.time)
  api(libs.hikaricp)
}
