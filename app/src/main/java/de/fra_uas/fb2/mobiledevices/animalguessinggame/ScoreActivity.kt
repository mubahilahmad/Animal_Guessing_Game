package de.fra_uas.fb2.mobiledevices.animalguessinggame

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_preferences")

class ScoreActivity : AppCompatActivity() {

    private lateinit var scoreTextView: TextView
    private lateinit var addScoreButton: Button
    private lateinit var startAnewButton: Button
    private lateinit var dataStoreTextView: TextView
    private lateinit var noteEditText: EditText

    companion object {
        val SCORE_KEY = stringPreferencesKey("user_score")
        val NOTE_KEY = stringPreferencesKey("user_note")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        scoreTextView = findViewById(R.id.textViewScore)
        addScoreButton = findViewById(R.id.buttonAddScore)
        startAnewButton = findViewById(R.id.buttonStartAnew)
        dataStoreTextView = findViewById(R.id.textViewDataStore)
        noteEditText = findViewById(R.id.editTextNote)

        noteEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                noteEditText.setText("")
            }
        }

        try {
            val score = intent.getIntExtra("SCORE", 0)
            val savedData = getSavedData(this)

            // Display the score from MainActivity
            scoreTextView.text = getString(R.string.score_text, score)

            // Display the saved data from DataStore
            dataStoreTextView.text = savedData

            addScoreButton.setOnClickListener {
                val note = noteEditText.text.toString()
                saveData(this, score, note)
                Toast.makeText(this, getString(R.string.score_saved), Toast.LENGTH_SHORT).show()

                // Update the DataStore TextView with the new saved data
                val newSavedData = getSavedData(this)
                dataStoreTextView.text = newSavedData
            }

            startAnewButton.setOnClickListener {
                try {
                    val intent = Intent(this, RoundActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, getString(R.string.error_starting_new_round, e.message), Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error_initializing_score_activity, e.message), Toast.LENGTH_LONG).show()
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


    private fun saveData(context: Context, score: Int, note: String) {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences[SCORE_KEY] = "Score: $score"
                preferences[NOTE_KEY] = note
            }
        }
    }

    private fun getSavedData(context: Context): String {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            val score = preferences[SCORE_KEY] ?: ""
            val note = preferences[NOTE_KEY] ?: ""
            "$note, $score"
        }
    }
}
