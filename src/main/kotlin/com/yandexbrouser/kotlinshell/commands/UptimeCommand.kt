package com.yandexbrouser.kotlinshell.commands

class UptimeCommand(private val startTime: Long) : Command {
  override fun execute(args: List<String>): String {
    val uptime = System.currentTimeMillis() - startTime
    return "Uptime: $uptime milliseconds"
  }
}
