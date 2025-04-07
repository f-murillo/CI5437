import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sudokuSolver{
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Error: numero de argumentos invalido. \nUso: java sudokuSolver <instancia>");
            System.exit(1);
        }
        String sudokuInstance = args[0].trim();
        if(sudokuInstance.length() != 81){
            System.err.println("Error: la instancia de Sudoku debe tener 81 caracteres.");
            System.exit(1);
        }
        
        // Se convierte la instancia de Sudoku a una formula CNF.
        List<List<Integer>> cnf = sudokuSAT.sudokuToCNF(sudokuInstance);
        
        // Se invoca el SAT solver (DPLL) con la CNF obtenida.
        Map<Integer, Boolean> assignment = new HashMap<>();
        // Tomamos el tiempo de ejecucion
        long startTime = System.nanoTime();
        boolean isSat = dpllLib.dpll(cnf, assignment); // Se llama al solver DPLL
        long endTime = System.nanoTime();
        double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;
        
        // Se decodifica y muestra el resultado.
        if(isSat){
            String solution = sudokuSAT.decodeSudokuSolution(assignment);
            System.out.println(solution);
        } else{
            System.out.println("No se encontro solucion para la instancia de Sudoku.");
        }
        System.out.println("Tiempo de resolucion: " + elapsedSeconds + " segundos");
    }
}
