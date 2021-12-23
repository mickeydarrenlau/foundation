package cloud.kubelet.foundation.core.persist

import cloud.kubelet.foundation.core.FoundationCorePlugin
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityIterable
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.entitystore.StoreTransaction

class PersistentStore(corePlugin: FoundationCorePlugin, fileStoreName: String) : AutoCloseable {
  private val fileStorePath = corePlugin.pluginDataPath.resolve("persistence/${fileStoreName}")
  private val entityStore = PersistentEntityStores.newInstance(fileStorePath.toFile())

  fun <R> transact(block: (StoreTransaction) -> R): R {
    var result: R? = null
    entityStore.executeInTransaction { tx ->
      result = block(tx)
    }
    return result!!
  }

  fun create(entityTypeName: String, populate: Entity.() -> Unit) = transact { tx ->
    val entity = tx.newEntity(entityTypeName)
    populate(entity)
  }

  fun getAll(entityTypeName: String) =
    transact { tx -> tx.getAll(entityTypeName) }

  fun <T> find(entityTypeName: String, propertyName: String, value: Comparable<T>): EntityIterable =
    transact { tx -> tx.find(entityTypeName, propertyName, value) }

  override fun close() {
    entityStore.close()
  }
}
