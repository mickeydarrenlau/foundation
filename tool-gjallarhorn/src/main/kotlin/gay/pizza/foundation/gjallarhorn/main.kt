package gay.pizza.foundation.gjallarhorn

import com.github.ajalt.clikt.core.subcommands
import gay.pizza.foundation.gjallarhorn.commands.BlockChangeTimelapseCommand
import gay.pizza.foundation.gjallarhorn.commands.ChunkExportLoaderCommand
import gay.pizza.foundation.gjallarhorn.commands.PlayerPositionExport
import gay.pizza.foundation.gjallarhorn.commands.PlayerSessionExport

fun main(args: Array<String>) = GjallarhornCommand().subcommands(
  BlockChangeTimelapseCommand(),
  PlayerSessionExport(),
  PlayerPositionExport(),
  ChunkExportLoaderCommand()
).main(args)
