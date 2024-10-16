package com.yandexbrouser.kotlinshell.commands

interface Command {
  fun execute(args: List<String>): String
}
