package com.yandexbrouser.kotlinshell.commands

import com.yandexbrouser.kotlinshell.filesystem.VirtualFileSystem

class CdCommand(private val fileSystem: VirtualFileSystem) : Command {
  override fun execute(args: List<String>): String {
    if (args.isEmpty()) return "No directory specified."
    val success = fileSystem.changeDirectory(args[0])
    return if (success) "Changed directory to ${args[0]}" else "Directory not found."
  }
}
