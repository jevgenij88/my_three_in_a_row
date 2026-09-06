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

        say("\n\n\nWelcome to 'my_three_in_a_row' by eg_");
        say("The player who get the first three stones in a row wins the game...");
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

                say(CURRENT_PLAYER + " it is your turn! \nChoose your field \nInput numbers from 1 to 9: \n");

                while (!validMove) {

                    String playerInput = scan();

                    if (playerInput.length() != 1 || !whiteList.contains(playerInput)) {
                        say("Invalid input!");
                        say("Try again!");
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
                        say("There is already a stone in this field!");
                        say("Try again!");
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
                    say(CURRENT_PLAYER + " won!");
                    playedGames++;
                    gameOver = true;
                }

                if (moveCount == 10 && !gameOver) {
                    say("It's a draw!");
                    playedGames++;
                    draw = true;
                    gameOver = true;
                }
            }

            say("Saving leaderboard...");
            saveLeaderboard(draw);

            say("\nrevenge? (y/n)");
            boolean validAnswer = false;
            while (!validAnswer) {
                String playerAnswer = scan();
                if (playerAnswer.equalsIgnoreCase("y")) {
                    say("REVENGE!!! \nPreparing for new game...");
                    validAnswer = true;
                    player1Turn = false;
                } else if (playerAnswer.equalsIgnoreCase("n")) {
                    say("EXIT");
                    validAnswer = true;
                    revenge = false;
                    showLeaderboard();
                } else {
                    say("Invalid input!");
                    say("Try again!");
                }
            }

        }
    }

    private static void say(String text) {
        for (String line : text.split("\n", -1)) {
            if (line.isEmpty()) {
                System.out.println();
            } else {
                System.out.println(FontColor.BG_WHITE + FontColor.BLACK_BOLD + line + FontColor.RESET);
            }
        }
    }

    private static void ask(String text) {
        System.out.print(FontColor.BG_WHITE + FontColor.BLACK_BOLD + text + " ");
    }

    private static void printBoard(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            System.out.print(FontColor.BG_WHITE + FontColor.BLACK_BOLD + "|");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(" " + cellColor(board[i][j]) + " " + FontColor.BLACK_BOLD + "|");
            }
            System.out.println(FontColor.RESET);
        }
    }

    private static String cellColor(String cell) {
        if (cell.equals("X")) {
            return FontColor.RED_BOLD + cell;
        }
        if (cell.equals("O")) {
            return FontColor.BLUE_BOLD + cell;
        }
        return cell;
    }

    private static String scan() {
        System.out.print(FontColor.BG_WHITE + FontColor.BLACK_BOLD);
        String input = SCANNER.nextLine().trim();
        System.out.print(FontColor.RESET);
        return input;
    }

    private static String getName(String stone, String text, String player1) {
        while (true) {
            ask(text);
            String playerInput = scan();
            if (playerInput.isEmpty()) {
                return stone;
            }
            if (isValidName(playerInput) && !player1.equals(playerInput)) {
                return playerInput;
            }
            say("Invalid name or name already exists!");
            say("Only letters and digits, starting with a letter.");
            say("Try again!");
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
            say("Error reading leaderboard!");
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
            say("Error writing leaderboard!");
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
