package com.example.g355llm.ui



import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun PromptScreen(mode: String) {
    val context = LocalContext.current
    var prompt by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Enter prompt") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            runPrompt(prompt, context, mode) { response ->
                result = response
            }
        }) {
            Text("Submit")
        }

        Spacer(Modifier.height(16.dp))

        Text(text = result)
    }
}

private fun runPrompt(prompt: String, context: Context, mode: String, callback: (String) -> Unit) {
    val promptFile = File("/data/data/com.termux/files/home/llm_input/prompt.txt")
    promptFile.writeText(prompt)

    Thread {
        try {
            Runtime.getRuntime().exec(arrayOf("sh", "-c", "bash ~/run_orca.sh")).waitFor()

            if (mode == "calendar") {
                Runtime.getRuntime().exec(arrayOf("sh", "-c", "bash ~/calendar_creator.sh")).waitFor()
            }

            val outputFile = File("/data/data/com.termux/files/home/llm_output/response_pretty.txt")
            val response = outputFile.readText()

            (context as android.app.Activity).runOnUiThread {
                callback(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.start()
}
