package battleship;

import java.util.Scanner;

public class Game {

    private final Field[] players;

    private int currentPlayer = 1;

    public Game(Field player1, Field player2) {
        this.players = new Field[]{player1, player2};
    }

    public void start() {
        System.out.println("Player 1, place your ships on the game field\n");
        players[0].placeAllShips();
        passTheMove();
        System.out.println("Player 2, place your ships on the game field\n");
        players[1].placeAllShips();

        while (players[0].remainingShips != 0 && players[1].remainingShips != 0) {
            playerTurn();
        }
    }

    private void playerTurn() {
        passTheMove();
        currentPlayer = 1 - currentPlayer;
        players[1 - currentPlayer].showMap(false);
        System.out.println("---------------------");
        players[currentPlayer].showMap(true);
        System.out.printf("%nPlayer %d, it's your turn:%n%n", currentPlayer + 1);
        players[1 - currentPlayer].takeShot();
    }

    private void passTheMove() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter and pass the move to another player");
        System.out.println("...");
        scanner.nextLine();
    }
}
