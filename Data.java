import java.util.Random;
import java.util.Scanner;

public class Data {
    // Scanner for taking user input from the console.
    Scanner input = new Scanner(System.in);

    // ANSI escape codes for setting text colors in the console output.
    public static final String RESET = "\u001B[0m";
    public static final String RED_DARK = "\u001B[38;5;88m";
    public static final String LIGHT_RED = "\u001B[91m";
    public static final String PINK = "\u001B[38;5;13m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // ANSI escape codes for setting background colors in the console output.
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_PURPLE = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_WHITE = "\u001B[47m";


    // ANSI escape code for applying text effects, such as bold formatting
    public static final String BOLD = "\u001B[1m";


    // userViewBoard: The board shown to the player, with revealed and closed cells
    // boardData: The actual game board, with bombs and empty spaces
    private char[][] userViewBoard;
    private char[][] boardData;


    // closedSymbol: Represents a closed (unrevealed) cell
    // emptySymbol: Represents an empty cell with no adjacent bombs
    // bombSymbol: Represents a cell containing a bomb
    // flagSymbol: Represents a marked cell (flagged by the player)
    private char closedSymbol = '-';
    private char emptySymbol = '.';
    private char bombSymbol = '*';
    private char flagSymbol = 'F';

    // Sets the symbol for closed cells.
    public void setClosedSymbol(char closedSymbol)
    {
        // Checks if the symbol is valid (not in the list of invalid symbols)
        if (!isValidSymbol(closedSymbol)) {
            System.out.println(RED + "Invalid symbol! This symbol is not allowed." + RESET);
        } else {
            this.closedSymbol = closedSymbol;
        }
    }


    // Sets the symbol for empty cells.
    // User can select space.
    public void setEmptySymbol(char emptySymbol)
    {
        if ((!isValidSymbol(emptySymbol) && emptySymbol != ' ')) {
            System.out.println(RED + "Invalid symbol! This symbol is not allowed." + RESET);
        } else {
            this.emptySymbol = emptySymbol;
        }
    }


    // Sets the symbol for bomb cells
    public void setBombSymbol(char bombSymbol)
    {
        if (!isValidSymbol(bombSymbol)) {
            System.out.println(RED + "Invalid symbol! This symbol is not allowed." + RESET);
        } else {
            this.bombSymbol = bombSymbol;
        }
    }


    // Checks if the given symbol is valid for custom settings.
    public boolean isValidSymbol(char symbol)
    {
        // Symbols that are already used in the game and can't be reassigned.
        char[] invalidSymbols = {
                '1', '2', '3', '4', '5', '6', '7', '8', '9', // Numbered hints
                flagSymbol,                                  // Player flag
                closedSymbol,                                // Unopened cell
                emptySymbol,                                 // Empty cell with 0 bombs nearby
                bombSymbol,                                  // Bomb cell
                ' '                                          // Space for visibility
        };

        // If the symbol matches any of the invalid ones, it's not allowed.
        for (char invalid : invalidSymbols) {
            if (symbol == invalid) return false;
        }

        // If we reach here, the symbol is valid.
        return true;
    }


    // Main menu of the Minesweeper game.
    // Allows the player to start, change settings, view instructions, or quit.
    public void menu()
    {
        while (true) {
            // Display the main menu with color formatting
            System.out.println(BG_BLACK + CYAN + BOLD + " Welcome to minesweeper!" + RESET);
            System.out.println("Enter 'S' to start game.");
            System.out.println("Enter 'C' to change settings.");
            System.out.println("Enter 'I' to see gameInstructions.");
            System.out.println("Enter 'Q' to exit game.");

            // Get user input and normalize character.
            char choice = input.next().charAt(0);

            switch (choice) {
                case 'S':
                case 's':
                    input.nextLine(); // Clear the buffer.
                    startGame(); // Start the game with current settings.
                    break;

                case 'C':
                case 'c':
                    input.nextLine(); // Clear the buffer.
                    changeSettings(); // Let player change difficulty or symbols.
                    startGame(); // Let player change difficulty or symbols
                    break;

                case 'I':
                case 'i':
                    showGameInstructions(); // Show instructions.
                    System.out.println();
                    continue; // Go back to the menu.

                case 'Q':
                case 'q':
                    return; // Exit the menu loop and terminate the method.

                default:
                    System.out.println(RED + "Invalid choice!" + RESET);
                    System.out.println();
                    continue; // Show menu again
            }
        }
    }


