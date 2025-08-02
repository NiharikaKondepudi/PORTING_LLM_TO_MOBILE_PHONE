package com.example.g355llm

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Changed the startDestination to "main"
    NavHost(navController, startDestination = "main") {
        // The "start" route is no longer needed
        composable("main") { MainScreen(navController) }
        composable("qa") { PromptScreen(task = "qa") }
        composable("calendar") { PromptScreen(task = "calendar") }
    }
}

// The entire StartScreen composable has been removed

@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Offline AI Assistant", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        val context = LocalContext.current

        Button(
            onClick = {
                val urlIntent =
                    android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("http://127.0.0.1:8081")
                    }
                context.startActivity(urlIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Question Answering")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("calendar") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calendar Tasks")
        }
    }
}

@Composable
fun PromptScreen(task: String) {
    var userPrompt by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("Prompt will be saved to file") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (task == "qa") "Question Answering" else "Calendar Tasks",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = userPrompt,
            onValueChange = { userPrompt = it },
            label = { Text("Enter your prompt") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                try {
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val promptFile = File(downloadsDir, "prompt.txt")

                    val finalPrompt = if (task == "calendar") {
                        """
    Please convert the following sentence into a JSON event object with this structure:

    {
      "summary": "Short title of the meeting",
      "description": "Detailed description of the event",
      "start": {
        "dateTime": "YYYY-MM-DDTHH:MM:SSZ",
        "timeZone": "Asia/Kolkata"
      },
      "end": {
        "dateTime": "YYYY-MM-DDTHH:MM:SSZ",
        "timeZone": "Asia/Kolkata"
      },
      "location": "Room or venue"
    }

    Text: "$userPrompt"

    ⚠ Respond ONLY with the JSON. Do not add explanation or examples.
    """.trimIndent()
                    } else {
                        userPrompt
                    }

                    promptFile.writeText(finalPrompt)
                    response = "Prompt saved and sent for processing."
                } catch (e: Exception) {
                    response = "❌ Failed to save prompt: ${e.message}"
                }
            }
        ) {
            Text("Send")
        }

        Text(
            text = response,
            modifier = Modifier.fillMaxWidth()
        )
    }
}