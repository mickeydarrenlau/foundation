package gay.pizza.foundation.core.abstraction

interface CoreFeature {
  fun enable()
  fun disable()
  fun module() = org.koin.dsl.module {}
}
