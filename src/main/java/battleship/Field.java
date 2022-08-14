package battleship;

import java.util.Scanner;

public class Field {

    private static final int N = 10;
    private static final char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};

    private final char[][] field;

    int remainingShips = 5;

    public Field() {
        this.field = new char[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.field[i][j] = '~';
            }
        }
    }

    public void showMap(boolean isMine) {
        System.out.print(' ');
        for (int i = 1; i <= N; i++) {
            System.out.printf(" %d", i);
        }
        System.out.println();
        for (int i = 0; i < CHARS.length; i++) {
            System.out.print(CHARS[i]);
            for (int j = 0; j < this.field[i].length; j++) {
                if (!isMine && this.field[i][j] == 'O') {
                    System.out.print(" ~");
                } else {
                    System.out.printf(" %c", this.field[i][j]);
                }
            }
            System.out.println();
        }
    }
    public void placeAllShips(){
        showMap(true);
        placeShip(Ship.CARRIER);
        placeShip(Ship.BATTLESHIP);
        placeShip(Ship.SUBMARINE);
        placeShip(Ship.CRUISER);
        placeShip(Ship.DESTROYER);
    }

    private void placeShip(Ship ship) {
        System.out.printf("%nEnter the coordinates of the %s (%d cells):%n%n", ship.getName(), ship.getSize());
        Scanner scanner = new Scanner(System.in);
        String first, second;
        int res;
        do {
            first = scanner.next();
            second = scanner.next();
            res = setShip(first, second, ship);
        } while (res == -1);
        showMap(true);
    }

    private int setShip(String first, String second, Ship ship) {
        try {
            int x1 = first.charAt(0) - 'A';
            int x2 = second.charAt(0) - 'A';
            int y1 = Integer.parseInt(first.substring(1)) - 1;
            int y2 = Integer.parseInt(second.substring(1)) - 1;

            validateCoordinates(x1, x2, y1, y2, ship);

            for (int i = Math.min(x1, x2); i <= Math.max(x1, x2); i++) {
                for (int j = Math.min(y1, y2); j <= Math.max(y1, y2); j++) {
                    field[i][j] = 'O';
                }
            }

        } catch (BattleShipException e) {
            System.out.println(e.getMessage());
            return -1;
        } catch (IllegalArgumentException e) {
            System.out.println("\nError! Wrong coordinates! Try again:\n");
            return -1;
        }
        return 0;
    }

    void takeShot() {
        Scanner scanner = new Scanner(System.in);
        String cell;
        int res;
        do {
            cell = scanner.next();
            res = shot(cell);
        } while (res == -1);
    }

    private int shot(String cell) {
        try {
            int x = cell.charAt(0) - 'A';
            int y = Integer.parseInt(cell.substring(1)) - 1;

            validateCoordinates(x, y);

            char point = this.field[x][y];
            switch (point) {
                case 'O' -> {
                    this.field[x][y] = 'X';
                    if (checkIsSank(x, y, false)) {
                        this.remainingShips--;
                        if (this.remainingShips == 0) {
                            System.out.println("\nYou sank the last ship. You won. Congratulations!\n");
                        } else {
                            System.out.println("\nYou sank a ship!\n");
                        }
                    } else {
                        System.out.println("\nYou hit a ship!\n");
                    }
                }
                case 'X' -> System.out.println("\nYou hit a ship!\n");
                case '~', 'M' -> {
                    this.field[x][y] = 'M';
                    System.out.println("\nYou missed!\n");
                }
                default -> throw new BattleShipException("\nError! Incorrect cell value! Try again:\n");
            }
        } catch (BattleShipException e) {
            System.out.println(e.getMessage());
            return -1;
        } catch (IllegalArgumentException e) {
            System.out.println("\nError! You entered the wrong coordinates! Try again:\n");
            return -1;
        }
        return 0;
    }

    private boolean checkIsSank(int x, int y, boolean tryNum) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (inRange(i) && inRange(j) && field[i][j] == 'O') {
                    return false;
                }
                if (inRange(i) && inRange(j) && field[i][j] == 'X' && !(x == i && y == j)) {
                    if (i + j <  x + y && !tryNum){
                        return checkIsSank(i, j, false);
                    }
                    if (i + j > x + y) {
                        return checkIsSank(i, j, true);
                    }
                }
            }
        }

        return true;
    }

    private void validateCoordinates(int x1, int x2, int y1, int y2, Ship ship) throws BattleShipException {
        int startX, endX, startY, endY;
        int size = ship.getSize();

        startX = Math.min(x1, x2);
        startY = Math.min(y1, y2);
        endX = Math.max(x1, x2);
        endY = Math.max(y1, y2);

        if (!(inRange(x1) && inRange(x2) && inRange(y1) && inRange(y2))) {
            throw new BattleShipException("\nError! Wrong coordinates! Try again:\n");
        }

        if (!(x1 == x2 || y1 == y2)) {
            throw new BattleShipException("\nError! Wrong ship location! Try again:\n");
        }

        if (!(endX - startX + 1 == size || endY - startY + 1 == size)) {
            throw new BattleShipException(String.format("\nError! Wrong length of the %s! Try again:\n", ship));
        }

        for (int i = startX - 1; i <= endX + 1; i++) {
            for (int j = startY - 1; j <= endY + 1; j++) {
                if (inRange(i) && inRange(j) && field[i][j] == 'O') {
                    throw new BattleShipException("\nError! You placed it too close to another one. Try again:\n");
                }
            }
        }
    }

    private void validateCoordinates(int x, int y) throws BattleShipException {
        if (!(inRange(x) && inRange(y) )) {
            throw new BattleShipException("\nError! You entered the wrong coordinates! Try again:\n");
        }
    }

    static boolean inRange(int i) {
        return i < N && i >= 0;
    }

}