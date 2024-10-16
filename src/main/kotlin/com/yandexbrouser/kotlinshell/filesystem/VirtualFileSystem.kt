package com.yandexbrouser.kotlinshell.filesystem

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class VirtualFileSystem(tarPath: String) {
  private val root = mutableMapOf<String, MutableList<String>>()
  private val fileContents = mutableMapOf<String, String>()
  private var currentDirectory: String = "/"

  init {
    extractTar(tarPath)

    // Set currentDirectory to the first directory in the map (if any)
    if (root.isNotEmpty()) {
      currentDirectory = root.keys.first() // Set to the first directory found
      println("Set initial directory to: $currentDirectory")
    }
  }

  private fun extractTar(tarPath: String) {
    val tarInputStream = TarArchiveInputStream(FileInputStream(tarPath))
    var entry = tarInputStream.nextTarEntry

    println("Starting to extract TAR file: $tarPath")

    while (entry != null) {
      val entryName = entry.name.trimStart('/').trimEnd('/')
      println("Extracting: $entryName (is directory: ${entry.isDirectory})")

      if (entry.isDirectory) {
        root[entryName] = mutableListOf()
      } else {
        val parentDir = entryName.substringBeforeLast('/')
        root.computeIfAbsent(parentDir) { mutableListOf() }
        root[parentDir]?.add(entryName.substringAfterLast('/'))

        // Store file contents in memory for later use
        val outputStream = ByteArrayOutputStream()
        tarInputStream.copyTo(outputStream)
        fileContents[entryName] = outputStream.toString()
      }

      entry = tarInputStream.nextTarEntry
    }

    println("Extracted file structure:")
    root.forEach { (dir, files) ->
      println("Directory: $dir")
      files.forEach { file ->
        println("  File: $file")
      }
    }
  }

  // Return the file content as a string if the file exists in the current directory
  fun readFileContent(fileName: String): String? {
    val fullPath = if (currentDirectory == "/") fileName else "$currentDirectory/$fileName"
    return fileContents[fullPath]
  }

  fun listFiles(): List<String> {
    val cleanCurrentDir = currentDirectory.trimEnd('/')
    println("Listing files and directories in: $cleanCurrentDir")

    val files = root[cleanCurrentDir] ?: emptyList()

    val subdirectories = root.keys.filter { it.startsWith("$cleanCurrentDir/") && it != cleanCurrentDir }
      .map { it.removePrefix("$cleanCurrentDir/").split("/").first() }
      .distinct()

    val combinedList = files + subdirectories

    if (combinedList.isEmpty()) {
      println("No files or directories found in $cleanCurrentDir")
    } else {
      println("Found files and directories in $cleanCurrentDir: ${combinedList.joinToString(", ")}")
    }

    return combinedList
  }

  fun changeDirectory(path: String): Boolean {
    val cleanCurrentDir = currentDirectory.trimEnd('/')
    val newPath = if (path == "..") {
      File(cleanCurrentDir).parent ?: "/"
    } else {
      val targetPath = if (cleanCurrentDir == "/") path else "$cleanCurrentDir/$path"
      targetPath.trimEnd('/')
    }

    return if (root.containsKey(newPath)) {
      println("Changed directory to: $newPath")
      currentDirectory = newPath
      true
    } else {
      println("Directory not found: $newPath")
      false
    }
  }
}
