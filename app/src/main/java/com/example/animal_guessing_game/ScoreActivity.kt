package com.example.animal_guessing_game


import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ScoreActivity : AppCompatActivity() {

    private lateinit var textViewScoreTwo: TextView
    private lateinit var buttonAddScore: Button
    private lateinit var buttonStartAnew: Button
    private lateinit var buttonProfile: Button
    private lateinit var editTextNote: EditText

    companion object {
        val ENTRIES_KEY = stringPreferencesKey("user_entries")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        textViewScoreTwo = findViewById(R.id.textViewScoreTwo)
        buttonAddScore = findViewById(R.id.buttonAddScore)
        buttonStartAnew = findViewById(R.id.buttonStartAnew)
        buttonProfile = findViewById(R.id.buttonProfile)
        editTextNote = findViewById(R.id.editTextNote)

        val defaultNoteText = getString(R.string.editTextNote)

        editTextNote.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (editTextNote.text.toString() == defaultNoteText) {
                    editTextNote.setText(getString(R.string.EmptyString))
                }
            } else {
                if (editTextNote.text.toString().isEmpty()) {
                    editTextNote.setText(defaultNoteText)
                }
            }
        }

        try {
            val score = intent.getIntExtra("SCORE", 0)

            textViewScoreTwo.text = getString(R.string.score_text, score)

            buttonAddScore.setOnClickListener {
                val noteText = editTextNote.text.toString()
                if (noteText.isNotBlank() && noteText != defaultNoteText) {
                    saveData(this, score, noteText)
                    Toast.makeText(this, getString(R.string.score_saved), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.note_empty_warning), Toast.LENGTH_SHORT)
                        .show()
                }
            }

            buttonStartAnew.setOnClickListener {
                try {
                    val intent = Intent(this, RoundActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        getString(R.string.error_starting_new_round, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            buttonProfile.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                getString(R.string.error_initializing_score_activity, e.message),
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToRoundActivity()
    }

    private fun navigateToRoundActivity() {
        val intent = Intent(this, RoundActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveData(context: Context, score: Int, noteText: String) {
        val timeZoneBerlin = TimeZone.getTimeZone("Europe/Berlin")
        val dateFormat = SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = timeZoneBerlin
        val currentDate = dateFormat.format(Date())
        val entry = ScoreEntry(score, noteText, currentDate)
        runBlocking {
            context.dataStore.edit { preferences ->
                val entriesJson = preferences[ENTRIES_KEY] ?: "[]"
                val entries: MutableList<ScoreEntry> =
                    Gson().fromJson(entriesJson, Array<ScoreEntry>::class.java).toMutableList()
                entries.add(0, entry)
                if (entries.size > 5) entries.removeAt(5)
                preferences[ENTRIES_KEY] = Gson().toJson(entries)
            }
        }
    }


}
