package com.yandexbrouser.kotlinshell

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

// Helper function to create a temporary tar file for testing
fun createTestTarFile(files: List<Pair<String, String>>): File {
  val tempTarFile = Files.createTempFile("test_virtualfs", ".tar").toFile()  // Create temp tar file
  TarArchiveOutputStream(FileOutputStream(tempTarFile)).use { tarOut ->
    files.forEach { (filePath, content) ->
      // Create directories if necessary
      val tempFile = Files.createTempFile("tempfile", ".txt").toFile()
      tempFile.writeText(content)

      // Add the file to the tar archive under "test_virtualfs/"
      tarOut.putArchiveEntry(tarOut.createArchiveEntry(tempFile, "test_virtualfs/$filePath"))
      tempFile.inputStream().use { it.copyTo(tarOut) }
      tarOut.closeArchiveEntry()

      // Clean up the temporary individual file after adding to tar
      tempFile.delete()
    }
  }
  return tempTarFile  // Return the temporary tar file
}

// Helper function to delete the test tar file
fun deleteTestFile(file: File) {
  if (file.exists()) {
    file.delete()
  }
}