    // Initializes and starts the Minesweeper game.
    public void startGame()
    {
        chooseLevel();
        setUserViewBoard();
        setBoardData();
        printUserViewBoard();
        displayGame();
    }


    // If player lost or won and wanted to restart game, this boolean will be true.
    public boolean shouldRestart = false;

    public void restartGame()
    {
        setUserViewBoard();   // Reset the user's view of the board.
        setBoardData();       // Generate a new game board with bombs and numbers.
        printUserViewBoard(); // Print the reset board

        if (shouldRestart)
        {
            shouldRestart = false; // Reset the flag
            displayGame();         // Start the game loop again
        }
    }


    // This is for counting reveal boosts in next methode.
    public int revealBoosts;
    // By this integer, we can reset the number of reveal boosts if player restart the game.
    public int revealBoostsTemp;
    // This method uses a reveal boost to randomly reveal a non-bomb, closed cell.
    // After revealing the cell, if the cell is empty, it recursively uncovers neighboring cells.
    public void showRevealBoostMessage()
    {
        Random rand = new Random();

        while (true)
        {
            // Randomly generate row and column indices.
            int row = rand.nextInt(size);
            int col = rand.nextInt(size);

            // Checks if the cell is not a bomb and did is closed.
            if (boardData[row][col] != bombSymbol && userViewBoard[row][col] == closedSymbol)
            {
                revealBoosts--; // Decrease the number of available reveal boosts.

                // If the selected cell is empty, uncover its neighboring cells recursively.
                if (boardData[row][col] == emptySymbol) {
                    uncoverNeighbors(row, col);
                }
                else
                {
                    userViewBoard[row][col] = boardData[row][col];
                }

                // Inform the player that a cell has been revealed.
                System.out.println("[Hint Used!] Cell at (" + (row + 1) + ',' + (col + 1) + ") revealed!");
                System.out.println();

                if (isPlayerWin())
                {
                    printUserViewBoard();
                    showWinMessage();
                    break;
                }
                break;
            }
        }
    }


    // Player can read game instructions by enter 'I' in menu.
    public void showGameInstructions()
    {
        // Print the main game goal and instructions for the player.
        System.out.println("Goal: Reveal all safe cells without hitting a bomb!");
        System.out.println();
        System.out.println("Numbers show how many bombs are around that cell.");
        System.out.println("Bombs end the game immediately if you click on them.");
        System.out.println("You can flag suspicious cells using the flag feature.");
        System.out.println("Reveal Boosts: Use them to reveal a random safe cell.");
        System.out.println();
        System.out.println("Controls:");
        System.out.println("   - To open a cell: Type the row and column number.");
        System.out.println("   - To use a Reveal Boost, press the dedicated button.");
        System.out.println("   - To place/remove a flag, use the flag command.");
        System.out.println();
        System.out.println("Tip: Empty cells will automatically uncover neighbors!");
        System.out.println();
        System.out.println("Good luck, brave miner!");
        System.out.println();

        // Prompt for 'Q' to go back to the main menu
        while (true)
        {
            System.out.println("Enter 'Q' to back to the main menu.");
            char choice = input.next().charAt(0);
            // Break the loop if 'Q' is pressed, returning the user to the main menu.
            if (choice == 'Q' || choice == 'q') break;
                // Print an error message if the user enters an invalid choice.
            else System.out.println(RED + "Invalid choice! This choice is not allowed." + RESET);
        }
    }


