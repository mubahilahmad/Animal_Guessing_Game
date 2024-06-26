package de.fra_uas.fb2.mobiledevices.animalguessinggame

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var textViewAnimalHint: TextView // das ist neu
    private lateinit var buttonHint: Button // das ist neu
    private lateinit var textViewHintsCountLeft: TextView // das ist neu
    private lateinit var editTextEnterYourGuess: EditText // das ist neu
    private lateinit var buttonGuess: Button // das ist neu
    private lateinit var textViewAttemptsCountLeft: TextView // das ist neu
    private lateinit var textViewScore: TextView // das ist neu
    private lateinit var buttonSkip: Button // das ist neu
    private lateinit var textViewCurrentAnimal: TextView // das ist neu

    private var currentAnimalIndex = 0
    private var hintIndex = 0
    private var guessAttempts = 0
    private var score = 0
    private var numAnimals = 0
    private var animalsToPlay = listOf<Animal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewAnimalHint = findViewById(R.id.textViewAnimalHint) // das ist neu
        buttonHint = findViewById(R.id.buttonHint) // das ist neu
        textViewHintsCountLeft = findViewById(R.id.textViewHintsCountLeft) // das ist neu
        editTextEnterYourGuess = findViewById(R.id.editTextEnterYourGuess) // das ist neu
        buttonGuess = findViewById(R.id.buttonGuess) // das ist neu
        textViewAttemptsCountLeft = findViewById(R.id.textViewAttemptsCountLeft) // das ist neu
        textViewScore = findViewById(R.id.textViewScore) // das ist neu
        buttonSkip = findViewById(R.id.buttonSkip) // das ist neu
        textViewCurrentAnimal = findViewById(R.id.textViewCurrentAnimal) // das ist neu

        try {
            if (intent.hasExtra("NUM_ANIMALS")) {
                numAnimals = intent.getIntExtra("NUM_ANIMALS", 0)
            } else {
                Toast.makeText(this, getString(R.string.no_number_of_animals_selected), Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error_retrieving_num_animals, e.message), Toast.LENGTH_LONG).show()
            finish()
            return
        }


        animalsToPlay = Animal.animals.shuffled().take(numAnimals)
        /*In der MainActivity wurde das companion object der Animal-Klasse verwendet,
        um die Liste der Tiere zu erhalten, ohne eine Instanz der Animal-Klasse zu erstellen*/

        updateHint() // Aktualisiert den aktuellen Hinweis und den Namen des aktuellen Tieres
        updateHintCount() // Aktualisiert die Anzeige der verbleibenden Hinweise
        updateAttemptsCount() // Aktualisiert die Anzeige der verbleibenden Versuche

        buttonHint.setOnClickListener {
            hintIndex++ // Erhöht den Hinweisindex
            if (hintIndex < animalsToPlay[currentAnimalIndex].hints.size) {
                //hintIndex < Anzahl der Hinweise (hints.size), wenn nicht (else), keine weitere hinweise und button deaktivieren
                // Der nächste Hinweis kann angezeigt werden
                // hintIndex: Der aktuelle Index des angezeigten Hinweises.
                // animalsToPlay[currentAnimalIndex].hints: Die Liste der Hinweise für das aktuelle Tier
                // hints.size: Die Gesamtanzahl der Hinweise für das aktuelle Tier.
                textViewAnimalHint.text = animalsToPlay[currentAnimalIndex].hints[hintIndex] // Zeigt den nächsten Hinweis an
                updateHintCount() // Aktualisiert die Anzeige der verbleibenden Hinweise
            } else {
                disableHintButton() // Deaktiviert den Hinweis-Button
                Toast.makeText(this, getString(R.string.toastNoMoreHints), Toast.LENGTH_SHORT).show()
            // Zeigt eine Nachricht an, dass keine Hinweise mehr verfügbar sind
            }
        }

        // if (guess.equals(animalsToPlay[currentAnimalIndex].name, ignoreCase = true))
        // ignoreCase = true: Dieser Parameter sorgt dafür, dass der Vergleich der beiden Strings die Groß- und Kleinschreibung ignoriert.

        buttonGuess.setOnClickListener {
            val guess = editTextEnterYourGuess.text.toString() // Holt den Benutzereingabetext
            if (guess.equals(animalsToPlay[currentAnimalIndex].name, ignoreCase = true)) { // Überprüft, ob die Eingabe korrekt ist
                // Überprüft, ob die Benutzereingabe (guess) mit dem Namen des aktuellen Tieres übereinstimmt
                // `guess` ist der vom Benutzer eingegebene Text
                // `animalsToPlay[currentAnimalIndex].name` ist der Name des aktuellen Tieres
                // `ignoreCase = true` bedeutet, dass die Groß- und Kleinschreibung bei der Überprüfung ignoriert wird
                val pointsFromHints = hintIndex // Punkteabzug für verwendete Hinweise
                val pointsFromAttempts = guessAttempts // Punkteabzug für Versuche
                val pointsEarned = 13 - pointsFromHints - pointsFromAttempts // Berechnet die verdienten Punkte
                score += pointsEarned // Addiert die Punkte zum Gesamtscore
                textViewScore.text = getString(R.string.score, score) // Aktualisiert die Anzeige des Scores
                currentAnimalIndex++ // Wechselt zum nächsten Tier
                if (currentAnimalIndex < animalsToPlay.size) { // Überprüft, ob es weitere Tiere gibt
                    hintIndex = 0 // Setzt den Hinweisindex zurück
                    guessAttempts = 0 // Setzt die Versuchsanzahl zurück
                    updateHint() // Aktualisiert den Hinweis
                    editTextEnterYourGuess.text.clear() // Leert das Eingabefeld
                    resetGuessButton() // Aktiviert den Rate-Button
                    resetHintButton() // Aktiviert den Hinweis-Button
                    updateHintCount() // Aktualisiert die verbleibenden Hinweise
                    updateAttemptsCount() // Aktualisiert die verbleibenden Versuche
                    Toast.makeText(this, getString(R.string.toastCorrect), Toast.LENGTH_SHORT).show() // Zeigt eine Erfolgsmeldung an
                } else {
                    endGame() // Beendet das Spiel, wenn keine Tiere mehr übrig sind
                }
            } else {
                if (guessAttempts < 3) { // Überprüft, ob noch Versuche übrig sind
                    guessAttempts++ // Erhöht die Anzahl der Versuche
                    updateAttemptsCount() // Aktualisiert die verbleibenden Versuche
                    if (guessAttempts < 3) {
                        Toast.makeText(this, getString(R.string.toastWrong), Toast.LENGTH_SHORT).show() // Zeigt eine Fehlermeldung an
                    } else {
                        disableGuessButton() // Deaktiviert den Rate-Button, wenn keine Versuche mehr übrig sind
                        buttonSkip.visibility = View.VISIBLE // Zeigt den Überspringen-Button an
                    }
                }
            }
        }


        buttonSkip.setOnClickListener {
            currentAnimalIndex++ // Erhöht den Index des aktuellen Tieres
            if (currentAnimalIndex < animalsToPlay.size) { // Überprüft, ob es weitere Tiere gibt
                hintIndex = 0 // Setzt den Hinweisindex zurück
                guessAttempts = 0 // Setzt die Anzahl der Versuche zurück
                updateHint() // Aktualisiert den Hinweis
                editTextEnterYourGuess.text.clear() // Leert das Eingabefeld
                resetGuessButton() // Aktiviert den Rate-Button
                resetHintButton() // Aktiviert den Hinweis-Button
                updateHintCount() // Aktualisiert die verbleibenden Hinweise
                updateAttemptsCount() // Aktualisiert die verbleibenden Versuche
                buttonSkip.visibility = View.GONE // Verbirgt den Überspringen-Button
                Toast.makeText(this, getString(R.string.toastDontGiveUp), Toast.LENGTH_SHORT).show() // Zeigt eine Ermutigungsnachricht an
            } else {
                endGame() // Beendet das Spiel, wenn keine Tiere mehr übrig sind
            }
        }

    }

    override fun onBackPressed() {
        // Diese Methode ist außerhalb von onCreate(), da sie die Standardfunktionalität von onBackPressed() überschreibt
        // und automatisch vom System aufgerufen wird, wenn die Zurück-Taste gedrückt wird.
        super.onBackPressed()
        navigateToRoundActivity()
    }

    private fun navigateToRoundActivity() {
        val intent = Intent(this, RoundActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun endGame() {
        try {
            val intent = Intent(this, ScoreActivity::class.java) // Erstellt ein Intent, um zur ScoreActivity zu wechseln
            intent.putExtra("SCORE", score) // Fügt die erreichte Punktzahl als Extra hinzu
            startActivity(intent) // Startet die ScoreActivity
            finish() // Schließt die aktuelle MainActivity
        } catch (e: Exception) {
            e.printStackTrace() // Gibt den Fehler-Stacktrace aus
            Toast.makeText(this, getString(R.string.error_ending_game, e.message), Toast.LENGTH_LONG).show() // Zeigt eine Fehlermeldung an
        } /*e.message ist eine Eigenschaft des Exception-Objekts e,  detaillierte Fehlermeldung enthält.
        Diese Nachricht beschreibt den Grund des aufgetretenen Fehlers. */
    }

    private fun updateHint() {
        textViewAnimalHint.text = animalsToPlay[currentAnimalIndex].hints[hintIndex]
        /*/*Der Text der TextView textViewAnimalHint wird auf den aktuellen Hinweis gesetzt.*/*/
        textViewCurrentAnimal.text = animalsToPlay[currentAnimalIndex].name
        /*animalsToPlay[currentAnimalIndex] greift auf das aktuelle Animal-Objekt in der Liste zu.
        .hints[hintIndex] greift auf den spezifischen Hinweis dieses Tieres basierend auf dem aktuellen hintIndex zu*/
    }

    private fun updateHintCount() {
        val hintsLeft = animalsToPlay[currentAnimalIndex].hints.size - hintIndex // Berechnet die verbleibenden Hinweise
        if (hintsLeft > 0) {
            textViewHintsCountLeft.text = getString(R.string.hintsLeft, hintsLeft) // Setzt TextView auf die Anzahl der verbleibenden Hinweise
        } else {
            textViewHintsCountLeft.text = getString(R.string.noMoreHints) // Setzt TextView auf "Keine Hinweise mehr"
        }
    }

    private fun updateAttemptsCount() {
        val attemptsLeft = 3 - guessAttempts // Berechnet die verbleibenden Versuche
        if (attemptsLeft > 0) {
            textViewAttemptsCountLeft.text = getString(R.string.solveAttemptsLeft, attemptsLeft)
        // Setzt TextView auf die Anzahl der verbleibenden Versuche
        } else {
            textViewAttemptsCountLeft.text = getString(R.string.noMoreSolveAttempts) // Setzt TextView auf "Keine Versuche mehr"
        }
    }


    private fun disableGuessButton() {
        buttonGuess.isEnabled = false // Deaktiviert den Rate-Button
        buttonGuess.alpha = 0.5f // Verringert die Transparenz des Rate-Buttons
    }


    private fun resetGuessButton() {
        buttonGuess.isEnabled = true // Aktiviert den Rate-Button
        buttonGuess.alpha = 1.0f // Setzt die Transparenz des Rate-Buttons auf vollständig sichtbar
    }


    private fun disableHintButton() {
        buttonHint.isEnabled = false // Deaktiviert den Hinweis-Button
        textViewHintsCountLeft.text = getString(R.string.noMoreHints)
        // Setzt TextView auf "Keine Hinweise mehr"
        buttonHint.alpha = 0.5f // Verringert die Transparenz des Hinweis-Buttons
    }


    private fun resetHintButton() {
        buttonHint.isEnabled = true // Aktiviert den Hinweis-Button
        buttonHint.alpha = 1.0f // Setzt die Transparenz des Hinweis-Buttons auf vollständig sichtbar
        updateHintCount() // Aktualisiert die Anzahl der verbleibenden Hinweise
    }

}
