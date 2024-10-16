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
    val tarFile = File(tarPath)
    if (tarFile.exists()) {
      extractTar(tarPath)
    } else {
      throw IllegalArgumentException("Tar file not found: $tarPath")
    }

    if (root.isNotEmpty()) {
      currentDirectory = root.keys.first()
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
        // If it's a directory, add it to the root map
        root.computeIfAbsent(entryName) { mutableListOf() }
      } else {
        // It's a file, extract its parent directory and add the file to it
        val parentDir = entryName.substringBeforeLast('/', "")
        val fileName = entryName.substringAfterLast('/')

        if (parentDir.isNotEmpty()) {
          root.computeIfAbsent(parentDir) { mutableListOf() }
          root[parentDir]?.add(fileName)
        } else {
          // Root directory files
          root.computeIfAbsent("/") { mutableListOf() }
          root["/"]?.add(fileName)
        }

        // Store the file's content in the fileContents map
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

  fun readFileContent(fileName: String): String? {
    val fullPath = if (currentDirectory == "/") fileName else "$currentDirectory/$fileName"
    return fileContents[fullPath]
  }

  fun listFiles(): List<String> {
    val cleanCurrentDir = currentDirectory.trimEnd('/')
    val files = root[cleanCurrentDir] ?: emptyList()
    val subdirectories = root.keys.filter { it.startsWith("$cleanCurrentDir/") && it != cleanCurrentDir }
      .map { it.removePrefix("$cleanCurrentDir/").split("/").first() }
      .distinct()

    return files + subdirectories
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
      currentDirectory = newPath
      println("Changed directory to: $newPath")
      true
    } else {
      println("Directory not found: $newPath")
      false
    }
  }
}
