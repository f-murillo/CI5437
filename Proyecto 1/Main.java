// Este archivo se encarga de ejecutar el programa
// Establece el tablero meta, pide al usuario que ingrese una secuencia de numeros, crea las instancias de IDA* y del tablero, e imprime los resultados
import java.util.Scanner;
public class Main{
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa una secuencia de 16 numeros (del 0 al 15) separados por espacios:");
        String[] input = scanner.nextLine().split(" ");

        // Se crea el tablero inicial
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

        if(!solver.solve(puzzle)) // Si no se logro encontrar una solucion
            System.out.println("No hay solucion para el puzzle.");
        
        scanner.close();
    }  
}
