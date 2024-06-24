package de.fra_uas.fb2.mobiledevices.animalguessinggame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RoundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_round)


        val button5Animals = findViewById<Button>(R.id.button5Animals)
        val button10Animals = findViewById<Button>(R.id.button10Animals)
        val button20Animals = findViewById<Button>(R.id.button20Animals)
        val button30Animals = findViewById<Button>(R.id.button30Animals)
        val button40Animals = findViewById<Button>(R.id.button40Animals)
        val button50Animals = findViewById<Button>(R.id.button50Animals)

        button5Animals.setOnClickListener { startMainActivity(5) }
        button10Animals.setOnClickListener { startMainActivity(10) }
        button20Animals.setOnClickListener { startMainActivity(20) }
        button30Animals.setOnClickListener { startMainActivity(30) }
        button40Animals.setOnClickListener { startMainActivity(40) }
        button50Animals.setOnClickListener { startMainActivity(50) }
    }

    private fun startMainActivity(numAnimals: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NUM_ANIMALS", numAnimals)
        startActivity(intent)
        finish()
    }
}