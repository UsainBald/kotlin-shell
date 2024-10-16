package com.yandexbrouser.kotlinshell.commands

import com.yandexbrouser.kotlinshell.filesystem.VirtualFileSystem

class LsCommand(private val fileSystem: VirtualFileSystem) : Command {
  override fun execute(args: List<String>): String {
    println("Executing ls command...")
    val files = fileSystem.listFiles()
    return files.joinToString("\n").ifEmpty { "No files found" }
  }
}
