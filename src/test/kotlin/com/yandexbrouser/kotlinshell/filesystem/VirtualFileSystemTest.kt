package com.yandexbrouser.kotlinshell.filesystem

import com.yandexbrouser.kotlinshell.createTestTarFile
import com.yandexbrouser.kotlinshell.deleteTestFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNull

class VirtualFileSystemTest {

  @Test
  fun `should extract and store files from TAR`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "Content 1", "file2.txt" to "Content 2"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val files = fileSystem.listFiles()
    assertEquals(listOf("file1.txt", "file2.txt"), files)

    deleteTestFile(tarFile)
  }

  @Test
  fun `should list files in a subdirectory`() {
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Content 1", "dir1/file2.txt" to "Content 2"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    fileSystem.changeDirectory("dir1")
    val files = fileSystem.listFiles()
    assertEquals(listOf("file1.txt", "file2.txt"), files)

    deleteTestFile(tarFile)
  }

  @Test
  fun `should not change to non-existent directory`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "Content"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val result = fileSystem.changeDirectory("nonexistent")
    assertTrue(!result)

    deleteTestFile(tarFile)
  }

  @Test
  fun `should change directory back to parent`() {
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Inside dir1", "file2.txt" to "In root"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    fileSystem.changeDirectory("dir1")
    val result = fileSystem.changeDirectory("..")
    assertTrue(result)

    val files = fileSystem.listFiles()
    assertTrue(files.contains("file2.txt"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `should read file content`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "File content here"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val content = fileSystem.readFileContent("file1.txt")
    assertEquals("File content here", content)

    deleteTestFile(tarFile)
  }

  @Test
  fun `should return null for non-existent file`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "File content here"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val content = fileSystem.readFileContent("nonexistent.txt")
    assertNull(content)

    deleteTestFile(tarFile)
  }

  @Test
  fun `should list files in empty directory`() {
    val tarFile = createTestTarFile(listOf("emptyDir/" to ""))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    fileSystem.changeDirectory("emptyDir")
    val files = fileSystem.listFiles()

    assertTrue(files.isEmpty())

    deleteTestFile(tarFile)
  }
}
