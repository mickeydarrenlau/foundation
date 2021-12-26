package cloud.kubelet.foundation.gjallarhorn

import cloud.kubelet.foundation.gjallarhorn.commands.BlockLogReplay
import cloud.kubelet.foundation.gjallarhorn.commands.PlayerPositionExport
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) = GjallarhornCommand().subcommands(
  BlockLogReplay(),
  PlayerSessionExport(),
  PlayerPositionExport()
).main(args)
