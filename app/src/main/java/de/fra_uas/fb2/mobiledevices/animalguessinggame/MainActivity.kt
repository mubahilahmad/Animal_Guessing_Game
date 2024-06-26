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

        updateHint()
        updateHintCount()
        updateAttemptsCount()

        buttonHint.setOnClickListener {
            hintIndex++
            if (hintIndex < animalsToPlay[currentAnimalIndex].hints.size) {
                textViewAnimalHint.text = animalsToPlay[currentAnimalIndex].hints[hintIndex]
                updateHintCount()
            } else {
                disableHintButton()
                Toast.makeText(this, getString(R.string.toastNoMoreHints), Toast.LENGTH_SHORT).show()
            }
        }

        buttonGuess.setOnClickListener {
            val guess = editTextEnterYourGuess.text.toString()
            if (guess.equals(animalsToPlay[currentAnimalIndex].name, ignoreCase = true)) {
                val pointsFromHints = hintIndex
                val pointsFromAttempts = guessAttempts
                val pointsEarned = 13 - pointsFromHints - pointsFromAttempts
                score += pointsEarned
                textViewScore.text = getString(R.string.score, score)
                currentAnimalIndex++
                if (currentAnimalIndex < animalsToPlay.size) {
                    hintIndex = 0
                    guessAttempts = 0
                    updateHint()
                    editTextEnterYourGuess.text.clear()
                    resetGuessButton()
                    resetHintButton()
                    updateHintCount()
                    updateAttemptsCount()
                    Toast.makeText(this, getString(R.string.toastCorrect), Toast.LENGTH_SHORT).show()
                } else {
                    endGame()
                }
            } else {
                if (guessAttempts < 3) {
                    guessAttempts++
                    updateAttemptsCount()
                    if (guessAttempts < 3) {
                        Toast.makeText(this, getString(R.string.toastWrong), Toast.LENGTH_SHORT).show()
                    } else {
                        disableGuessButton()
                        buttonSkip.visibility = View.VISIBLE
                    }
                }
            }
        }

        buttonSkip.setOnClickListener {
            currentAnimalIndex++
            if (currentAnimalIndex < animalsToPlay.size) {
                hintIndex = 0
                guessAttempts = 0
                updateHint()
                editTextEnterYourGuess.text.clear()
                resetGuessButton()
                resetHintButton()
                updateHintCount()
                updateAttemptsCount()
                buttonSkip.visibility = View.GONE
                Toast.makeText(this, getString(R.string.toastDontGiveUp), Toast.LENGTH_SHORT).show()
            } else {
                endGame()
            }
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

    private fun endGame() {
        try {
            val intent = Intent(this, ScoreActivity::class.java)
            intent.putExtra("SCORE", score)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error_ending_game, e.message), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateHint() {
        textViewAnimalHint.text = animalsToPlay[currentAnimalIndex].hints[hintIndex] // das ist neu
        textViewCurrentAnimal.text = animalsToPlay[currentAnimalIndex].name // das ist neu
    }

    private fun updateHintCount() {
        val hintsLeft = animalsToPlay[currentAnimalIndex].hints.size - hintIndex // das ist neu
        if (hintsLeft > 0) {
            textViewHintsCountLeft.text = getString(R.string.hintsLeft, hintsLeft) // das ist neu
        } else {
            textViewHintsCountLeft.text = getString(R.string.noMoreHints) // das ist neu
        }
    }

    private fun updateAttemptsCount() {
        val attemptsLeft = 3 - guessAttempts // das ist neu
        if (attemptsLeft > 0) {
            textViewAttemptsCountLeft.text = getString(R.string.solveAttemptsLeft, attemptsLeft) // das ist neu
        } else {
            textViewAttemptsCountLeft.text = getString(R.string.noMoreSolveAttempts) // das ist neu
        }
    }

    private fun disableGuessButton() {
        buttonGuess.isEnabled = false // das ist neu
        buttonGuess.alpha = 0.5f // das ist neu
    }

    private fun resetGuessButton() {
        buttonGuess.isEnabled = true // das ist neu
        buttonGuess.alpha = 1.0f // das ist neu
    }

    private fun disableHintButton() {
        buttonHint.isEnabled = false // das ist neu
        textViewHintsCountLeft.text = getString(R.string.noMoreHints) // das ist neu
        buttonHint.alpha = 0.5f // das ist neu
    }

    private fun resetHintButton() {
        buttonHint.isEnabled = true // das ist neu
        buttonHint.alpha = 1.0f // das ist neu
        updateHintCount()
    }
}
