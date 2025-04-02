// Este archivo tiene las funciones heuristicas usadas para resolver el problema
// Calcula las heuristicas manhattanDistance, linearConflict, cornerTile, y HH
public class Heuristica{
    // Funcion que calcula la heuristica distancia Manhattan
    public static int manhattanDistance(int[][] board, int[][] goal){
        int distance = 0;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                int value = board[i][j]; // Casilla actual
                if(value != 0){ // Si la casilla no es el espacio en blanco
                    // Se calcula la distancia entre la casilla actual y la casilla objetivo
                    int goalRow = (value - 1) / 4;
                    int goalCol = (value - 1) % 4;
                    distance += Math.abs(i - goalRow) + Math.abs(j - goalCol);
                }
            }
        }
        return distance;
    }

    // Funcion que calcula la heuristica Linear Conflict 
    public static int linearConflict(int[][] position){
        int conflict = 0;
        int size = position.length;
        
        // Conflictos en las filas
        for(int row = 0; row < size; row++){
            int maxVal = -1;
            for(int col = 0; col < size; col++){
                int value = position[row][col];
                if(value != 0 && (value - 1) / size == row){
                    if(value > maxVal)
                        maxVal = value;
                    else
                        conflict += 2;
                }
            }
        }
            
        // Conflictos en las columnas
        for(int col = 0; col < size; col++){
            int maxVal = -1;
            for(int row = 0; row < size; row++){
                int value = position[row][col];
                if(value != 0 && (value - 1) % size == col){
                    if(value > maxVal)
                        maxVal = value;
                    else 
                        conflict += 2;
                }
            }
        }
        
        return conflict;
    }
        
    // Funcion que calcula la heuristica Corner-Tile 
    public static int cornerTileHeuristic(int[][] board, int[][] goal){
        int heuristic = 0;
        // Posiciones de las esquinas del tablero
        int[][] cornerPositions ={
            {0, 0}, {0, 3}, {3, 0}, {3, 3}
        };
        // Para cada esquina del tablero
        for(int[] corner : cornerPositions){
            // Posicion de la esquina actual
            int currentRow = corner[0];
            int currentCol = corner[1];
            // Si la pieza en la posicion de la esquina está fuera de lugar respecto al tablero objetivo
            if (board[currentRow][currentCol] != goal[currentRow][currentCol])
                heuristic += 4; 
        }
        return heuristic;
    }
        
    // Funcion que retorna la formula Hybride Heuristic (HH) (modficada, se cambio wd por ct, y se multiplico md por 4) 
    public static int hybridHeuristic(int[][] board, int[][] goal){
        return 4*manhattanDistance(board, goal)/3 + linearConflict(board) + cornerTileHeuristic(board, goal);
    }
}
