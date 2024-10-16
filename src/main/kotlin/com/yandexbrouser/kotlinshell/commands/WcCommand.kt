package com.yandexbrouser.kotlinshell.commands

import com.yandexbrouser.kotlinshell.filesystem.VirtualFileSystem

class WcCommand(private val fileSystem: VirtualFileSystem) : Command {
  override fun execute(args: List<String>): String {
    if (args.isEmpty()) {
      return "No file specified."
    }

    val fileName = args[0]
    val fileContent = fileSystem.readFileContent(fileName)

    return if (fileContent != null) {
      val wordCount = fileContent.split(Regex("\\s+")).filter { it.isNotEmpty() }.size
      "Word count: $wordCount"
    } else {
      "File not found: $fileName"
    }
  }
}
