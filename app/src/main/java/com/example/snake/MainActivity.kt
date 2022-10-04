package com.example.snake


import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.snake.SnakeCore.gameSpeed
import com.example.snake.SnakeCore.isPlay
import com.example.snake.SnakeCore.startTheGame
import com.example.snake.databinding.ActivityMainBinding

const val HEAD_SIZE = 50
const val CELL_ON_FIELD = 10


class MainActivity : AppCompatActivity() {

    private val allTale = mutableListOf<PartOfTale>()
    private val human by lazy {
        ImageView(this)
    }
    private val head by lazy {
        ImageView(this)
    }
    private var currentDirection = Directions.BOTTOM
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        head.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
        head.setImageResource(R.drawable.pacman)

        startTheGame()
        generateNewHuman()
        SnakeCore.nextMove = { move(Directions.BOTTOM) }

        binding.ivArrowUp.setOnClickListener {
            SnakeCore.nextMove = {
                checkCurrentDiraection(Directions.UP, Directions.BOTTOM)
            }
        }
        binding.ivArrowBottom.setOnClickListener {
            SnakeCore.nextMove = { checkCurrentDiraection(Directions.BOTTOM, Directions.UP) }
        }
        binding.ivArrowLeft.setOnClickListener {
            SnakeCore.nextMove = { checkCurrentDiraection(Directions.LEFT, Directions.RIGHT) }
        }
        binding.ivArrowRight.setOnClickListener {
            SnakeCore.nextMove = { checkCurrentDiraection(Directions.RIGHT, Directions.LEFT) }
        }
        binding.ivPause.setOnClickListener {
            if (isPlay) {
                binding.ivPause.setImageResource(R.drawable.ic_play)
            } else {
                binding.ivPause.setImageResource(R.drawable.ic_pause)
            }
            SnakeCore.isPlay = !isPlay
        }
    }

    private fun generateNewHuman() {
        human.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
        human.setImageResource(R.drawable.mouse)
        (human.layoutParams as FrameLayout.LayoutParams).topMargin =
            (0 until CELL_ON_FIELD).random() * HEAD_SIZE
        (human.layoutParams as FrameLayout.LayoutParams).leftMargin =
            (0 until CELL_ON_FIELD).random() * HEAD_SIZE
        binding.container.removeView(human)
        binding.container.addView(human)
    }

    private fun moveHead(direcrion: Directions, ang: Float, cordinate: Int) {
        head.rotation = ang
        when (direcrion) {
            Directions.UP, Directions.BOTTOM -> {
                (head.layoutParams as FrameLayout.LayoutParams).topMargin += cordinate
            }
            Directions.LEFT, Directions.RIGHT -> {
                (head.layoutParams as FrameLayout.LayoutParams).leftMargin += cordinate
            }
        }
        currentDirection = direcrion
    }

    private fun checkIfSnakeEatsPerson() {
        if (head.left == human.left && head.top == human.top) {
            generateNewHuman()
            addPartOfTale(head.top, head.left)
            includeSpeedGame()
        }
    }

    private fun addPartOfTale(top: Int, left: Int) {
        val talePart = drawPartOfTale(top, left)
        allTale.add(PartOfTale(top, left, talePart))
    }

    private fun drawPartOfTale(top: Int, left: Int): ImageView {
        val taleImage = ImageView(this)
        taleImage.setImageResource(R.drawable.scale)
        taleImage.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        taleImage.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
        (taleImage.layoutParams as FrameLayout.LayoutParams).topMargin = top
        (taleImage.layoutParams as FrameLayout.LayoutParams).leftMargin = left

        binding.container.addView(taleImage)
        return taleImage
    }

    private fun includeSpeedGame() {
        if (gameSpeed <= MINIMUM_GAME_SPEED) {
            return
        }
        if (allTale.size % 5 == 0) {
            gameSpeed -= 50
        }
    }

    fun move(directions: Directions) {
        when (directions) {
            Directions.UP -> moveHead(Directions.UP, 90f, -HEAD_SIZE)
            Directions.BOTTOM -> moveHead(Directions.BOTTOM, 270f, HEAD_SIZE)
            Directions.LEFT -> moveHead(Directions.LEFT, 0f, -HEAD_SIZE)
            Directions.RIGHT -> moveHead(Directions.RIGHT, 180f, HEAD_SIZE)
        }
        runOnUiThread {
            showScore()
            makeTaleMove()
            checkIfSnakeEatsPerson()
            binding.container.removeView(head)
            binding.container.addView(head)
        }

    }

    private fun checkCurrentDiraection(proper: Directions, opposite: Directions) {
        if (currentDirection == opposite) {
            move(currentDirection)
        } else {
            move(proper)
        }
    }

    private fun showScore() {
        binding.textScore.text = allTale.size.toString()
    }

    private fun makeTaleMove() {
        var tempTalePart: PartOfTale? = null
        for (index in 0 until allTale.size) {
            val talePart = allTale[index]
            binding.container.removeView(talePart.imageView)
            if (index == 0) {
                tempTalePart = talePart
                allTale[index] =
                    PartOfTale(head.top, head.left, drawPartOfTale(head.top, head.left))
            } else {
                val anotherTempPartOfTale = allTale[index]
                tempTalePart?.let {
                    allTale[index] = PartOfTale(it.top, it.left, drawPartOfTale(it.top, it.left))
                }
                tempTalePart = anotherTempPartOfTale
            }
        }
    }
}

enum class Directions {
    UP,
    RIGHT,
    BOTTOM,
    LEFT
}




