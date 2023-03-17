plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

dependencies {
  implementation(project(":common-plugin"))
  compileOnly(project(":foundation-shared"))
}

concreteItem {
  dependency(project(":foundation-core"))
}
