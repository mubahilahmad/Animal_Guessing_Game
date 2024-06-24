package de.fra_uas.fb2.mobiledevices.animalguessinggame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var animalHint: TextView
    private lateinit var hintButton: Button
    private lateinit var hintsCount: TextView
    private lateinit var guessInput: EditText
    private lateinit var guessButton: Button
    private lateinit var attemptsCount: TextView
    private lateinit var scoreView: TextView
    private lateinit var skipButton: Button
    private lateinit var currentAnimalTextView: TextView

    private var currentAnimalIndex = 0
    private var hintIndex = 0
    private var guessAttempts = 0
    private var score = 0
    private var numAnimals = 0
    private var animalsToPlay = listOf<Animal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animalHint = findViewById(R.id.textViewAnimalHint)
        hintButton = findViewById(R.id.buttonHint)
        hintsCount = findViewById(R.id.textViewHintsCountLeft)
        guessInput = findViewById(R.id.editTextEnterYourGuess)
        guessButton = findViewById(R.id.buttonGuess)
        attemptsCount = findViewById(R.id.textViewAttemptsCountLeft)
        scoreView = findViewById(R.id.textViewScore)
        skipButton = findViewById(R.id.buttonSkip)
        currentAnimalTextView = findViewById(R.id.textViewCurrentAnimal)


        if (intent.hasExtra("NUM_ANIMALS")) {
            numAnimals = intent.getIntExtra("NUM_ANIMALS", 0)
        } else {
            Toast.makeText(this, getString(R.string.no_number_of_animals_selected), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        animalsToPlay = Animal.animals.shuffled().take(numAnimals)

        updateHint()
        updateHintCount()
        updateAttemptsCount()


        hintButton.setOnClickListener {
            hintIndex++
            if (hintIndex < animalsToPlay[currentAnimalIndex].hints.size) {
                animalHint.text = animalsToPlay[currentAnimalIndex].hints[hintIndex]
                updateHintCount()
            } else {
                disableHintButton()
                Toast.makeText(this, getString(R.string.toastNoMoreHints), Toast.LENGTH_SHORT).show()
            }
        }

        guessButton.setOnClickListener {
            val guess = guessInput.text.toString()
            if (guess.equals(animalsToPlay[currentAnimalIndex].name, ignoreCase = true)) {
                val pointsFromHints = hintIndex
                val pointsFromAttempts = guessAttempts
                val pointsEarned = 13 - pointsFromHints - pointsFromAttempts
                score += pointsEarned
                scoreView.text = getString(R.string.score, score)
                currentAnimalIndex++
                if (currentAnimalIndex < animalsToPlay.size) {
                    hintIndex = 0
                    guessAttempts = 0
                    updateHint()
                    guessInput.text.clear()
                    resetGuessButton()
                    resetHintButton()
                    updateHintCount()
                    updateAttemptsCount()
                    Toast.makeText(this, getString(R.string.toastCorrect), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.finished), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ScoreActivity::class.java)
                    intent.putExtra("SCORE", score)
                    startActivity(intent)
                    finish()
                }
            } else {
                if (guessAttempts < 3) {
                    guessAttempts++
                    updateAttemptsCount()
                    if (guessAttempts < 3) {
                        Toast.makeText(this, getString(R.string.toastWrong), Toast.LENGTH_SHORT).show()
                    } else {
                        disableGuessButton()
                        skipButton.visibility = View.VISIBLE
                    }
                }
            }
        }

        skipButton.setOnClickListener {
            currentAnimalIndex++
            if (currentAnimalIndex < animalsToPlay.size) {
                hintIndex = 0
                guessAttempts = 0
                updateHint()
                guessInput.text.clear()
                resetGuessButton()
                resetHintButton()
                updateHintCount()
                updateAttemptsCount()
                skipButton.visibility = View.GONE
                Toast.makeText(this, getString(R.string.toastDontGiveUp), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.finished), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ScoreActivity::class.java)
                intent.putExtra("SCORE", score)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun updateHint() {
        animalHint.text = animalsToPlay[currentAnimalIndex].hints[hintIndex]
        currentAnimalTextView.text = animalsToPlay[currentAnimalIndex].name
    }

    private fun updateHintCount() {
        val hintsLeft = animalsToPlay[currentAnimalIndex].hints.size - hintIndex
        if (hintsLeft > 0) {
            hintsCount.text = getString(R.string.hintsLeft, hintsLeft)
        } else {
            hintsCount.text = getString(R.string.noMoreHints)
        }
    }

    private fun updateAttemptsCount() {
        val attemptsLeft = 3 - guessAttempts
        if (attemptsLeft > 0) {
            attemptsCount.text = getString(R.string.solveAttemptsLeft, attemptsLeft)
        } else {
            attemptsCount.text = getString(R.string.noMoreSolveAttempts)
        }
    }

    private fun disableGuessButton() {
        guessButton.isEnabled = false
        guessButton.alpha = 0.5f
    }

    private fun resetGuessButton() {
        guessButton.isEnabled = true
        guessButton.alpha = 1.0f
    }

    private fun disableHintButton() {
        hintButton.isEnabled = false
        hintsCount.text = getString(R.string.noMoreHints)
        hintButton.alpha = 0.5f
    }

    private fun resetHintButton() {
        hintButton.isEnabled = true
        hintButton.alpha = 1.0f
        updateHintCount()
    }
}