    // This method checks if the selected cell is a bomb.
    // If it is a bomb, the game is over, and it returns true.
    public boolean isGameOver(int row, int col)
    {
        // If the cell contains a bomb, return true, meaning the game is over.
        if (boardData[row][col] == bombSymbol) return true;
        else return false;
    }


    // This method handles game over scenario and gives the player options to restart or quit.
    public void gameOverDisplay()
    {
        // Display the final state of the board.
        printBoardData();
        System.out.println();
        System.out.println(BG_BLACK + RED + " You lost! Game Over." + RESET);


        // Ask if the player wants to restart or quit the game.
        while (true)
        {
            System.out.println("Do you want to restart?");
            System.out.println("Enter 1 for Yes, 2 for No.");

            String inputStr = input.nextLine().trim(); // Get the input and trim extra spaces.

            // Check for valid input (1 or 2).
            if (inputStr.equalsIgnoreCase("1"))
            {
                // If player chooses to restart, reset variables and restart the game.
                revealBoosts = revealBoostsTemp;
                shouldRestart = true;
                restartGame();
                break;
            }
            else if (inputStr.equalsIgnoreCase("2"))
            {
                // If player chooses to quit, go back to the main menu.
                menu();
                break;
            }
            else
            {
                // Handle invalid input.
                System.out.println(RED + "Invalid input! Please try again." + RESET);
                System.out.println();
            }
        }
    }


    // Returns true if the cell has already been revealed by the player.
    public boolean isAlreadyChosen(int row, int col)
    {
        if (userViewBoard[row][col] != closedSymbol) return true;
        else return false;
    }


    // If "isAlreadyChosen" methode returns true, this methode will be shown.
    public void showAlreadyChosenMessage()
    {
        String[] messages =
        {
                "Oops! You already opened this cell.",
                "Hey, that's an old move. Try another one!",
                "This cell is taken. Go elsewhere, miner!"
        };
        System.out.println(RED + messages[new Random().nextInt(messages.length)] + RESET);
        System.out.println();
        printUserViewBoard();
    }


    // Checks if the player has revealed all safe cells. (win condition).
    public boolean isPlayerWin()
    {
        int counter = 0;
        for (int i = 0; i < Data.this.userViewBoard.length; i++)
        {
            for (int j = 0; j < Data.this.userViewBoard[i].length; j++)
            {
                // Count how many cells are still closed (not revealed).
                if (userViewBoard[i][j] != closedSymbol && userViewBoard[i][j] != flagSymbol)
                    counter++;
            }
        }
        int allCells = size * size;
        if (counter == (allCells) - numBombs) return true;
        else return false;
    }


    // This method is called when isPlayerWin() returns true.
    public void showWinMessage()
    {
        String[] messages =
        {
                "You did it, legend!",
                "Boom! Victory is yours!",
                "Flawless win. Respect!"
        };
        System.out.println(GREEN + messages[new Random().nextInt(messages.length)] + RESET);

        while (true)
        {
            System.out.println("Enter Q for Quit, R for Restart.");
            char choice = input.next().charAt(0);
            // Restarting the game.
            if (choice == 'R' || choice == 'r')
            {
                revealBoosts = revealBoostsTemp;
                shouldRestart = true;
                restartGame();
                break;
            }
            // Back to menu.
            else if (choice == 'Q' || choice == 'q')
            {
                menu();
                break;
            }
            else
            {
                System.out.println(RED + "Oops! That wasn't a valid choice." + RESET);
            }
        }
    }


