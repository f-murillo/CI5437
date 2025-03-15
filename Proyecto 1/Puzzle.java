// Este archivo contiene la logica del 15-puzzle
// Crea el tablero, obtiene los estados, realiza los movimientos para obtener estados vecinos, y copia estados
/* Es posible adaptarlo para tener un solver para el N-Puzzle. Hay que tener en cuenta que se tendría que 
    cambiar la longitud de la secuencia a pedir al usuario en el archivo Main.java; y dependiendo del N, se 
    tendria que considerar modificar o no las heuristicas
*/
import java.util.*;
public class Puzzle{
    // Tablero, fila y columna donde esta el espacio en blanco y el predecesor del tablero (padre)
    private int[][] board;
    // Tablero meta (ordenado del 1 al 15, con el espacio blanco al final)
    private int[][] goalBoard ={
            {1,2,3,4}, 
            {5,6,7,8}, 
            {9,10,11,12}, 
            {13,14,15,0} 
    };
    private int blankRow;
    private int blankCol;
    private Puzzle parent;

    // Constructor del tablero inicial
    public Puzzle(int[][] initialBoard){
        this.board = initialBoard;
        this.parent = null;
        // Se encuentra la posicion inicial del espacio en blanco
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(board[i][j] == 0){
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
    }

    // Constructor de un tablero vecino
    public Puzzle(int[][] board, int blankRow, int blankCol, Puzzle parent){
        this.board = board;
        this.blankRow = blankRow;
        this.blankCol = blankCol;
        this.parent = parent;
    }

    // Funcion que obtiene el tablero de un estado
    public int[][] getBoard(){
        return board;
    }

    // Funcion que obtiene el tablero meta
    public int[][] getGoalBoard(){
        return goalBoard;
    }
    
    // Funcion que obtiene el padre de un estado
    public Puzzle getParent(){
        return parent;
    }

    // Funcion que verifica si un tablero es el tablero meta
    public boolean isGoal(int[][] goalBoard){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(board[i][j] != goalBoard[i][j])
                    return false;
            }
        }
        return true;
    }

    // Funcion que obtiene los estados (tableros) vecinos
    public PriorityQueue<Puzzle> getNeighbors(int[][] goalBoard){
        // Se usa una cola de prioridad (la prioridad es la heuristica del tablero)
        PriorityQueue<Puzzle> pq_neighbors = new PriorityQueue<>(Comparator.comparingInt(p -> Heuristica.hybridHeuristic(p.getBoard(), goalBoard)));
        /*
         * Posibles movimientos de la casilla blanca
         * En las filas: -1: hacia arriba; 1: hacia abajo; 0: quedarse en la misma fila (moverse horizontalmente)
         * En las columnas: -1: hacia la izquierda; 1: hacia la derecha; 0: quedarse en la misma fila (moverse verticalmente)
         */
        int[] rowMoves ={-1, 1, 0, 0};
        int[] colMoves ={0, 0, -1, 1};

        // Ciclo que calcula cada posible movimiento de la casilla blanca, crea un nuevo tablero, y lo agrega a la cola de vecinos
        for(int k = 0; k < 4; k++){
            // Se calcula las posiciones de fila y columna que puede tener el espacio en blanco
            int newRow = blankRow + rowMoves[k];
            int newCol = blankCol + colMoves[k];
            if(newRow >= 0 && newRow < 4 && newCol >= 0 && newCol < 4){ // Si la fila y columna calculadas estan en el rango de la matriz
                int[][] newBoard = copyBoard(); // Se copia el tablero actual
                // Se hace el intercambio entre la casilla a mover y el espacio en blanco
                newBoard[blankRow][blankCol] = newBoard[newRow][newCol];
                newBoard[newRow][newCol] = 0;
                // Se crea el tablero con el movimiento hecho y se agrega a la cola de vecinos
                Puzzle neighbor = new Puzzle(newBoard, newRow, newCol, this);
                pq_neighbors.add(neighbor);
            }
        }
        return pq_neighbors;
    }

    // Funcion que copia un tablero
    private int[][] copyBoard(){
        int[][] newBoard = new int[4][4];
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }
}
