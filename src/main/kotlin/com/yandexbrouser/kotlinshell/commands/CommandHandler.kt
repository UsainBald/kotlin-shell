package com.yandexbrouser.kotlinshell.commands

import com.yandexbrouser.kotlinshell.filesystem.VirtualFileSystem

class CommandHandler(private val virtualFileSystem: VirtualFileSystem) {
  private val history = mutableListOf<String>()
  private val startTime = System.currentTimeMillis()

  fun handleCommand(input: String): String {
    history.add(input)

    val parts = input.split(" ")
    val commandName = parts[0]
    val args = parts.drop(1)

    return when (commandName) {
      "ls" -> LsCommand(virtualFileSystem).execute(args)
      "cd" -> CdCommand(virtualFileSystem).execute(args)
      "exit" -> ExitCommand().execute(args)
      "wc" -> WcCommand(virtualFileSystem).execute(args)
      "history" -> HistoryCommand(history).execute(args)
      "uptime" -> UptimeCommand(startTime).execute(args)
      else -> "Unknown command: $commandName"
    }
  }
}