    // If in "displayGame" methode player enter 'F', this methode will be shown.
    public void showFlagMessage()
    {
        while (true) {
            boolean alreadyChosen = false;

            System.out.println("Enter the row number followed by the column number to mark or unmark.");
            System.out.println("Enter Q to back to the game.");

            String inputStr = input.nextLine().trim(); // Get the input and trim extra spaces.

            // Player wants to withdraw their request.
            if (inputStr.equalsIgnoreCase("Q"))
            {
                printUserViewBoard();
                break;
            }

            // Check if input is valid.
            String[] inputParts = inputStr.split(" ");

            // Check if the input has two parts (row and column).
            if (inputParts.length == 2)
            {
                try
                {
                    int row = Integer.parseInt(inputParts[0]) - 1; // Convert row to number and adjust index.
                    int col = Integer.parseInt(inputParts[1]) - 1; // Convert column to number and adjust index.


                    // Check if row and column are within valid range.
                    if (row >= 0 && row < size && col >= 0 && col < size)
                    {
                        // Checks if the selected cell is close or not.
                        alreadyChosen = isAlreadyChosen(row, col);

                        // The cell is already opened.
                        if (alreadyChosen && userViewBoard[row][col] != flagSymbol)
                        {
                            showAlreadyChosenMessage();
                            continue;
                        }

                        // Cell is marked already.
                        if (userViewBoard[row][col] == flagSymbol)
                        {
                            userViewBoard[row][col] = closedSymbol;
                        }

                        // Flag the cell.
                        else
                        {
                            userViewBoard[row][col] = flagSymbol;
                        }

                        printUserViewBoard(); // Display the updated board
                        break;
                    }

                    // Row and column are not within valid range.
                    else
                    {
                        System.out.println(RED + "Invalid row or column. Please try again." + RESET);
                        System.out.println();
                        printUserViewBoard();
                        continue;
                    }
                }

                // If the input is not a number, this error will be display.
                catch (NumberFormatException e)
                {
                    System.out.println(RED + "Invalid input. Please enter numbers for row and column." + RESET);
                    System.out.println();
                    printUserViewBoard();
                    continue;
                }
            }
            // Player has entered more than two numbers.
            else
            {
                System.out.println(RED + "Invalid input! Please enter row and column." + RESET);
                System.out.println();
                printUserViewBoard();
                continue;
            }
        }
    }


    public void uncoverNeighbors(int row, int col)
    {
        // If the cell is a bomb, stop right here.
        if (boardData[row][col] == bombSymbol) return;

        // If the cell has already been revealed, skip it.
        if (userViewBoard[row][col] == emptySymbol) return;

        // Reveal the current cell.
        userViewBoard[row][col] = boardData[row][col];

        // If the cell has a number (not empty), no need to continue.
        if (boardData[row][col] != emptySymbol) return;

        // Now we know the cell is empty, so we reveal all its neighbors recursively.
        // Up and down
        if (row - 1 >= 0) uncoverNeighbors(row - 1, col);         // Up
        if (row + 1 < boardData.length) uncoverNeighbors(row + 1, col); // Down

        // Left and right
        if (col - 1 >= 0) uncoverNeighbors(row, col - 1);         // Left
        if (col + 1 < boardData[0].length) uncoverNeighbors(row, col + 1); // Right

        // Diagonals
        if (row - 1 >= 0 && col - 1 >= 0) uncoverNeighbors(row - 1, col - 1); // Top-left
        if (row - 1 >= 0 && col + 1 < boardData[0].length) uncoverNeighbors(row - 1, col + 1); // Top-right
        if (row + 1 < boardData.length && col - 1 >= 0) uncoverNeighbors(row + 1, col - 1); // Bottom-left
        if (row + 1 < boardData.length && col + 1 < boardData[0].length) uncoverNeighbors(row + 1, col + 1); // Bottom-right
    }


