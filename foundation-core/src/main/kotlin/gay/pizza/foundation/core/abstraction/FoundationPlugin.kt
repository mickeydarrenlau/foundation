package gay.pizza.foundation.core.abstraction

import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module

abstract class FoundationPlugin : JavaPlugin() {
  private lateinit var pluginModule: Module
  private lateinit var pluginApplication: KoinApplication
  private lateinit var features: List<CoreFeature>
  private lateinit var module: Module

  override fun onEnable() {
    pluginModule = module {
      single { this@FoundationPlugin }
      single { server }
      single { config }
      single { slF4JLogger }
    }

    features = createFeatures()
    module = createModule()

    // TODO: If we have another plugin using Koin, we may need to use context isolation and ensure
    //  it uses the same context so they can fetch stuff from us.
    //  https://insert-koin.io/docs/reference/koin-core/context-isolation
    pluginApplication = startKoin {
      modules(pluginModule)
      modules(module)
    }

    // This is probably a bit of a hack.
    pluginApplication.modules(module {
      single { pluginApplication }
    })

    features.forEach {
      pluginApplication.modules(it.module())
    }

    features.forEach {
      try {
        slF4JLogger.info("Enabling feature: ${it.javaClass.simpleName}")
        it.enable()
        // TODO: May replace this check with a method in the interface, CoreFeature would no-op.
        if (it is Feature) {
          server.pluginManager.registerEvents(it, this)
        }
      } catch (e: Exception) {
        slF4JLogger.error("Failed to enable feature: ${it.javaClass.simpleName}", e)
      }
    }
  }

  override fun onDisable() {
    features.forEach {
      it.disable()
    }

    stopKoin()
  }

  protected open fun createModule() = module {}
  protected abstract fun createFeatures(): List<CoreFeature>
}
