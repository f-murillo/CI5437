import javax.swing.SwingUtilities;

public class Connect6Game{
    private Connect6Board gameBoard; // Estado lógico del tablero
    private Connect6MCTS ai; // Algoritmo MCTS
    private Connect6GUI gui; // Interfaz gráfica
    private int humanMoveCount = 0; 
    private int[][] humanMoves = new int[2][2]; // Fichas colocadas por el usuario
    private char aiPlayer; 
    private char humanPlayer; 
    private boolean isFirstMove = true;

    // Constructor
    public Connect6Game(String aiColor, long timeLimitMillis){
        // Se asigna las fichas de la IA y del humano segun el color seleccionado
        if (aiColor.equalsIgnoreCase("negras")){
            aiPlayer = 'B';
            humanPlayer = 'W';
        } else if (aiColor.equalsIgnoreCase("blancas")){
            aiPlayer = 'W';
            humanPlayer = 'B';
        } else{
            throw new IllegalArgumentException("Color inválido. Usa 'negras' o 'blancas'.");
        }

        // Se inicializa el tablero y la interfaz grafica
        gameBoard = new Connect6Board();
        ai = new Connect6MCTS(timeLimitMillis);
        gui = new Connect6GUI(this, humanPlayer, aiPlayer);
    

        // Si la IA juega con negro, realiza su primer movimiento
        if (aiPlayer == 'B')
            handleAIMove();
        
    }

    // Funcion que maneja el movimiento hecho por el usuario
    public void handleHumanMove(int row, int col){
        if (gameBoard.isValidMove(new int[]{row, col})){
            // Si es el primer movimiento del humano y el humano juega con negras
            if (isFirstMove && humanPlayer == 'B'){
                // Se coloca solo una ficha negra
                gameBoard.makeMove(new int[]{row, col}, null, humanPlayer);
                gui.updateButton(new int[]{row, col}, gui.getHumanColor());
    
                isFirstMove = false;
    
                // Para asegurarse de que la GUI se actualice antes de pasar a la IA
                SwingUtilities.invokeLater(() -> handleAIMove());
                return;
            }
    
            // Movimientos regulares (dos fichas)
            humanMoves[humanMoveCount] = new int[]{row, col};
            humanMoveCount++;
    
            // Se actualiza la interfaz gráfica
            gui.updateButton(new int[]{row, col}, gui.getHumanColor());
    
            // Si el humano ya realizó sus dos movimientos
            if (humanMoveCount == 2){
                gameBoard.makeMove(humanMoves[0], humanMoves[1], humanPlayer);
                humanMoveCount = 0;
    
                // Si el humano gano
                if (gameBoard.isWinner(humanPlayer)){
                    gui.showMessage("Ganaste!");
                    return;
                }
    
                // Igual que antes
                SwingUtilities.invokeLater(() -> handleAIMove());
            }
        }
    }
    
    // Funcion que maneja los movimientos de la IA
    public void handleAIMove(){
        if (gameBoard.getCurrentPlayer() == aiPlayer){
            
            if (gameBoard.getLastMoves() == null && aiPlayer == 'B'){
                // Primer movimiento de la IA (si juega con negro)
                int[][] firstMove = {{19 / 2, 19 / 2}};
                gameBoard.makeMove(firstMove[0], null, aiPlayer);
                gui.updateButton(firstMove[0], gui.getAIColor());
            } else{
                // Movimientos regulares de la IA
                int[][] aiMoves = ai.findBestMove(gameBoard);
                if(aiMoves == null || aiMoves[0] == null){
                    System.err.println("Error: la IA no genero movimientos validos");
                }
                gameBoard.makeMove(aiMoves[0], aiMoves[1], aiPlayer);
                gui.updateButton(aiMoves[0], gui.getAIColor());
                if(aiMoves[1] != null)
                    gui.updateButton(aiMoves[1], gui.getAIColor());
            }

            // Si la IA gano
            if (gameBoard.isWinner(aiPlayer)) 
                gui.showMessage("La IA gano!");
        }
    }

}
