import javax.swing.SwingUtilities;

public class Main{
    public static void main(String[] args){
        if (args.length < 2) {
            System.out.println("Error: paso de argumentos invalido");
            System.out.println("Uso: java Main <color> <tiempo>");
            System.out.println("<color>: negro o blanco (color de las fichas de la IA)");
            System.out.println("<tiempo>: tiempo limite en segundos para el calculo de jugadas de la IA");
            return;
        }

        // Color de las fichas de la IA y tiempo limite
        String aiColor = args[0];
        final long timeLimitMillis; 

        try{
            timeLimitMillis = Long.parseLong(args[1]) * 1000; // Se convierte de segundos a milisegundos
        } catch(NumberFormatException e){
            System.out.println("Error: <tiempo> debe ser un numero entero.");
            return;
        }

        SwingUtilities.invokeLater(() -> new Connect6Game(aiColor, timeLimitMillis)); // Iniciamos el programa
    }
}