    // Loop to keep the game running until quit or game ends.
    public void displayGame()
    {
        boolean gameOver = false;
        boolean alreadyChosen = false;
        boolean playerWin = false;

        String inputStr = "";
        while (true)
        {
            System.out.println("Enter the row number followed by the column number (e.g., '3 5').");
            System.out.println("Use 'R' to restart or 'Q' to quit the game.");
            System.out.println("Enter 'F' to mark or unmark cell.");
            System.out.println("Enter 'B' to use reveal Boost.");
            System.out.println("(⚡ Reveal Boosts left: " + revealBoosts + ')');

            while (true)
            {
                inputStr = input.nextLine().trim(); // Get the input and trim extra spaces.
                if (!inputStr.isEmpty())  // Input validation: skip if empty input.
                {
                    break;
                }
            }

            if (inputStr.length() == 1) {
                // Handle single-character commands like R, Q, F, B.
                switch (inputStr.toUpperCase()) {
                    case "R":
                        revealBoosts = revealBoostsTemp;
                        restartGame();
                        continue;

                    case "Q":
                        menu();
                        break;

                    case "F":
                        printUserViewBoard();
                        showFlagMessage();
                        continue;

                    case "B":
                        if (revealBoosts > 0) {
                            showRevealBoostMessage();
                            printUserViewBoard();
                        } else {
                            System.out.println(RED + "You can not use reveal Boost!" + RESET);
                            printUserViewBoard();
                        }
                        continue;

                    default:
                        System.out.println(RED + "Invalid input. Please try again." + RESET);
                        break;
                }
            }

            // Check if the input contains two numbers. (row and column)
            String[] inputParts = inputStr.split(" ");

            // Check if the input has two parts (row and column)
            if (inputParts.length == 2)
            {
                try
                {
                    int row = Integer.parseInt(inputParts[0]) - 1; // Convert row to number and adjust index.
                    int col = Integer.parseInt(inputParts[1]) - 1; // Convert column to number and adjust index.

                    // Check if row and column are within valid range
                    if (row >= 0 && row < size && col >= 0 && col < size)
                    {
                        gameOver = isGameOver(row, col);
                        alreadyChosen = isAlreadyChosen(row, col);

                        if (userViewBoard[row][col] == flagSymbol)
                        {
                            System.out.println(RED + "You have to unmark this cell first!" + RESET);
                            System.out.println();
                            printUserViewBoard();
                            continue;
                        }

                        if (gameOver) break;
                        if (alreadyChosen)
                        {
                            showAlreadyChosenMessage();
                            continue;
                        }

                        uncoverNeighbors(row , col);
                        printUserViewBoard(); // Display the updated board

                        playerWin = isPlayerWin(); // Check player won or not.
                        if (playerWin) break;

                    }
                    else
                    {
                        System.out.println(RED + "Invalid row or column. Please try again." + RESET);
                        System.out.println();
                        printUserViewBoard();
                    }
                }
                catch (NumberFormatException e)
                {
                    // If the input is not a number, this error is displayed
                    System.out.println(RED + "Invalid input. Please enter numbers for row and column." + RESET);
                    System.out.println();
                    printUserViewBoard();
                }
            }
            else
            {
                System.out.println(RED + "Invalid format. Please enter the row and column numbers separated by a space." + RESET);
                System.out.println();
                printUserViewBoard();
            }
        }

        if (gameOver) gameOverDisplay();
        if (playerWin) showWinMessage();
    }


    //Allows the player to customize the symbols used in the game.
    public void changeSettings()
    {
        // Prompt the user for a new closed cell symbol. (e.g., '-')
        System.out.println("Enter the closed cell symbol(default: - ): ");
        String closedInput = input.nextLine();

        // If the input is not empty, update the closedSymbol to the first character entered.
        if (!closedInput.isEmpty())
        {
            setClosedSymbol(closedInput.charAt(0));
        }

        // Prompt the user for a new empty cell symbol. (e.g., '.')
        System.out.println("Enter the empty cell symbol(default: . ): ");
        String emptyInput = input.nextLine();

        // If the input is not empty, update the emptySymbol to the first character entered.
        if (!emptyInput.isEmpty())
        {
            setEmptySymbol(emptyInput.charAt(0));
        }

        // Prompt the user for a new bomb cell symbol. (e.g., '*')
        System.out.println("Enter the bomb cell symbol(default: * ): ");
        String bombInput = input.nextLine();

        // If the input is not empty, update the bombSymbol to the first character entered.
        if (!bombInput.isEmpty())
        {
            setBombSymbol(bombInput.charAt(0));
        }

        // Display the new settings to the user.
        System.out.println("Symbols updated! Closed: " + RED + closedSymbol + RESET +
                                             " Empty: " + RED + emptySymbol + RESET +
                                             " bomb: " + RED + bombSymbol + RESET);
        System.out.println();
    }


