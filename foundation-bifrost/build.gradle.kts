dependencies {
  implementation("net.dv8tion:JDA:5.0.0-alpha.2") {
    exclude(module = "opus-java")
  }
  implementation("com.rabbitmq:amqp-client:5.14.2")

  compileOnly(project(":foundation-core"))
}
