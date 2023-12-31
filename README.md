# Foundation

Foundation is a set of plugins that implement the core functionality for a small community Minecraft
server.

## Plugins

* foundation-core: Core functionality
* foundation-bifrost: Discord chat bridge
* foundation-chaos: Simulate chaos inside a minecraft world
* foundation-heimdall: Event tracking
* foundation-tailscale: Connect the Minecraft Server to Tailscale

## Tools

* tool-gjallarhorn - Heimdall swiss army knife
* 

## Libraries

* common-all: Common code for every Foundation module.
* common-plugin: Common code for every Foundation plugin. Included directly in the plugin jar.
* common-heimdall: Common code for Heimdall modules.
* foundation-shared: Common code for every Foundation plugin. Linked dynamically from Foundation Core.

## Installation

The following command downloads and runs a script that will fetch the latest update manifest, and
install all plugins available. It can also be used to update plugins to the latest version
available.

```bash
# Always validate the contents of a script from the internet!
bash -c "$(curl -sL https://github.com/GayPizzaSpecifications/foundation/raw/main/install.sh)"
```
