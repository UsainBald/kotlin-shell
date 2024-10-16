package com.yandexbrouser.kotlinshell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.yandexbrouser.kotlinshell.commands.CommandHandler
import com.yandexbrouser.kotlinshell.filesystem.VirtualFileSystem
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import java.io.File

class ShellEmulator(
  private val computerName: String,
  tarPath: String,
  private val startScriptPath: String // Accept the start script path
) {
  private val fileSystem = VirtualFileSystem(tarPath)
  private val commandHandler = CommandHandler(fileSystem)

  fun start() {
    application {
      Window(onCloseRequest = ::exitApplication, title = "$computerName Shell") {
        ShellGUI(computerName, commandHandler, startScriptPath)
      }
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShellGUI(computerName: String, commandHandler: CommandHandler, startScriptPath: String) {
  var inputText by remember { mutableStateOf("") }
  var outputText by remember { mutableStateOf("") }
  val scrollState = rememberScrollState()
  val keyboardController = LocalSoftwareKeyboardController.current

  // Execute the startup script when the shell starts
  LaunchedEffect(Unit) {
    executeStartupScript(startScriptPath, commandHandler, computerName) { result ->
      outputText += result
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
      .padding(16.dp)
  ) {
    Box(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .fillMaxWidth()
        .background(Color.Black)
    ) {
      Text(
        text = outputText,
        modifier = Modifier.padding(8.dp),
        fontSize = 14.sp,
        color = Color.White
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
    ) {
      OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        label = { Text("Enter Command", color = Color.White) },
        modifier = Modifier
          .weight(1f)
          .onKeyEvent { event ->
            if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
              executeCommand(inputText, commandHandler, computerName) { result ->
                outputText += result
                inputText = ""
              }
              true
            } else {
              false
            }
          },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
          executeCommand(inputText, commandHandler, computerName) { result ->
            outputText += result
            inputText = ""
          }
          keyboardController?.hide()
        }),
        colors = TextFieldDefaults.outlinedTextFieldColors(
          textColor = Color.White,
          backgroundColor = Color.Black,
          focusedBorderColor = Color.White,
          unfocusedBorderColor = Color.Gray
        )
      )
      Spacer(modifier = Modifier.width(8.dp))
      Button(
        onClick = {
          executeCommand(inputText, commandHandler, computerName) { result ->
            outputText += result
            inputText = ""
          }
        },
        modifier = Modifier.align(Alignment.CenterVertically)
      ) {
        Text("Send")
      }
    }
  }
}

// Function to execute commands from the startup script
fun executeStartupScript(
  startScriptPath: String,
  commandHandler: CommandHandler,
  computerName: String,
  onCommandExecuted: (String) -> Unit
) {
  val scriptFile = File(startScriptPath)
  if (scriptFile.exists()) {
    val commands = scriptFile.readLines() // Read all the lines (commands) from the script
    commands.forEach { command ->
      if (command.isNotBlank()) {
        val result = commandHandler.handleCommand(command)
        onCommandExecuted("$computerName> $command\n$result\n")
      }
    }
  } else {
    onCommandExecuted("Startup script not found: $startScriptPath\n")
  }
}

// Function to handle user-entered commands
fun executeCommand(
  inputText: String,
  commandHandler: CommandHandler,
  computerName: String,
  onCommandExecuted: (String) -> Unit
) {
  val cleanedInput = inputText.trimEnd('\n').trim()
  if (cleanedInput.isNotEmpty()) {
    val result = commandHandler.handleCommand(cleanedInput)
    onCommandExecuted("$computerName> $cleanedInput\n$result\n")
  }
}
