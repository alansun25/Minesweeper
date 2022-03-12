package com.example.minesweeper

import kotlin.random.Random

object MinesweeperModel {
    // Cell data class hold all data for each cell.
    // hasMine: true if cell contains a mine.
    // danger: number of bombs surrounding the cell in any direction.
    // state: whether the cell is hidden, revealed, or flagged.
    data class Cell(var hasMine: Boolean, var danger: Int, var state: Short)

    // Cell states
    const val HIDDEN: Short = 1
    const val REVEALED: Short = 2
    const val FLAGGED: Short = 3

    // Player mode
    const val REVEAL: Short = 4
    const val FLAG: Short = 5

    var mode = REVEAL

    var numFlags = 3

    // Initialize game board
    val board = arrayOf(
        arrayOf(
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN)
        ),
        arrayOf(
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN)
        ),
        arrayOf(
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN)
        ),
        arrayOf(
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN)
        ),
        arrayOf(
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN),
            Cell(false, 0, HIDDEN)
        )
    )

    var bombLocations = arrayOf(
        intArrayOf(0 ,0), intArrayOf(0 ,0), intArrayOf(0 ,0)
    )

    public fun randomizeBoard() {
        // Place bombs in 3 random cells
        var bombNum = 0

        val randX = List(3) { Random.nextInt(0, 4) }
        val randY = List(3) { Random.nextInt(0, 4) }
        for (i in 0..2) {
            val x = randX[i]
            val y = randY[i]

            board[x][y].hasMine = true

            bombLocations[bombNum][0] = x
            bombLocations[bombNum][1] = y
            bombNum += 1
        }

        setDanger()
    }

    private fun setDanger() {
        for (x in 0..4) {
            for (y in 0..4) {
                if (!board[x][y].hasMine) {
                    if (x > 0 && board[x - 1][y].hasMine) {
                        board[x][y].danger += 1
                    }
                    if (y > 0 && board[x][y - 1].hasMine) {
                        board[x][y].danger += 1
                    }
                    if (x < 4 && board[x + 1][y].hasMine) {
                        board[x][y].danger += 1
                    }
                    if (y < 4 && board[x][y + 1].hasMine) {
                        board[x][y].danger += 1
                    }

                    // diagonal checks
                    if (x > 0 && y > 0 && board[x - 1][y - 1].hasMine) {
                        board[x][y].danger += 1
                    }
                    if (x > 0 && y < 4 && board[x - 1][y + 1].hasMine) {
                        board[x][y].danger += 1
                    }
                    if (x < 4 && y > 0 && board[x + 1][y - 1].hasMine) {
                        board[x][y].danger += 1
                    }
                    if (x < 4 && y < 4 && board[x + 1][y + 1].hasMine) {
                        board[x][y].danger += 1
                    }
                }
            }
        }
    }

    fun resetModel() {
        for (i in 0..4) {
            for (j in 0..4) {
                board[i][j] = Cell(false, 0, HIDDEN)
            }
        }

        randomizeBoard()
    }

    fun getHasMine(x: Int, y: Int) = board[x][y].hasMine

    fun getDanger(x: Int, y: Int) = board[x][y].danger

    fun getState(x: Int, y: Int) = board[x][y].state

    fun getGameMode() = mode

    fun setGameMode(m: Short) {
        mode = m
    }

    fun getFlagsLeft() = numFlags

    fun setCellState(x: Int, y: Int, state: Short) {
        board[x][y].state = state
    }

    fun checkWin(): Boolean {
        for (x in 0..4) {
            for (y in 0..4) {
                // Player has not won yet if they have not revealed a
                // non-bomb cell
                if (getState(x, y) == HIDDEN && !getHasMine(x, y)) {
                    return false
                }

                // Player has not won yet if they have not flagged
                // all the bombs
                if (getHasMine(x, y) && getState(x, y) != FLAGGED) {
                    return false
                }
            }
        }

        return true
    }
}