    // The size of the game board (number of rows/columns) and number of bombs.
    private int size = 0;
    private int numBombs = 0;

    // Prompts the user to select a difficulty level for the game.
    public void chooseLevel ()
    {
        int level = 0;
        while (true)
        {
            // Show difficulty options to the user.
            System.out.println("Choose a difficulty level:");
            System.out.println("1. Easy (9x9 board, 10 bombs)");
            System.out.println("2. Medium (16x16 board, 40 bombs)");
            System.out.println("3. Hard (24x24 board, 90 bombs)");

            String inputStr = input.nextLine().trim(); // Get the input and trim extra spaces.

            // Check if user entered something.
            if (!inputStr.isEmpty())
            {
                switch (inputStr)
                {
                    case "1":
                        level = 1;
                        break;

                    case "2":
                        level = 2;
                        break;

                    case "3":
                        level = 3;
                        break;

                    default:
                        // If the input is invalid, show an error and ask again.
                        System.out.println(RED + "Invalid level! Try again." + RESET);
                        System.out.println();
                        continue;
                }
            }

            // If a valid level is selected, break the loop.
            if (level != 0) break;
        }

        // Set the board size, number of bombs, and reveal boosts based on difficulty level.
        switch(level)
        {
            case 1:
                size = 9; numBombs = 10; revealBoosts = 3; break;

            case 2:
                size = 16; numBombs = 40; revealBoosts = 6; break;

            case 3:
                size = 24; numBombs = 90; revealBoosts = 10; break;
        }

        // Save the initial number of boosts for future use. (like restarting the game)
        revealBoostsTemp = revealBoosts;

        // Show the selected level to the player.
        System.out.println("Level selected: " + (level == 1 ? "Easy" : level == 2 ? "Medium" : "Hard"));
    }


