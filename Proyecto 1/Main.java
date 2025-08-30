// Este archivo se encarga de ejecutar el programa
import java.util.Scanner;
public class Main{
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa una secuencia de 16 numeros (del 0 al 15) separados por espacios:");
        String[] input = scanner.nextLine().split(" ");

        // Tablero inicial
        int[][] initialBoard = new int[4][4];
        int index = 0;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                initialBoard[i][j] = Integer.parseInt(input[index++]);
            }
        }

        // Instancias del tablero inicial y del solver
        Puzzle puzzle = new Puzzle(initialBoard);
        IDAStar solver = new IDAStar(puzzle.getGoalBoard());

        if(!solver.solve(puzzle))
            System.out.println("No hay solucion para el puzzle.");
        
        scanner.close();
    }  
}
