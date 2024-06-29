package com.example.animal_guessing_game


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.gson.Gson
import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ProfileActivity : AppCompatActivity() {

    private lateinit var textViewEntries: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        textViewEntries = findViewById(R.id.textViewEntries)

        val entries = getSavedData(this)
        displaySavedData(entries)
    }

    private fun displaySavedData(entries: List<ScoreEntry>) {
        val displayText = entries.joinToString("\n\n") { entry ->
            "${entry.date}\nNote: ${entry.note},\nScore: ${entry.score}"
        }
        textViewEntries.text = displayText
    }

    private fun getSavedData(context: Context): List<ScoreEntry> {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            val entriesJson = preferences[ScoreActivity.ENTRIES_KEY] ?: "[]"
            return@runBlocking Gson().fromJson(entriesJson, Array<ScoreEntry>::class.java).toList()
        }
    }
}