    // Initializes the user view board with all cells set to the closed symbol.
    public void setUserViewBoard()
    {
        // Create a new 2D array to represent the board the user will interact with.
        userViewBoard = new char[size][size];

        // Fill every cell of the board with the 'closed' symbol (e.g., '-').
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                userViewBoard[i][j] = closedSymbol;
            }
        }
    }


    // This part of the code is responsible for printing the column numbers with proper alignment.
    public void printUserViewBoard()
    {
        System.out.print("  "); // Extra space for aligning rows.

        for (int j = 1; j <= size; j++)
        {
            System.out.printf("%s%2d%s", CYAN, j, RESET); // Print the column numbers in CYAN.
            if (j >= 10) System.out.print(" "); // Add extra space if column number is greater than 9.
        }
        System.out.println();

        // This section prints the rows and the content of each cell.
        for (int i = 0; i < size; i++)
        {
            System.out.printf("%s%2d%s ", CYAN, i + 1, RESET); // Print the row number in CYAN.

            // Check and print the content of each cell, with color formatting.
            for (int j = 0; j < size; j++)
            {
                // If the cell is closed, print the closed symbol.
                if (userViewBoard[i][j] == closedSymbol)
                {
                    System.out.print(userViewBoard[i][j]);
                }
                else if (userViewBoard[i][j] == emptySymbol)
                {
                    // Print the empty cell in black.
                    System.out.print(BLACK + userViewBoard[i][j] + RESET);
                }
                else if (userViewBoard[i][j] == flagSymbol)
                {
                    // Print the flagged cell in yellow.
                    System.out.print(YELLOW + userViewBoard[i][j] + RESET);
                }
                else
                {
                    // Print numbers in different colors based on their value.
                    switch (userViewBoard[i][j])
                    {
                        case '1':
                            System.out.print(LIGHT_RED+ userViewBoard[i][j] + RESET);
                            break;

                        case '2':
                            System.out.print(RED + userViewBoard[i][j] + RESET);
                            break;

                        case '3':
                            System.out.print(RED_DARK + userViewBoard[i][j] + RESET);
                            break;

                        default:
                            // Print all other values in purple.
                            System.out.print(PURPLE + userViewBoard[i][j] + RESET);
                    }
                }

                // Manage spacing for a neat display.
                if (j < 10) System.out.print(" ");  // Add a single space for one-digit numbers.
                else System.out.print("  "); // Add two spaces for two-digit numbers.
            }
            System.out.println();
        }
    }


    // Initializes the board with bombs and adjacent bomb counts.
    public void setBoardData()
    {
        // Create the board with the given size.
        boardData = new char[size][size];

        // Place bombs randomly on the board.
        placeBombs();

        // Iterate over the entire board to calculate bomb counts for each cell.
        for(int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                // If the cell is not a bomb, calculate the number of surrounding bombs.
                if (boardData[i][j] != bombSymbol)
                {
                    int num = countBomb(i,j);
                    if (num != 0)
                    {
                        // If there are surrounding bombs, store the count in the cell.
                        boardData[i][j] = Character.forDigit(num, 10);
                    }
                    else
                    {
                        // If no bombs around, mark the cell as empty.
                        boardData[i][j] = emptySymbol;
                    }
                }
            }
        }
    }


    // Triggered when the player selects a bomb or the game ends.
    public void printBoardData ()
    {
        System.out.print("  "); // Extra space for aligning rows.
        for (int j = 1; j <= size; j++)
        {
            System.out.printf("%s%2d%s", CYAN, j, RESET); // Column counting.
            if (j >= 10) System.out.print(" ");
        }
        System.out.println();

        for (int i = 0; i < size; i++)
        {
            System.out.printf("%s%2d%s ", CYAN, i + 1, RESET); // Row counting.

            for (int j = 0; j < size; j++)
            {
                if (boardData[i][j] == bombSymbol)
                {
                    System.out.print(RED + bombSymbol + RESET);
                    if (j < 10) System.out.print(" ");
                    else System.out.print("  ");
                }
                else
                {
                    System.out.print(emptySymbol);
                    if (j < 10) System.out.print(" ");
                    else System.out.print("  ");
                }
            }
            System.out.println();
        }
    }



     // Calculates the number of bombs adjacent to a given cell.
     // Checks all 8 neighboring positions (up, down, left, right, and diagonals).
    public int countBomb(int row, int col)
    {
        int bombCount = 0;

        // Count bombs in all 8 adjacent cells if within bounds

        if (col - 1 >= 0 && boardData[row][col - 1] == bombSymbol) bombCount++; // Check left
        if (col + 1 < size && boardData[row][col + 1] == bombSymbol) bombCount++; // Check right
        if (row - 1 >= 0 && boardData[row - 1][col] == bombSymbol) bombCount++; // Check top
        if (row + 1 < size && boardData[row + 1][col] == bombSymbol) bombCount++; // Check bottom
        if (row + 1 < size && col - 1 >= 0 && boardData[row + 1][col - 1] == bombSymbol) bombCount++; // Check bottom-left
        if (row + 1 < size && col + 1 < size && boardData[row + 1][col + 1] == bombSymbol) bombCount++; // Check bottom-right
        if (row - 1 >= 0 && col - 1 >= 0 && boardData[row - 1][col - 1] == bombSymbol) bombCount++; // Check top-left
        if (row - 1 >= 0 && col + 1 < size && boardData[row - 1][col + 1] == bombSymbol) bombCount++; // Check top-right

        return bombCount;
    }


    // Randomly places bombs on the game board without duplication.
    public void placeBombs()
    {
        Random rand = new Random();
        int bombsPlaced = 0;

        // Loop until all bombs are placed.
        while (bombsPlaced < numBombs)
        {
            // Generate random row and column
            int row = rand.nextInt(size);
            int col = rand.nextInt(size);

            // Check if the cell already has a bomb
            if (boardData[row][col] != bombSymbol)
            {
                boardData[row][col] = bombSymbol;
                bombsPlaced++;
            }
        }
    }


}