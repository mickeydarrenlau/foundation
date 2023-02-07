plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

dependencies {
  api(project(":common-all"))
  implementation(project(":foundation-shared"))

  implementation(libs.aws.sdk.s3)
  implementation(libs.quartz.core)
  implementation(libs.guava)

  implementation(libs.koin.core)
  testImplementation(libs.koin.test)
}
