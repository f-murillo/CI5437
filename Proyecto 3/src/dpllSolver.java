// Este archivo tiene el metodo principal que verifica si una formula booleana es o no satisfacible
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dpllSolver{
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Error: numero de argumentos invalido. \nUso: java dpllSolver <archivo-cnf>");
            System.exit(1);
        }

        String filename = args[0]; // Archivo con la instancia CNF

        try{
            File file = new File(filename);
            List<List<Integer>> formula = dpllLib.parseDimacs(file); // Se parsea la formula
            Map<Integer, Boolean> assignment = new HashMap<>();

            // Comenzamos a tomar el tiempo
            long startTime = System.nanoTime();
            boolean isSatisfiable = dpllLib.dpll(formula, assignment); // Se llama a DPLL
            long endTime = System.nanoTime();
            // Terminamos de tomar el tiempo
            double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;
            
            // Se muestra el resultado
            if(isSatisfiable) 
                System.out.println("SATISFIABLE");
            else 
                System.out.println("UNSATISFIABLE");
            System.out.println("Tiempo de resolucion: " + elapsedSeconds + " segundos");

        } catch(IOException e){
            System.out.println("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
