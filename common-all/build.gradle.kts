plugins {
  id("gay.pizza.foundation.concrete-base")
}

dependencies {
  // Serialization
  api(libs.kotlin.serialization.json)
  api(libs.kotlin.serialization.yaml)
}
