import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean gameOver = false;
        int playedGames = 0;
        String stone1 = "X";
        String stone2 = "O";
        String player1 = "";
        String player2 = "";
        String whiteList = "123456789";
        int moveCount = 1;

        Scanner input = new Scanner(System.in);

        String[][] board = {
                {"1", "2", "3"},
                {"4", "5", "6"},
                {"7", "8", "9"}
        };

        printBoard(board);

        String playerInput;

        if (player1.isEmpty() && player2.isEmpty()) {
            System.out.print("Enter your name Player 1 (empty = X): ");
            playerInput = input.nextLine();
            if (playerInput.isEmpty()) {
                player1 = "X";
            } else {
                player1 = playerInput;
            }
            System.out.print("Enter your name Player 2 (empty = O): ");
            playerInput = input.nextLine();
            if (playerInput.isEmpty()) {
                player2 = "O";
            } else {
                player2 = playerInput;
            }
        }


        while (!gameOver) {

            String currentPlayer = "";
            String currentStone = "";
            boolean validMove = false;

            if (moveCount % 2 != 0) {
                currentPlayer = player1;
                currentStone = stone1;
            } else {
                currentPlayer = player2;
                currentStone = stone2;
            }

            System.out.println();
            System.out.println(currentPlayer + " it is your turn!");
            System.out.println();
            System.out.println("Choose your field");
            System.out.println("Input numbers from 1 to 9: ");
            System.out.println();


            while (!validMove) {

                playerInput = input.nextLine().trim();

                if (playerInput.length() != 1 || !whiteList.contains(playerInput)) {
                    System.out.println("Invalid input!");
                    System.out.println("Try again!");
                    System.out.println();
                    continue;
                }

                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        if (board[i][j].equals(playerInput)) {
                            board[i][j] = currentStone;
                            moveCount++;
                            validMove = true;
                            break;
                        }
                    }
                }

                if (!validMove) {
                    System.out.println("There is already a stone in this field!");
                    System.out.println("Try again!");
                    System.out.println();
                }


            }


            printBoard(board);


            if (
                    (board[0][0].equals(currentStone) && board[0][1].equals(currentStone) && board[0][2].equals(currentStone))
                    || (board[1][0].equals(currentStone) && board[1][1].equals(currentStone) && board[1][2].equals(currentStone))
                    || (board[2][0].equals(currentStone) && board[2][1].equals(currentStone) && board[2][2].equals(currentStone))
                    || (board[0][0].equals(currentStone) && board[1][1].equals(currentStone) && board[2][2].equals(currentStone))
                    || (board[0][2].equals(currentStone) && board[1][1].equals(currentStone) && board[2][0].equals(currentStone))
                    || (board[0][0].equals(currentStone) && board[1][0].equals(currentStone) && board[2][0].equals(currentStone))
                    || (board[0][1].equals(currentStone) && board[1][1].equals(currentStone) && board[2][1].equals(currentStone))
                    || (board[0][2].equals(currentStone) && board[1][2].equals(currentStone) && board[2][2].equals(currentStone))
            ) {
                System.out.println(currentPlayer + " won!");
                playedGames++;
                gameOver = true;
            }


            if (moveCount == 10 && !gameOver) {
                System.out.println("It's a draw!");
                playedGames++;
                gameOver = true;
            }


        }


    }

    private static void printBoard(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j]);
                System.out.print(" | ");
            }
            System.out.println();
        }
    }
}
