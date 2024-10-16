package com.yandexbrouser.kotlinshell.filesystem

import com.yandexbrouser.kotlinshell.createTestTarFile
import com.yandexbrouser.kotlinshell.deleteTestFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNull

class VirtualFileSystemTest {

  @Test
  fun `test extracting and storing files from TAR`() {
    // Create a tar file with two files in the root directory
    val tarFile = createTestTarFile(listOf("file1.txt" to "Content 1", "file2.txt" to "Content 2"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Test that files are extracted correctly and stored in the root directory
    val files = fileSystem.listFiles()
    assertEquals(listOf("file1.txt", "file2.txt"), files)

    deleteTestFile(tarFile)
  }

  @Test
  fun `test listing files in a subdirectory`() {
    // Create a tar file with a directory and two files inside it
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Content 1", "dir1/file2.txt" to "Content 2"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Change to the subdirectory and list files
    fileSystem.changeDirectory("dir1")
    val files = fileSystem.listFiles()
    assertEquals(listOf("file1.txt", "file2.txt"), files)

    deleteTestFile(tarFile)
  }

  @Test
  fun `test changing directory to non-existent directory`() {
    // Create a tar file with one file
    val tarFile = createTestTarFile(listOf("file1.txt" to "Content"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Try changing to a non-existent directory
    val result = fileSystem.changeDirectory("nonexistent")
    assertTrue(!result)

    deleteTestFile(tarFile)
  }

  @Test
  fun `test changing directory back to parent`() {
    // Create a tar file with a directory and one file in both the root and the directory
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Inside dir1", "file2.txt" to "In root"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Change to the subdirectory
    fileSystem.changeDirectory("dir1")

    // Change back to the parent directory
    val result = fileSystem.changeDirectory("..")
    assertTrue(result)

    // List files and ensure we are back in the root directory
    val files = fileSystem.listFiles()
    assertTrue(files.contains("file2.txt"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `test reading file content`() {
    // Create a tar file with one text file
    val tarFile = createTestTarFile(listOf("file1.txt" to "File content here"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Read the content of file1.txt
    val content = fileSystem.readFileContent("file1.txt")
    assertEquals("File content here", content)

    deleteTestFile(tarFile)
  }

  @Test
  fun `test reading non-existent file`() {
    // Create a tar file with one text file
    val tarFile = createTestTarFile(listOf("file1.txt" to "File content here"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Try to read a non-existent file
    val content = fileSystem.readFileContent("nonexistent.txt")
    assertNull(content)

    deleteTestFile(tarFile)
  }

  @Test
  fun `test listing files in empty directory`() {
    // Create a tar file with an empty directory
    val tarFile = createTestTarFile(listOf("emptyDir/" to ""))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Change to the empty directory and list files
    fileSystem.changeDirectory("emptyDir")
    val files = fileSystem.listFiles()

    assertTrue(files.isEmpty())

    deleteTestFile(tarFile)
  }
}
