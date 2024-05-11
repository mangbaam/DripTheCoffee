package com.mangbaam.dripthecoffee

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mangbaam.dripthecoffee.model.IntRange
import com.mangbaam.dripthecoffee.model.Recipe
import com.mangbaam.dripthecoffee.ui.theme.DripTheCoffeeTheme

class MainActivity : ComponentActivity() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DripTheCoffeeTheme {
                var uiState: Recipe? by remember { mutableStateOf(null) }
                db.collection("recipe")
                    .get()
                    .addOnSuccessListener { result ->
                        result.documents.forEach {
                            Log.d("[MANGBAAM]", "recipeLowData: ${it.data}")
                            val recipe = it.data?.let {
                                Recipe(
                                    author = it["author"] as String,
                                    cups = (it["cups"] as? Long)?.toInt() ?: 1,
                                    isHot = (it["isHot"] as? Boolean) ?: true,
                                    videoUrl = it["video"] as? String,
                                    beanWeight = (it["beanWeight"] as Long).toInt(),
                                    waterTemperature = (it["waterTemperature"] as Map<String, Any>).let {
                                        IntRange(
                                            from = (it["from"] as Long).toInt(),
                                            to = (it["to"] as Long).toInt(),
                                            desc = it["desc"] as? String,
                                        )
                                    },
                                    pours = (it["recipe"] as List<Map<String, Any>>).map {
                                        val seconds = (it["seconds"] as Long).toInt()
                                        val water = (it["water"] as Long).toInt()
                                        val desc = it["desc"] as? String
                                        Recipe.Pour(seconds, water, desc)
                                    }
                                )
                            }
                            uiState = recipe
                            Log.d("[MANGBAAM]", "recipe: $recipe")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("[MANGBAAM]", "Error getting documents.", exception)
                    }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        uiState?.let {
                            Recipe(
                                modifier = Modifier.padding(innerPadding),
                                recipe = it,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Recipe(modifier: Modifier = Modifier, recipe: Recipe) {
    Column {
        Text(
            text = "${recipe.author}, ${recipe.cups}컵",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "원두량: ${recipe.beanWeight}g",
        )
        Text(text = "물 온도: ${recipe.waterTemperature.from} ~ ${recipe.waterTemperature.to} (${recipe.waterTemperature.desc})")

        Column(
            modifier = Modifier.padding(top = 24.dp),
        ) {
            recipe.pours.forEach {
                Text(text = "시간: ${it.seconds}초, 물: ${it.water}ml, ${it.desc}")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DripTheCoffeeTheme {
        Greeting("Android")
    }
}