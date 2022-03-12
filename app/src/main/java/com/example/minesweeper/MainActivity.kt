package com.example.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.minesweeper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MinesweeperModel.randomizeBoard()

        binding.btnReset.setOnClickListener {
            binding.msView.resetGame()
        }

        binding.toggleFlag.setOnCheckedChangeListener {_, isChecked ->
            binding.msView.changeFlagMode()
        }
    }
}