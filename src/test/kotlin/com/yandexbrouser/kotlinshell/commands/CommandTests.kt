package com.yandexbrouser.kotlinshell.commands

import com.yandexbrouser.kotlinshell.createTestTarFile
import com.yandexbrouser.kotlinshell.deleteTestFile
import com.yandexbrouser.kotlinshell.filesystem.VirtualFileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommandTests {

  @Test
  fun `test ls command in root directory`() {
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
  fun `test ls command in empty directory`() {
    // Create a tar file with an empty directory
    val tarFile = createTestTarFile(listOf("emptyDir/" to ""))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Change to the empty directory and list files
    val cdCommand = CdCommand(fileSystem)
    cdCommand.execute(listOf("emptyDir"))

    val lsCommand = LsCommand(fileSystem)
    val result = lsCommand.execute(emptyList())

    assertTrue(result.contains("No files found"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `test ls command after navigating to subdirectory`() {
    // Create a tar file with a subdirectory and two files in it
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Content 1", "dir1/file2.txt" to "Content 2"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Change to the subdirectory and list files
    val cdCommand = CdCommand(fileSystem)
    cdCommand.execute(listOf("dir1"))

    val lsCommand = LsCommand(fileSystem)
    val result = lsCommand.execute(emptyList())

    assertTrue(result.contains("file1.txt"))
    assertTrue(result.contains("file2.txt"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `test cd command into subdirectory`() {
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Inside dir1"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val lsCommand = LsCommand(fileSystem)
    val lsResult = lsCommand.execute(emptyList())

    assertTrue(lsResult.contains("file1.txt"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `test cd command back to parent directory`() {
    // Create a tar file with a subdirectory and a file
    val tarFile = createTestTarFile(listOf("dir1/file1.txt" to "Inside dir1", "file2.txt" to "In root"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    // Change to subdirectory
    val cdCommand = CdCommand(fileSystem)
    cdCommand.execute(listOf("dir1"))

    // Change back to the parent directory
    cdCommand.execute(listOf(".."))

    // List files and ensure the file in the root directory is listed
    val lsCommand = LsCommand(fileSystem)
    val lsResult = lsCommand.execute(emptyList())

    assertTrue(lsResult.contains("file2.txt"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `test cd command to non-existent directory`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "Content"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val cdCommand = CdCommand(fileSystem)
    val result = cdCommand.execute(listOf("nonexistent"))

    assertTrue(result.contains("Directory not found"))

    deleteTestFile(tarFile)
  }

  @Test
  fun `test wc command for word count in a file`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "Hello world this is a test"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val wcCommand = WcCommand(fileSystem)
    val result = wcCommand.execute(listOf("file1.txt"))

    assertEquals("Word count: 6", result)

    deleteTestFile(tarFile)
  }

  @Test
  fun `test wc command for empty file`() {
    // Create a tar file with an empty file
    val tarFile = createTestTarFile(listOf("emptyFile.txt" to ""))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val wcCommand = WcCommand(fileSystem)
    val result = wcCommand.execute(listOf("emptyFile.txt"))

    assertEquals("Word count: 0", result)

    deleteTestFile(tarFile)
  }

  @Test
  fun `test wc command for non-existent file`() {
    val tarFile = createTestTarFile(listOf("file1.txt" to "Content"))
    val fileSystem = VirtualFileSystem(tarFile.absolutePath)

    val wcCommand = WcCommand(fileSystem)
    val result = wcCommand.execute(listOf("nonexistent.txt"))

    assertEquals("File not found: nonexistent.txt", result)

    deleteTestFile(tarFile)
  }

  @Test
  fun `test uptime command`() {
    // Simulate the start time and run the uptime command after a delay
    val startTime = System.currentTimeMillis()

    // Simulate uptime by sleeping for 50ms
    Thread.sleep(50)

    val uptimeCommand = UptimeCommand(startTime)
    val result = uptimeCommand.execute(emptyList())

    // Ensure the uptime is at least 50 milliseconds
    val uptimeValue = result.removePrefix("Uptime: ").removeSuffix(" milliseconds").toLong()
    assertTrue(uptimeValue >= 50, "Expected uptime to be at least 50 milliseconds, but got $uptimeValue")
  }

  @Test
  fun `test history command`() {
    // Simulate a list of previously run commands
    val commandHistory = listOf("ls", "cd dir1", "wc file.txt")

    // Create a history command
    val historyCommand = HistoryCommand(commandHistory)

    // Execute the command and check the result
    val result = historyCommand.execute(emptyList())

    // Ensure the result matches the expected history
    val expectedHistory = "ls\ncd dir1\nwc file.txt"
    assertEquals(expectedHistory, result)
  }
}
