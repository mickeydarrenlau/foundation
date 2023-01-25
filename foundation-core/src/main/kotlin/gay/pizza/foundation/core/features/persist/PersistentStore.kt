package gay.pizza.foundation.core.features.persist

import gay.pizza.foundation.core.FoundationCorePlugin
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityIterable
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.entitystore.StoreTransaction

class PersistentStore(corePlugin: FoundationCorePlugin, fileStoreName: String) : AutoCloseable {
  private val fileStorePath = corePlugin.pluginDataPath.resolve("persistence/${fileStoreName}")
  private val entityStore = PersistentEntityStores.newInstance(fileStorePath.toFile())

  fun <R> transact(block: StoreTransaction.() -> R): R {
    var result: R? = null
    entityStore.executeInTransaction { tx ->
      result = block(tx)
    }
    return result!!
  }

  fun create(entityTypeName: String, populate: Entity.() -> Unit) = transact {
    populate(newEntity(entityTypeName))
  }

  fun <R> getAll(entityTypeName: String, block: (EntityIterable) -> R): R =
    transact { block(getAll(entityTypeName)) }

  fun <T, R> find(entityTypeName: String, propertyName: String, value: Comparable<T>, block: (EntityIterable) -> R): R =
    transact { block(find(entityTypeName, propertyName, value)) }

  fun deleteAllEntities(entityTypeName: String) =
    transact { entityStore.deleteEntityType(entityTypeName) }

  override fun close() {
    entityStore.close()
  }
}
