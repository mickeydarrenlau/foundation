package gay.pizza.foundation.heimdall.event

import org.jetbrains.exposed.sql.Transaction

abstract class HeimdallEvent {
  abstract fun store(transaction: Transaction)
}
