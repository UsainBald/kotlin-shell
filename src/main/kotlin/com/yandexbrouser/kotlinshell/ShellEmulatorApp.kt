package com.yandexbrouser.kotlinshell

fun main(args: Array<String>) {
  if (args.size < 3) {
    println("Usage: <computer name> <path to tar archive> <path to start script>")
    return
  }

  val computerName = args[0]
  val tarPath = args[1]
  val startScriptPath = args[2]

  ShellEmulator(computerName, tarPath, startScriptPath).start()
}
