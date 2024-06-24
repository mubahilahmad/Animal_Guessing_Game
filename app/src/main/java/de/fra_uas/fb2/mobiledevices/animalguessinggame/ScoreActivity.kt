package de.fra_uas.fb2.mobiledevices.animalguessinggame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_preferences")

class ScoreActivity : AppCompatActivity() {

    private lateinit var scoreTextView: TextView
    private lateinit var addScoreButton: Button
    private lateinit var startAnewButton: Button

    companion object {
        val POINTS_KEY = intPreferencesKey("user_points")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        scoreTextView = findViewById(R.id.textViewScore)
        addScoreButton = findViewById(R.id.buttonAddScore)
        startAnewButton = findViewById(R.id.buttonStartAnew)

        val score = intent.getIntExtra("SCORE", 0)
        val savedScore = getPoints(this)

        scoreTextView.text = getString(R.string.score_text, savedScore + score)

        addScoreButton.setOnClickListener {
            savePoints(this, savedScore + score)
            Toast.makeText(this, getString(R.string.score_saved), Toast.LENGTH_SHORT).show()
        }

        startAnewButton.setOnClickListener {
            val intent = Intent(this, RoundActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun savePoints(context: Context, points: Int) {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences[POINTS_KEY] = points
            }
        }
    }

    private fun getPoints(context: Context): Int {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            preferences[POINTS_KEY] ?: 0
        }
    }
}
