package cloud.kubelet.foundation.core.persist

import cloud.kubelet.foundation.core.FoundationCorePlugin
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.entitystore.StoreTransaction

class PersistentStore(corePlugin: FoundationCorePlugin, fileStoreName: String) : AutoCloseable {
  private val fileStorePath = corePlugin.pluginDataPath.resolve("persistence/${fileStoreName}")
  internal val entityStore = PersistentEntityStores.newInstance(fileStorePath.toFile())

  fun transact(block: (StoreTransaction) -> Unit) = entityStore.executeInTransaction(block)

  fun create(entityTypeName: String, populate: Entity.() -> Unit) = transact { tx ->
    val entity = tx.newEntity(entityTypeName)
    populate(entity)
  }

  fun <T> find(entityTypeName: String, propertyName: String, value: Comparable<T>) =
    transact { tx -> tx.find(entityTypeName, propertyName, value) }

  override fun close() {
    entityStore.close()
  }
}
