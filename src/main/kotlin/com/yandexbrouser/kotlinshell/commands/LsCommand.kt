package com.yandexbrouser.kotlinshell.commands

import com.yandexbrouser.kotlinshell.filesystem.VirtualFileSystem

class LsCommand(private val fileSystem: VirtualFileSystem) : Command {
  override fun execute(args: List<String>): String {
    println("Executing ls command...")

    val originalDirectoryDepth = fileSystem.getCurrentDirectoryDepth()
    val path = args.firstOrNull()

    return if (path != null) {
      if (fileSystem.changeDirectory(path)) {
        val files = fileSystem.listFiles()
        val newDirectoryDepth = fileSystem.getCurrentDirectoryDepth()
        val depthDifference = newDirectoryDepth - originalDirectoryDepth

        repeat(depthDifference) {
          fileSystem.changeDirectory("..")
        }
        files.joinToString("\n").ifEmpty { "No files found" }
      } else {
        "Directory not found: $path"
      }
    } else {
      val files = fileSystem.listFiles()
      files.joinToString("\n").ifEmpty { "No files found" }
    }
  }
}
