// Este archivo tiene la logica del algoritmo IDA*
// Ademas, tiene procedimientos que imprimen un tablero con un estado, y que imprimen los resultados de la busqueda
import java.util.*;
public class IDAStar{
    // Tablero meta, n de estados generados, y conjunto de String para almacenar los vecinos ya visitados
    private int[][] goalBoard;
    private int generatedStates;
    private Set<String> visited; 

    // Constructor
    public IDAStar(int[][] goalBoard){
        this.goalBoard = goalBoard;
        this.generatedStates = 0;
        this.visited = new HashSet<>(); 
    }

    // Funcion que verifica si el problema tiene solucion
    public boolean solve(Puzzle puzzle){
        int limit = Heuristica.hybridHeuristic(puzzle.getBoard(), goalBoard); // Limite de heuristic (inicialmente la heuristica del estado inicial)
        
        while(true){
            int temp = dfs(puzzle, 0, limit); // Llamada a la funcion recursiva
            // Si el retorno de dfs es -1, se obtuvo la solucion
            if(temp == -1) 
                return true; 
            // Si es el maximo valor definido para un entero, no se encontro solucion
            if(temp == Integer.MAX_VALUE) 
                return false;
            // Se actualiza el limite
            limit = temp; 
        }
    }

    // Funcion  recursiva que usa IDA*
    private int dfs(Puzzle puzzle, int g, int limit){
        int f = g + Heuristica.hybridHeuristic(puzzle.getBoard(), goalBoard); // Calculo de f (f = g + h)
        if(f > limit) // Si el valor de f para este estado es mayor al limite, se retorna f
            return f;
        
        if(puzzle.isGoal(goalBoard)){ // Si el estado es el estado meta, se imprime la solucion y retorna -1
            printSolution(puzzle, g);
            return -1;
        }
    
        String boardString = Arrays.deepToString(puzzle.getBoard()); // Convertimos el tablero en un String 
        visited.add(boardString); // Se agrega el tablero al conjunto de visitados
        int min = Integer.MAX_VALUE; // Para compararlo con los valores de f de los estados vecinos
        PriorityQueue<Puzzle> neighbors = puzzle.getNeighbors(goalBoard); // Se obtienen los estados vecinos
    
        while(!neighbors.isEmpty()){ // Mientras la cola de vecinos no este vacia
            Puzzle neighbor = neighbors.poll(); // Se desencola el primer vecino (que por como se definio la cola, es la de menor heuristica)
            String neighborString = Arrays.deepToString(neighbor.getBoard()); // Se transforma el tablero a String
            
            if(!visited.contains(neighborString)){ // Si el estado no ha sido visitado
                generatedStates++; // Se aumenta el numero de estados generados
                int temp = dfs(neighbor, g + 1, limit); // Llamada recursiva

                if(temp == -1) // Si el  valor de retorno es -1, se encontro la solucion. Retorna -1 a solve
                    return -1;
                
                if(temp < min) // Si el valor de retorno es menor que min, se establece el nuevo min
                    min = temp;
            }
        }
        // Se remueve el estado del conjunto de visitados y se retorna min (que sera el nuevo limite)
        visited.remove(boardString); 
        return min;
    }
    
    // Metodo que imprime la solucion
    private void printSolution(Puzzle puzzle, int steps){
        List<int[][]> solutionPath = new ArrayList<>(); // Lista que contendra los estados de la solucion
        while(puzzle != null){ // Mientras el tablero actual no sea nulo
            // Se agrega a la lista el tablero actual, y se pasa al tablero padre (al predecesor)
            solutionPath.add(puzzle.getBoard()); 
            puzzle = puzzle.getParent();
        }
        System.out.println("\nSecuencia de movimientos: ");
        for(int i = solutionPath.size() - 2; i >= 0; i--){ // Se recorre la lista en orden inverso (para que este desde el inicio hasta el final)
            printBoard(solutionPath.get(i));
            System.out.println();
        }
        System.out.println("Numero de pasos hasta llegar a la meta: " + steps);
        System.out.println("Numero de estados generados: " + generatedStates);
    }

    // Metodo que imprime un tablero
    private void printBoard(int[][] board){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
