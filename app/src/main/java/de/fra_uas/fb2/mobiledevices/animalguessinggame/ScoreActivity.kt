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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_preferences") // game... name vom datastore (zweck identifizieren von gespeicherten präferenzen)
// In ScoreActivity: Die Eigenschaft wird verwendet,
// um Daten zu speichern und abzurufen, indem sie einfach auf das dataStore-Eigenschaft des Kontextes zugreift.

class ScoreActivity : AppCompatActivity() {

    private lateinit var textViewScoreTwo: TextView
    private lateinit var buttonAddScore: Button
    private lateinit var buttonStartAnew: Button
    private lateinit var textViewDataStore: TextView
    private lateinit var editTextNote: EditText

    companion object {
        val SCORE_KEY = stringPreferencesKey("user_score")
        // // Definiert einen Schlüssel für die Benutzerscore-Präferenz
        val NOTE_KEY = stringPreferencesKey("user_note")
        // // Definiert einen Schlüssel für die Benutzernotiz-Präferenz
    } //companion object: Stellt sicher, dass die Schlüssel als statische Konstanten zur Klasse gehören
    // und ohne Instanz der Klasse verwendet werden können.
     // SCORE_KEY: Schlüssel für das Speichern und Abrufen des Benutzer-Scores.
     // NOTE_KEY: Schlüssel für das Speichern und Abrufen der Benutzernotiz.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        textViewScoreTwo = findViewById(R.id.textViewScoreTwo)
        buttonAddScore = findViewById(R.id.buttonAddScore)
        buttonStartAnew = findViewById(R.id.buttonStartAnew)
        textViewDataStore = findViewById(R.id.textViewDataStore)
        editTextNote = findViewById(R.id.editTextNote)

        editTextNote.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editTextNote.setText("")
            }
        }

        try {
            val score = intent.getIntExtra("SCORE", 0) // Holt den übergebenen Score aus dem Intent, Standardwert ist 0 (wenn intent leer, dann 0 damit nicht crash)
            val savedData = getSavedData(this) // Ruft die gespeicherten Daten aus dem DataStore ab
            // this ist eine Referenz auf die aktuell laufende Instanz von ScoreActivity

            // Display the score from MainActivity
            textViewScoreTwo.text = getString(R.string.score_text, score) // Zeigt den Score aus der MainActivity an

            // Display the saved data from DataStore
            textViewDataStore.text = savedData // Zeigt die gespeicherten Daten aus dem DataStore an

            buttonAddScore.setOnClickListener {
                val note = editTextNote.text.toString() // Holt die Notiz aus dem Eingabefeld
                saveData(this, score, note) // Speichert den Score und die Notiz im DataStore
                Toast.makeText(this, getString(R.string.score_saved), Toast.LENGTH_SHORT).show() // Zeigt eine Bestätigungsmeldung an

                // Update the DataStore TextView with the new saved data
                val newSavedData = getSavedData(this) // Ruft die aktualisierten gespeicherten Daten ab
                textViewDataStore.text = newSavedData // Aktualisiert die Anzeige mit den neuen gespeicherten Daten
            }

            buttonStartAnew.setOnClickListener {
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
        runBlocking { // Startet eine blockierende Coroutine, um sicherzustellen, dass die Daten synchron gespeichert werden
            // aktuelle Thread wird blockiert, bis die Coroutine abgeschlossen ist. das stellt sicher, dass die Daten synchron gespeichert werden, bevor der Code weiter ausgeführt wird.
            context.dataStore.edit { preferences -> // Öffnet den DataStore zum Bearbeiten der Präferenzen
                // Öffnet den DataStore im Bearbeitungsmodus. Die edit-Methode gibt ein MutablePreferences-Objekt zurück, das es ermöglicht, die gespeicherten Werte zu ändern
                preferences[SCORE_KEY] = "Score: $score" // Speichert den Score (im DataStore) als String unter dem Schlüssel SCORE_KEY, zb Score: 10
                preferences[NOTE_KEY] = note // Speichert die Notiz (im DataStore) unter dem Schlüssel NOTE_KEY, auch als string
                // Preferences ist eine Schnittstelle in Android's DataStore-API, die Schlüssel-Wert-Paare speichert im datastore
                // preferences: Dies ist ein Objekt vom Typ MutablePreferences, das in der edit-Funktion bereitgestellt wird.
            }   // preferences(objekt) -> [schlüssel] und operator [] -> (überlädt um wert in datastore zu speichern)
            // Die eckigen Klammern werden verwendet, um auf den Wert zuzugreifen oder einen Wert in einer Preferences-Instanz zu setzen
            // Syntax: Die eckigen Klammern sind eine vereinfachte Syntax für die get- und set-Methoden, die das Preferences-Interface implementiert.
        }
    } // score hat den Wert 10, dann wird "Score: 10" als String gespeichert.

    // Zugriff: val score = preferences[SCORE_KEY]
    //Setzen: preferences[SCORE_KEY] = "Score: $score"



    private fun getSavedData(context: Context): String {
        return runBlocking {
            val preferences = context.dataStore.data.first() // Holt die ersten gespeicherten Präferenzen aus dem DataStore
            val score = preferences[SCORE_KEY] ?: "" // Holt den gespeicherten Score oder einen leeren String, wenn nicht vorhanden
            val note = preferences[NOTE_KEY] ?: "" // Holt die gespeicherte Notiz oder einen leeren String, wenn nicht vorhanden
            "$note, $score" // Kombiniert die Notiz und den Score zu einem String
        }
    }

}
