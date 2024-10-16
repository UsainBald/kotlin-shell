package com.yandexbrouser.kotlinshell.commands

class ExitCommand : Command {
  override fun execute(args: List<String>): String {
    System.exit(0)
    return "Exiting..."
  }
}
