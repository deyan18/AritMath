package us.mis.calculator

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var operationAnsw = -1
    var answOptions = mutableListOf<Int>()
    var points = 0
    var rangeStart = 2
    var rangeEnd = 10
    var level = 1
    var lives = 3

    var buttons = arrayOf<Button>()
    lateinit var pointsTextView: TextView
    lateinit var levelTextView: TextView

    lateinit var countdownTimer: CountDownTimer
    lateinit var countdownTextView: TextView
    lateinit var heart1ImageView: ImageView
    lateinit var heart2ImageView: ImageView
    lateinit var heart3ImageView: ImageView
    var timeLeftInMillis: Long = 30000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons = arrayOf(
            findViewById<Button>(R.id.option1Button),
            findViewById<Button>(R.id.option2Button),
            findViewById<Button>(R.id.option3Button),
            findViewById<Button>(R.id.option4Button),
        )

        pointsTextView = findViewById<TextView>(R.id.pointsTextView)
        levelTextView = findViewById<TextView>(R.id.levelTextView)
        countdownTextView = findViewById(R.id.countdownTextView)
        heart1ImageView = findViewById<ImageView>(R.id.heart1ImageView)
        heart2ImageView = findViewById<ImageView>(R.id.heart2ImageView)
        heart3ImageView = findViewById<ImageView>(R.id.heart3ImageView)

        generateOperation()
        startCountdownTimer()
    }

    fun checkAnsw(userAnsw: String): Unit{
        if(userAnsw.equals(operationAnsw.toString())){
            updatePoints()
            generateOperation()
        }else{
            lives--
            modifyCountdown(-5000)
            countdownButtonColorUpdate(false)
        }
        updateHearts()
    }

    fun updateHearts(): Unit{
        when (lives) {
            0 -> {
                heart1ImageView.setImageResource(R.drawable.heart_border_icon)
                heart2ImageView.setImageResource(R.drawable.heart_border_icon)
                heart3ImageView.setImageResource(R.drawable.heart_border_icon)
                endGame()
            }
            1 -> {
                heart1ImageView.setImageResource(R.drawable.heart_fill_icon)
                heart2ImageView.setImageResource(R.drawable.heart_border_icon)
                heart3ImageView.setImageResource(R.drawable.heart_border_icon)
            }
            2 -> {
                heart1ImageView.setImageResource(R.drawable.heart_fill_icon)
                heart2ImageView.setImageResource(R.drawable.heart_fill_icon)
                heart3ImageView.setImageResource(R.drawable.heart_border_icon)
            }
            3 -> {
                heart1ImageView.setImageResource(R.drawable.heart_fill_icon)
                heart2ImageView.setImageResource(R.drawable.heart_fill_icon)
                heart3ImageView.setImageResource(R.drawable.heart_fill_icon)
            }
        }
    }

    fun updateButtons(): Unit{

        for ((index, button) in buttons.withIndex()) {
            button.text = answOptions[index].toString()
            button.setOnClickListener {
                checkAnsw(button.text.toString())
                updateScores()
            }
        }
    }

    fun countdownButtonColorUpdate(good: Boolean): Unit{
        if(good){
            countdownTextView.setTextColor(Color.BLUE)
        }else{
            countdownTextView.setTextColor(Color.RED)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            countdownTextView.setTextColor(Color.GRAY)
        }, 1000)
    }

    fun levelButtonColorUpdate(): Unit{
        levelTextView.setTextColor(Color.BLUE)

        Handler(Looper.getMainLooper()).postDelayed({
            levelTextView.setTextColor(Color.GRAY)
        }, 1000)
    }

    fun updateScores(): Unit{
        pointsTextView.text = "Points: $points"
        levelTextView.text = "Level: $level"
    }

    fun generateOperation(): Unit{
        val r1 = Random.nextInt(from = rangeStart, until = rangeEnd)
        val r2 = Random.nextInt(from = rangeStart, until = rangeEnd)

        operationAnsw = r1*r2
        val operationTextView = findViewById<TextView>(R.id.operationTextView)
        operationTextView.text = "$r1 x $r2"

        answOptions = mutableListOf<Int>(operationAnsw)

        generateWrongAnsw()
        updateButtons()
    }

    fun generateWrongAnsw(): Unit{
        do {
            val r1 = Random.nextInt(from = rangeStart, until = rangeEnd)
            val r2 = Random.nextInt(from = rangeStart, until = rangeEnd)
            val wrongAnsw = r1 * r2
            if (wrongAnsw !in answOptions) {
                answOptions.add(wrongAnsw)
            }
        } while (answOptions.size < 4)
        answOptions.shuffle()
    }

    fun updatePoints(): Unit{
        points += 10
        val newLevel = points/100 + 1

        if(newLevel > level){
            level = newLevel
            rangeEnd += 5
            rangeStart +=2
            lives = 3
            modifyCountdown(+10000)
            countdownButtonColorUpdate(true)
            levelButtonColorUpdate()
        }
    }


    fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdown()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateCountdown()
                endGame()
                // Handle end of game logic here
            }
        }.start()
    }

    fun updateCountdown() {
        val secondsLeft = timeLeftInMillis / 1000
        countdownTextView.text = "$secondsLeft s"
    }

    fun modifyCountdown(time: Long): Unit{
        timeLeftInMillis = timeLeftInMillis + time // Increase time by 10 seconds
        countdownTimer.cancel()
        startCountdownTimer()
    }

    fun endGame(): Unit{

        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val highscore = sharedPreference.getInt("highscore", 0)

        if(points > highscore){
            var editor = sharedPreference.edit()
            editor.putInt("highscore", points)
            editor.commit()
        }

        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()
    }


}