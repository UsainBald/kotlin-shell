package com.yandexbrouser.kotlinshell.commands

import com.yandexbrouser.kotlinshell.createTestTarFile
import com.yandexbrouser.kotlinshell.deleteTestFile
import com.yandexbrouser.kotlinshell.filesystem.VirtualFileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommandTests {

  @Test
  fun `should list files in root directory`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "Hello world!", "file2.txt" to "Kotlin test"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val lsCommand = LsCommand(fileSystem)
    val result = lsCommand.execute(emptyList())
    println(fileSystem.listFiles())

    assertTrue(result.contains("file1.txt"))
    assertTrue(result.contains("file2.txt"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `should list files in empty directory`() {
    val tarFile = createTestTarFile(listOf("emptyDir/" to ""))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val cdCommand = CdCommand(fileSystem)
    cdCommand.execute(listOf("emptyDir"))

    val lsCommand = LsCommand(fileSystem)
    val result = lsCommand.execute(emptyList())

    assertTrue(result.contains("No files found"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `should list files after navigating to subdirectory`() {
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Content 1", "dir1/file2.txt" to "Content 2"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val cdCommand = CdCommand(fileSystem)
    cdCommand.execute(listOf("dir1"))

    val lsCommand = LsCommand(fileSystem)
    val result = lsCommand.execute(emptyList())

    assertTrue(result.contains("file1.txt"))
    assertTrue(result.contains("file2.txt"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `should navigate into subdirectory`() {
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Inside dir1"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val lsCommand = LsCommand(fileSystem)
    val lsResult = lsCommand.execute(emptyList())

    assertTrue(lsResult.contains("file1.txt"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `should navigate back to parent directory`() {
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Inside dir1", "file2.txt" to "In root"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val cdCommand = CdCommand(fileSystem)
    cdCommand.execute(listOf("dir1"))
    cdCommand.execute(listOf(".."))

    val lsCommand = LsCommand(fileSystem)
    val lsResult = lsCommand.execute(emptyList())

    assertTrue(lsResult.contains("file2.txt"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `should not navigate to non-existent directory`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "Content"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val cdCommand = CdCommand(fileSystem)
    val result = cdCommand.execute(listOf("nonexistent"))

    assertTrue(result.contains("Directory not found"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `should count words in a file`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "Hello world this is a test"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val wcCommand = WcCommand(fileSystem)
    val result = wcCommand.execute(listOf("file1.txt"))

    assertEquals("Word count: 6", result)

    deleteTestFile(tarFile)
  }

  @Test
  fun `should count words in empty file`() {
    val tarFile = createTestTarFile(listOf("emptyFile.txt" to ""))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val wcCommand = WcCommand(fileSystem)
    val result = wcCommand.execute(listOf("emptyFile.txt"))

    assertEquals("Word count: 0", result)

    deleteTestFile(tarFile)
  }

  @Test
  fun `should handle non-existent file for wc command`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "Content"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val wcCommand = WcCommand(fileSystem)
    val result = wcCommand.execute(listOf("nonexistent.txt"))

    assertEquals("File not found: nonexistent.txt", result)

    deleteTestFile(tarFile)
  }

  @Test
  fun `should display correct uptime`() {
    val startTime = System.currentTimeMillis()

    Thread.sleep(50)

    val uptimeCommand = UptimeCommand(startTime)
    val result = uptimeCommand.execute(emptyList())

    val uptimeValue = result.removePrefix("Uptime: ").removeSuffix(" milliseconds").toLong()
    assertTrue(uptimeValue >= 50, "Expected uptime to be at least 50 milliseconds, but got $uptimeValue")
  }

  @Test
  fun `should display command history`() {
    val commandHistory = listOf("ls", "cd dir1", "wc file.txt")

    val historyCommand = HistoryCommand(commandHistory)

    val result = historyCommand.execute(emptyList())

    val expectedHistory = "ls\ncd dir1\nwc file.txt"
    assertEquals(expectedHistory, result)
  }

  @Test
  fun `should return to original directory after listing contents of a different directory`() {
    val tarFile = createTestTarFile(
      listOf(
        "file1.txt" to "Content in root",
        "dir1/file2.txt" to "Content in dir1",
        "dir1/file3.txt" to "Another file in dir1"
      )
    )
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val initialDirectory = fileSystem.getCurrentDirectory()

    val lsCommand = LsCommand(fileSystem)
    val result = lsCommand.execute(listOf("dir1"))

    assertTrue(result.contains("file2.txt"))
    assertTrue(result.contains("file3.txt"))

    val finalDirectory = fileSystem.getCurrentDirectory()
    assertEquals(initialDirectory, finalDirectory, "The current directory should be the same as it was before the ls command.")

    deleteTestFile(tarFile)
  }
}
