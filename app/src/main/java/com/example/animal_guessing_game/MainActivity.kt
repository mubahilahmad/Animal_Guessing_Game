package com.example.animal_guessing_game


import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var textViewAnimalHint: TextView
    private lateinit var buttonHint: Button
    private lateinit var textViewHintsCountLeft: TextView
    private lateinit var editTextEnterYourGuess: EditText
    private lateinit var buttonGuess: Button
    private lateinit var textViewAttemptsCountLeft: TextView
    private lateinit var textViewScore: TextView
    private lateinit var buttonSkip: Button
    private lateinit var textViewAppName: TextView

    private var currentAnimalIndex = 0
    private var hintIndex = 0
    private var guessAttempts = 0
    private var score = 0
    private var numAnimals = 0
    private var isPredatorMode = false
    private var animalsToPlay = listOf<AnimalInterface>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewAnimalHint = findViewById(R.id.textViewAnimalHint)
        buttonHint = findViewById(R.id.buttonHint)
        textViewHintsCountLeft = findViewById(R.id.textViewHintsCountLeft)
        editTextEnterYourGuess = findViewById(R.id.editTextEnterYourGuess)
        buttonGuess = findViewById(R.id.buttonGuess)
        textViewAttemptsCountLeft = findViewById(R.id.textViewAttemptsCountLeft)
        textViewScore = findViewById(R.id.textViewScore)
        buttonSkip = findViewById(R.id.buttonSkip)
        textViewAppName = findViewById(R.id.textViewAppName)

        val defaultText = getString(R.string.editTextEnterYourGuess)

        editTextEnterYourGuess.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (editTextEnterYourGuess.text.toString() == defaultText) {
                    editTextEnterYourGuess.setText(getString(R.string.EmptyString))
                }
            } else {
                if (editTextEnterYourGuess.text.toString().isEmpty()) {
                    editTextEnterYourGuess.setText(defaultText)
                }
            }
        }

        try {
            if (intent.hasExtra("NUM_ANIMALS")) {
                numAnimals = intent.getIntExtra("NUM_ANIMALS", 0)
                isPredatorMode = intent.getBooleanExtra("PREDATOR_MODE", false)
                updateAppName()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.no_number_of_animals_selected),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                getString(R.string.error_loading_num_animals, e.message),
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        animalsToPlay = if (isPredatorMode) {
            Predator.predators.shuffled().take(numAnimals).map { predator ->
                predator.copy(hints = predator.hints.shuffled())
            }
        } else {
            Animal.animals.shuffled().take(numAnimals).map { animal ->
                animal.copy(hints = animal.hints.shuffled())
            }
        }

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
                Toast.makeText(this, getString(R.string.toastNoMoreHints), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        buttonGuess.setOnClickListener {
            val guess = editTextEnterYourGuess.text.toString()
            editTextEnterYourGuess.setText(getString(R.string.EmptyString))
            if (guess.equals(animalsToPlay[currentAnimalIndex].name, ignoreCase = true)) {
                val pointsFromHints = hintIndex
                val pointsFromAttempts = guessAttempts
                val pointsEarned = 13 - pointsFromHints - pointsFromAttempts
                score += pointsEarned
                textViewScore.text = getString(R.string.score, score)
                currentAnimalIndex++
                updateAppName()
                editTextEnterYourGuess.setText(defaultText)
                if (currentAnimalIndex < animalsToPlay.size) {
                    hintIndex = 0
                    guessAttempts = 0
                    updateHint()
                    editTextEnterYourGuess.setText(getString(R.string.EmptyString))
                    resetGuessButton()
                    resetHintButton()
                    updateHintCount()
                    updateAttemptsCount()
                    Toast.makeText(this, getString(R.string.toastCorrect), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    endGame()
                }
            } else {
                if (guessAttempts < 3) {
                    guessAttempts++
                    updateAttemptsCount()
                    if (guessAttempts < 3) {
                        Toast.makeText(this, getString(R.string.toastWrong), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        disableGuessButton()
                        buttonSkip.visibility = View.VISIBLE
                    }
                }
                editTextEnterYourGuess.setText(getString(R.string.EmptyString))
            }
        }

        buttonSkip.setOnClickListener {
            currentAnimalIndex++
            updateAppName()
            if (currentAnimalIndex < animalsToPlay.size) {
                hintIndex = 0
                guessAttempts = 0
                updateHint()
                editTextEnterYourGuess.setText(getString(R.string.EmptyString))
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

    private fun updateAppName() {
        val remainingAnimals = numAnimals - currentAnimalIndex
        val animalText = if (remainingAnimals == 1) {
            if (isPredatorMode) "Predator" else "Animal"
        } else {
            if (isPredatorMode) "Predators" else "Animals"
        }
        textViewAppName.text = "$remainingAnimals $animalText"
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
            Toast.makeText(
                this,
                getString(R.string.error_ending_game, e.message),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateHint() {
        textViewAnimalHint.text = animalsToPlay[currentAnimalIndex].hints[hintIndex]
    }

    private fun updateHintCount() {
        val hintsLeft = animalsToPlay[currentAnimalIndex].hints.size - hintIndex
        if (hintsLeft > 0) {
            textViewHintsCountLeft.text = getString(R.string.hintsLeft, hintsLeft)
        } else {
            textViewHintsCountLeft.text = getString(R.string.noMoreHints)
        }
    }

    private fun updateAttemptsCount() {
        val attemptsLeft = 3 - guessAttempts
        if (attemptsLeft > 0) {
            textViewAttemptsCountLeft.text = getString(R.string.solveAttemptsLeft, attemptsLeft)
        } else {
            textViewAttemptsCountLeft.text = getString(R.string.noMoreSolveAttempts)
        }
    }

    private fun disableGuessButton() {
        buttonGuess.isEnabled = false
        buttonGuess.alpha = 0.5f
    }

    private fun resetGuessButton() {
        buttonGuess.isEnabled = true
        buttonGuess.alpha = 1.0f
    }

    private fun disableHintButton() {
        buttonHint.isEnabled = false
        textViewHintsCountLeft.text = getString(R.string.noMoreHints)
        buttonHint.alpha = 0.5f
    }

    private fun resetHintButton() {
        buttonHint.isEnabled = true
        buttonHint.alpha = 1.0f
        updateHintCount()
    }
}
