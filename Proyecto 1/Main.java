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

        // Tablero meta (ordenado del 1 al 15, con el espacio blanco al final)
        int[][] goalBoard ={
            {1,2,3,4}, 
            {5,6,7,8}, 
            {9,10,11,12}, 
            {13,14,15,0} 
        };

        // Instancias del tablero inicial y del solver
        Puzzle puzzle = new Puzzle(initialBoard);
        IDAStar solver = new IDAStar(goalBoard);

        if(!solver.solve(puzzle)) // Si no se logro encontrar solucion
            System.out.println("No hay solucion para el puzzle.");
        
        scanner.close();
    }  
}
