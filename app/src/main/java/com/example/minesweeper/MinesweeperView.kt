package com.example.minesweeper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.MainThread
import com.google.android.material.snackbar.Snackbar

class MinesweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paintGrid: Paint = Paint()
    private var paintHidden: Paint = Paint()
    private var paintRevealed: Paint = Paint()
    private var paintDanger1: Paint = Paint()
    private var paintDanger2: Paint = Paint()
    private var paintDanger3: Paint = Paint()
    private var paintBomb: Paint = Paint()
    private var paintFlag: Paint = Paint()

    init {
        paintGrid.color = Color.parseColor("#f34d4d")
        paintGrid.style = Paint.Style.STROKE
        paintGrid.strokeWidth = 5f

        paintHidden.color = Color.parseColor("#b5b5b5")
        paintHidden.style = Paint.Style.FILL

        paintRevealed.color = Color.parseColor("#161616")
        paintRevealed.style = Paint.Style.FILL

        paintDanger1.color = Color.parseColor("#1378f9")
        paintDanger1.style = Paint.Style.FILL_AND_STROKE
        paintDanger1.strokeWidth = 5f
        paintDanger1.textSize = 80f

        paintDanger2.color = Color.parseColor("#ae61f1")
        paintDanger2.style = Paint.Style.FILL_AND_STROKE
        paintDanger2.strokeWidth = 5f
        paintDanger2.textSize = 80f

        paintDanger3.color = Color.parseColor("#f34d4d")
        paintDanger3.style = Paint.Style.FILL_AND_STROKE
        paintDanger3.strokeWidth = 5f
        paintDanger3.textSize = 80f

        paintBomb.textSize = 80f

        paintFlag.textSize = 80f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintHidden)
        drawGameArea(canvas)
        drawStates(canvas)
    }

    private fun drawGameArea(canvas: Canvas) {
        // Border
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintGrid)

        // Four horizontal lines
        canvas.drawLine(
            0f, (height / 5).toFloat(), width.toFloat(), (height / 5).toFloat(),
            paintGrid
        )
        canvas.drawLine(
            0f, (2 * height / 5).toFloat(), width.toFloat(),
            (2 * height / 5).toFloat(), paintGrid
        )
        canvas.drawLine(
            0f, (3 * height / 5).toFloat(), width.toFloat(),
            (3 * height / 5).toFloat(), paintGrid
        )
        canvas.drawLine(
            0f, (4 * height / 5).toFloat(), width.toFloat(),
            (4 * height / 5).toFloat(), paintGrid
        )

        // Four vertical lines
        canvas.drawLine(
            (width / 5).toFloat(), 0f, (width / 5).toFloat(), height.toFloat(),
            paintGrid
        )
        canvas.drawLine(
            (2 * width / 5).toFloat(), 0f, (2 * width / 5).toFloat(), height.toFloat(),
            paintGrid
        )
        canvas.drawLine(
            (3 * width / 5).toFloat(), 0f, (3 * width / 5).toFloat(), height.toFloat(),
            paintGrid
        )
        canvas.drawLine(
            (4 * width / 5).toFloat(), 0f, (4 * width / 5).toFloat(), height.toFloat(),
            paintGrid
        )

        (context as MainActivity).binding.numFlags.text = MinesweeperModel.numFlags.toString() + " \uD83D\uDEA9"
    }

    private fun drawStates(canvas: Canvas) {
        val cellWidth = width.toFloat() / 5
        val cellHeight = height.toFloat() / 5

        for (i in 0..4) {
            for (j in 0..4) {
                if (MinesweeperModel.getState(i, j) == MinesweeperModel.REVEALED) {
                    canvas.drawRect(
                        i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        paintRevealed
                    )

                    if (MinesweeperModel.getHasMine(i, j)) {
                        canvas.drawText(
                            "\uD83D\uDCA3",
                            (i * cellWidth + (0.23 * cellWidth)).toFloat(),
                            (j * cellHeight + (0.65 * cellHeight)).toFloat(),
                            paintBomb
                        )
                    }

                    when (val danger = MinesweeperModel.getDanger(i, j)) {
                        1 -> {
                            canvas.drawText(
                                danger.toString(),
                                (i * cellWidth + (0.35 * cellWidth)).toFloat(),
                                (j * cellHeight + (0.7 * cellHeight)).toFloat(),
                                paintDanger1
                            )
                        }
                        2 -> {
                            canvas.drawText(
                                danger.toString(),
                                (i * cellWidth + (0.35 * cellWidth)).toFloat(),
                                (j * cellHeight + (0.7 * cellHeight)).toFloat(),
                                paintDanger2
                            )
                        }
                        3 -> {
                            canvas.drawText(
                                danger.toString(),
                                (i * cellWidth + (0.35 * cellWidth)).toFloat(),
                                (j * cellHeight + (0.7 * cellHeight)).toFloat(),
                                paintDanger3
                            )
                        }
                    }
                } else {
                    if (MinesweeperModel.getState(i, j) == MinesweeperModel.FLAGGED) {
                        canvas.drawText(
                            "\uD83D\uDEA9",
                            (i * cellWidth + (0.23 * cellWidth)).toFloat(),
                            (j * cellHeight + (0.65 * cellHeight)).toFloat(),
                            paintFlag
                        )
                    }

                    if (MinesweeperModel.getState(i, j) == MinesweeperModel.HIDDEN &&
                        MinesweeperModel.getGameMode() == MinesweeperModel.FLAG
                    ) {
                        canvas.drawText(
                            "",
                            (i * cellWidth + (0.23 * cellWidth)).toFloat(),
                            (j * cellHeight + (0.65 * cellHeight)).toFloat(),
                            paintDanger1
                        )
                    }
                }
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val x = event.x.toInt() / (width / 5)
            val y = event.y.toInt() / (height / 5)

            if (x < 5 && y < 5) {
                if (MinesweeperModel.mode == MinesweeperModel.REVEAL) {
                    if (MinesweeperModel.getState(x, y) == MinesweeperModel.HIDDEN) {
                        if (!MinesweeperModel.getHasMine(x, y)) {
                            // Reveal the cell if there is no mine in it
                            MinesweeperModel.setCellState(x, y, MinesweeperModel.REVEALED)
                        } else {
                            // Bomb clicked, player has lost the game
                            MinesweeperModel.setCellState(x, y, MinesweeperModel.REVEALED)
                            endGame()
                            Snackbar.make(
                                (context as MainActivity).binding.root,
                                "You lose :( Click RESET to play again!",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    if (MinesweeperModel.getState(x, y) == MinesweeperModel.HIDDEN &&
                        MinesweeperModel.getFlagsLeft() > 0
                    ) {
                        // Player wants to flag a cell

                        MinesweeperModel.setCellState(x, y, MinesweeperModel.FLAGGED)
                        MinesweeperModel.numFlags -= 1
                        (context as MainActivity).binding.numFlags.text =
                            MinesweeperModel.numFlags.toString() + " \uD83D\uDEA9"
                    } else if (MinesweeperModel.getState(x, y) == MinesweeperModel.FLAGGED &&
                        MinesweeperModel.getFlagsLeft() < 3
                    ) {
                        // Player wants to unflag a cell

                        MinesweeperModel.setCellState(x, y, MinesweeperModel.HIDDEN)
                        MinesweeperModel.numFlags += 1
                        (context as MainActivity).binding.numFlags.text =
                            MinesweeperModel.numFlags.toString() + " \uD83D\uDEA9"
                    }
                }
            }

            if (MinesweeperModel.checkWin()) {
                Snackbar.make(
                    (context as MainActivity).binding.root,
                    "You won! Click RESET to play again.",
                    Snackbar.LENGTH_LONG
                ).show()
                endGame()
            }

            invalidate()

        }

        return true
    }

    fun changeFlagMode() {
        if (MinesweeperModel.getGameMode() == MinesweeperModel.REVEAL) {
            MinesweeperModel.setGameMode(MinesweeperModel.FLAG)
        } else {
            MinesweeperModel.setGameMode(MinesweeperModel.REVEAL)
        }
    }

    public fun resetGame() {
        MinesweeperModel.resetModel()
        setOnTouchListener(null)
        MinesweeperModel.numFlags = 3
        invalidate()
    }

    private fun endGame() {
        // Make player unable to play more unless they restart
        setOnTouchListener { view, motionEvent -> true }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = View.MeasureSpec.getSize(widthMeasureSpec)
        val h = View.MeasureSpec.getSize(heightMeasureSpec)
        val d = if (w == 0) h else if (h == 0) w else if (w < h) w else h
        setMeasuredDimension(d, d)
    }
}