import java.util.ArrayList;
import java.util.List;

public class Connect6Board{
    private static final int BOARD_SIZE = 19; // 19x19 intersecciones
    private char[][] board; // Tablero 
    private char currentPlayer; // Jugador actual ('B' para negro, 'W' para blanco)
    private int[][] lastMoves; // Coordenadas de las dos ultimas fichas colocadas

    // Constructor
    public Connect6Board(){
        this.board = new char[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                board[i][j] = '-'; // Inicializamos el tablero vacio
            }
        }
        this.currentPlayer = 'B'; // El primer jugador siempre usa fichas negras
    }

    // Funcion que obtiene el jugador actual
    public char getCurrentPlayer(){
        return currentPlayer;
    }

    // Funcion que obtiene el tablero actual
    public char[][] getBoard(){
        return board;
    }

    // Funcion que realiza un movimiento
    public boolean makeMove(int[] pos1, int[] pos2, char player){
        if(!isValidMove(pos1) || (pos2 != null && !isValidMove(pos2)))
            return false; // Movimiento invalido
        
        board[pos1[0]][pos1[1]] = player;
        if(pos2 != null)
            board[pos2[0]][pos2[1]] = player;
        
        lastMoves = new int[][]{pos1, pos2};
        currentPlayer =(currentPlayer == 'B') ? 'W' : 'B';
        return true;
    }
    
    // Funcion que verifica que un movimiento es valido
    public boolean isValidMove(int[] pos){
        // Validamos que el movimiento este dentro del rango y sea sobre una posicion vacia
        int row = pos[0];
        int col = pos[1];
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE && board[row][col] == '-';
    }

    // Funcion que verifica si un jugador gano
    public boolean isWinner(char player){
        // Se revisan todas las lineas para determinar si un jugador ha conectado 6 o mas fichas
        for(int row = 0; row < BOARD_SIZE; row++){
            for(int col = 0; col < BOARD_SIZE; col++){
                if(checkDirection(player, row, col, 1, 0) || // Horizontal
                    checkDirection(player, row, col, 0, 1) || // Vertical
                    checkDirection(player, row, col, 1, 1) || // Diagonal principal
                    checkDirection(player, row, col, 1, -1)){ // Diagonal secundaria
                    return true;
                }
            }
        }
        return false;
    }

    // Funcion que verifica una direccion
    private boolean checkDirection(char player, int row, int col, int rowDelta, int colDelta){
        int count = 0;

        for(int i = 0; i < 6; i++){ // Se chequean las siguientes 6 posiciones
            int r = row + i * rowDelta;
            int c = col + i * colDelta;

            if(r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE || board[r][c] != player)
                return false;
            
            count++;
        }

        return count == 6; // Gana si conecta 6 fichas consecutivas
    }

    // Funcion que obtiene los movimientos posibles
    public List<int[]> getAvailableMoves(){
        List<int[]> moves = new ArrayList<>();
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(board[i][j] == '-')
                    moves.add(new int[]{i, j});
            }
        }
        return moves;
    }

    // Funcion que obtiene los ultimos movimientos
    public int[][] getLastMoves(){
        return lastMoves; 
    }
    
    // Funcion que clona un tablero
    public Connect6Board clone(){
        Connect6Board clone = new Connect6Board();
        for(int i = 0; i < BOARD_SIZE; i++){
            clone.board[i] = this.board[i].clone();
        }
        clone.currentPlayer = this.currentPlayer;
        return clone;
    }
}
