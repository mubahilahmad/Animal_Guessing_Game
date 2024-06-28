package de.fra_uas.fb2.mobiledevices.animalguessinggame

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class RoundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_round)

        val button5Animals = findViewById<Button>(R.id.button5Animals)
        val button5Predators = findViewById<Button>(R.id.button5Predators) // Neu hinzugefügt
        val button10Animals = findViewById<Button>(R.id.button10Animals) // Geändert
        val button10Predators = findViewById<Button>(R.id.button10Predators) // Neu hinzugefügt
        val button20Animals = findViewById<Button>(R.id.button20Animals) // Geändert
        val button20Predators = findViewById<Button>(R.id.button20Predators) // Neu hinzugefügt

        button5Animals.setOnClickListener { startMainActivity(5, false) }
        button5Predators.setOnClickListener { startMainActivity(5, true) }
        button10Animals.setOnClickListener { startMainActivity(10, false) }
        button10Predators.setOnClickListener { startMainActivity(10, true) }
        button20Animals.setOnClickListener { startMainActivity(20, false) }
        button20Predators.setOnClickListener { startMainActivity(20, true) }
    }

    private fun startMainActivity(numAnimals: Int, isPredatorMode: Boolean) {
        try {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("NUM_ANIMALS", numAnimals)
            intent.putExtra("PREDATOR_MODE", isPredatorMode)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error_starting_main_activity, e.message), Toast.LENGTH_LONG).show()
        }
    }
}
