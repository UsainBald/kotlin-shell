package com.yandexbrouser.kotlinshell.commands

class HistoryCommand(private val history: List<String>) : Command {
  override fun execute(args: List<String>): String {
    return history.joinToString("\n")
  }
}
