package de.fra_uas.fb2.mobiledevices.animalguessinggame


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class ScoreActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)


        val scoreTextView = findViewById<TextView>(R.id.textViewScore)
        val addScoreButton = findViewById<Button>(R.id.buttonAddScore)
        val startAnewButton = findViewById<Button>(R.id.buttonStartAnew)


        val score = intent.getIntExtra("SCORE", 0)

        scoreTextView.text = getString(R.string.score_text, score)



        startAnewButton.setOnClickListener {
            val intent = Intent(this, RoundActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    
}