import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Properties PROPS = new Properties();
    private static final String LEADERBOARD_PATH = "leaderboard.properties";
    private static String CURRENT_PLAYER;
    private static String ALT_PLAYER;

    public static void main(String[] args) {

        readLeaderboard();
        showLeaderboard();

        int playedGames = 0;
        String stone1 = "X";
        String stone2 = "O";
        String whiteList = "123456789";
        boolean player1Turn = true;
        boolean[][] winCells = new boolean[3][3];

        System.out.println("\n\n\nWelcome to 'my_three_in_a_row' by eg_");
        System.out.println("The player who get the first three stones in a row wins the game...");
        String player1 = getName(stone1, "Enter your name Player 1 (empty = X):", "");
        String player2 = getName(stone2, "Enter your name Player 2 (empty = O):", player1);

        boolean revenge = true;

        while (revenge) {

            String[][] board = {
                    {"1", "2", "3"},
                    {"4", "5", "6"},
                    {"7", "8", "9"}
            };
            int moveCount = 1;
            boolean gameOver = false;
            boolean draw = false;

            printBoard(board);

            while (!gameOver) {

                String currentStone;
                boolean validMove = false;

                if ((moveCount % 2 != 0) == player1Turn) {
                    CURRENT_PLAYER = player1;
                    ALT_PLAYER = player2;
                    currentStone = stone1;
                } else {
                    CURRENT_PLAYER = player2;
                    ALT_PLAYER = player1;
                    currentStone = stone2;
                }

                System.out.println(CURRENT_PLAYER + " it is your turn! \nChoose your field \nInput numbers from 1 to 9: \n");

                while (!validMove) {

                    String playerInput = scan();

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
                    System.out.println(CURRENT_PLAYER + " won!");
                    playedGames++;
                    gameOver = true;
                }

                if (moveCount == 10 && !gameOver) {
                    System.out.println("It's a draw!");
                    playedGames++;
                    draw = true;
                    gameOver = true;
                }
            }

            System.out.println("Saving leaderboard...");
            saveLeaderboard(draw);

            System.out.println("\nrevenge? (y/n)");
            boolean validAnswer = false;
            while (!validAnswer) {
                String playerAnswer = scan();
                if (playerAnswer.equalsIgnoreCase("y")) {
                    System.out.println("REVENGE!!! \nPreparing for new game...");
                    validAnswer = true;
                    player1Turn = false;
                } else if (playerAnswer.equalsIgnoreCase("n")) {
                    System.out.println("EXIT");
                    validAnswer = true;
                    revenge = false;
                    showLeaderboard();
                } else {
                    System.out.println("Invalid input!");
                    System.out.println("Try again!");
                }
            }

        }
    }

    private static void printBoard(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            System.out.print(FontColor.BG_WHITE + FontColor.BLACK_BOLD + "|");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(" " + board[i][j] + " ");
                System.out.print("|");
            }
            System.out.println(FontColor.RESET);
        }
    }

    private static String scan() {
        return SCANNER.nextLine().trim();
    }

    private static String getName(String stone, String text, String player1) {
        while (true) {
            System.out.print(text);
            String playerInput = scan();
            if (playerInput.isEmpty()) {
                return stone;
            }
            if (isValidName(playerInput) && !player1.equals(playerInput)) {
                return playerInput;
            }
            System.out.println("Invalid name or name already exists!");
            System.out.println("Only letters and digits, starting with a letter.");
            System.out.println("Try again!");
        }
    }

    private static boolean isValidName(String name) {
        return name.matches("^[a-zA-Z][a-zA-Z0-9]*$");
    }

    private static void readLeaderboard() {
        try (Reader reader = Files.newBufferedReader(Path.of(LEADERBOARD_PATH))) {
            PROPS.load(reader);
        } catch (NoSuchFileException _) {

        } catch (IOException e) {
            System.out.println("Error reading leaderboard!");
        }
    }

    private static void saveLeaderboard(boolean draw) {
        String now = LocalDateTime.now().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String date = dtf.format(LocalDateTime.now());

        int winsCurrentPlayer = Integer.parseInt(PROPS.getProperty(CURRENT_PLAYER + ".wins", "0"));
        int gamesCurrentPlayer = Integer.parseInt(PROPS.getProperty(CURRENT_PLAYER + ".games", "0"));
        int gamesAltPlayer = Integer.parseInt(PROPS.getProperty(ALT_PLAYER + ".games", "0"));

        if (!draw) {
            winsCurrentPlayer++;
            PROPS.setProperty(CURRENT_PLAYER + ".wins", String.valueOf(winsCurrentPlayer));
        }

        gamesCurrentPlayer++;
        gamesAltPlayer++;

        PROPS.setProperty(CURRENT_PLAYER + ".games", String.valueOf(gamesCurrentPlayer));
        PROPS.setProperty(CURRENT_PLAYER + ".last_played", date);
        PROPS.setProperty(ALT_PLAYER + ".games", String.valueOf(gamesAltPlayer));
        PROPS.setProperty(ALT_PLAYER + ".last_played", date);

        try (Writer writer = Files.newBufferedWriter(Path.of(LEADERBOARD_PATH))) {
            PROPS.store(writer, "Leaderboard");
        } catch (IOException e) {
            System.out.println("Error writing leaderboard!");
        }
    }

    private static void showLeaderboard() {
        try (Reader reader = Files.newBufferedReader(Path.of(LEADERBOARD_PATH))) {
            PROPS.load(reader);

            System.out.println("\nLeaderboard:\n");

            for (String key : PROPS.stringPropertyNames()) {
                String value = PROPS.getProperty(key);
                key = key.substring(0, key.indexOf('.'));
                System.out.println(key + ": " + value);
            }

        } catch (NoSuchFileException _) {
        } catch (IOException e) {
            System.out.println("Error reading leaderboard!");
        }
    }
}