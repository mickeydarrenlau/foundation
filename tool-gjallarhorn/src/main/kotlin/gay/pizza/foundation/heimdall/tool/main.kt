package gay.pizza.foundation.heimdall.tool

import gay.pizza.foundation.heimdall.tool.commands.BlockChangeTimelapseCommand
import gay.pizza.foundation.heimdall.tool.commands.ChunkExportLoaderCommand
import gay.pizza.foundation.heimdall.tool.commands.PlayerPositionExport
import gay.pizza.foundation.heimdall.tool.commands.PlayerSessionExport
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) = GjallarhornCommand().subcommands(
  BlockChangeTimelapseCommand(),
  PlayerSessionExport(),
  PlayerPositionExport(),
  ChunkExportLoaderCommand()
).main(args)
