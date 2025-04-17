# Minesweeper Game

**Minesweeper Game** is a console-based implementation of the classic Minesweeper game in Java. The game involves uncovering cells in a grid without triggering bombs. The player has to avoid bombs while uncovering all safe cells to win.

## Features
- **Three difficulty levels**: Easy, Medium, and Hard.
- **Randomly placed bombs and hints.**
- **Option to flag cells.**
- **Color-coded numbers** indicating how many bombs are adjacent to a cell.
- **Winning and losing messages** with different colors.
- **User-friendly commands** to interact with the game.

## Difficulty Levels
- **Easy**: 9x9 grid, 10 bombs, 3 hints.
- **Medium**: 16x16 grid, 40 bombs, 6 hints.
- **Hard**: 24x24 grid, 90 bombs, 10 hints.

## Gameplay
1. **Start the game** by selecting a difficulty level.
2. **Open cells** by typing the row and column of the cell you want to reveal.
3. **Flag a cell** to mark it as a bomb location.
4. If you reveal a bomb, **you lose the game**.
5. If you uncover all the safe cells, **you win**.
6. The game provides **random hints** for the player to help uncover safe cells.
7. The game supports **restarting at any time**.

## Commands
- **S/s**: Start a new game.
- **I/i**: View game information.
- **C/c**: Change settings (symbols and options).
- **Q/q**: Quit the game.
- **R/r**: Restart the game.
- **F/f**: Flag a cell.
- **B/b**: Use a hint to reveal a cell.

## How to Run
1. **Clone the repository**:
   ```bash
   git clone https://github.com/AmirhosseinBarzegari/MinesweeperGame.git
