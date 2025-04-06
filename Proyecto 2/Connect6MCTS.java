import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Connect6MCTS{
    // Clase para los nodos
    private class Node{
        Connect6Board state;
        Node parent;
        List<Node> children;
        int visits = 0;
        double score = 0.0;

        Node(Connect6Board state){
            this.state = state;
            this.children = new java.util.ArrayList<>();
        }
    }

    // Clase para los pares (movimiento, puntuacion) (se usara para determinar las jugadas "priorizadas")
    private class ScoredMove{
        int[] move; // Coordenadas del movimiento
        double score; // Puntuacion del movimiento
        // Constructor
        ScoredMove(int[] move, double score){
            this.move = move;
            this.score = score;
        }
    }

    private long timeLimitMillis; // Limite de tiempo en milisegundos (en Main.java se hace la conversion a segundos)
    // Constructor
    public Connect6MCTS(long timeLimitMillis){
        this.timeLimitMillis = timeLimitMillis;
    }

    // Funcion que retorna el hijo con el mayo valor UCT calculado
    private Node bestChild(Node node, double explorationConstant){
        int totalParentVisits = node.visits;
        return Collections.max(node.children, Comparator.comparingDouble(
            child -> (child.score / child.visits) + explorationConstant *
                     Math.sqrt(Math.log(totalParentVisits) / child.visits)
        ));
    }

    // Funcion que realiza la simulacion de un estado
    private double simulation(Node node, char currentPlayer){
        Connect6Board tempState = node.state.clone();
        while(!tempState.isWinner('B') && !tempState.isWinner('W')){ // Mientras no haya un ganador
            List<int[]> moves = tempState.getAvailableMoves(); 
            List<int[]> prioritizedMoves = prioritizeMoves(moves, tempState); 
        
            // Si no hay suficientes movimientos para realizar una jugada completa, se detiene
            if(prioritizedMoves.size() < 2) break;
        
            // Se seleccionan los dos movimientos con mayor prioridad
            int[] move1 = prioritizedMoves.get(0); // El mejor movimiento
            int[] move2 = prioritizedMoves.get(1); // El segundo mejor movimiento
        
            // Se realiza los movimientos en el tablero temporal
            tempState.makeMove(move1, move2, currentPlayer);
        }
        // Retorna la evaluacion sobre el estado generado
        return evaluate(tempState);
    }
    //  Funcion que propaga los resultados obtenidos durante una simulacion hacia arriba en el arbol de busqueda
    private void backpropagation(Node node, double result){
        while(node != null){
            node.visits++;
            node.score += result;
            node = node.parent;
        }
    }

    // Funcion que da valor al hecho de si un estado tiene fichas en el centro
    private double proximityToCenter(int row, int col){
        int center = 19 / 2;
        int distance = Math.abs(center - row) + Math.abs(center - col); // Distancia Manhattan
        return 10 / (1 + distance); 
    }

    // Funcion que evalua un estado
    private double evaluate(Connect6Board state){
        char[][] board = state.getBoard();
        double aiScore = 0.0;
        double opponentScore = 0.0;
    
        // Se evalua cada linea para cada jugador
        for(int row = 0; row < 19; row++){
            for(int col = 0; col < 19; col++){
                // Para la IA
                aiScore += evaluateLine(board, row, col, 1, 0, 'B', 'W'); // Horizontal
                aiScore += evaluateLine(board, row, col, 0, 1, 'B', 'W'); // Vertical
                aiScore += evaluateLine(board, row, col, 1, 1, 'B', 'W'); // Diagonal principal
                aiScore += evaluateLine(board, row, col, 1, -1, 'B', 'W'); // Diagonal secundaria
                // Para el usuario
                opponentScore += evaluateLine(board, row, col, 1, 0, 'W', 'B'); // Horizontal
                opponentScore += evaluateLine(board, row, col, 0, 1, 'W', 'B'); // Vertical
                opponentScore += evaluateLine(board, row, col, 1, 1, 'W', 'B'); // Diagonal principal
                opponentScore += evaluateLine(board, row, col, 1, -1, 'W', 'B'); // Diagonal secundaria
            }
        }
    
        // Se combina las puntuaciones de ataque y defensa
        return aiScore - opponentScore;
    }
    
    // Funcion que evalua cada linea
    private double evaluateLine(char[][] board, int row, int col, int rowDelta, int colDelta, char aiPlayer, char opponent){
        int aiCount = 0;
        int opponentCount = 0;
        double score = 0;
    
        for(int i = 0; i < 6; i++){ // Evalua las proximas 6 posiciones
            int r = row + i * rowDelta;
            int c = col + i * colDelta;
    
            if(r < 0 || r >= 19 || c < 0 || c >= 19){
                return 0; // Fuera de los limites
            }
    
            if(board[r][c] == aiPlayer){
                aiCount++;
            } else if(board[r][c] == opponent){
                opponentCount++;
            }
    
            // Se da una puntuacion adicional si esta cerca del centro del tablero
            score += proximityToCenter(r, c);
        }
    
        // Se asigna las puntuaciones basadas en las fichas consecutivas
        if(aiCount > 0 && opponentCount > 0)
            return 0; // Linea bloqueada

        if(aiCount > 0)
            return Math.pow(10, aiCount) + 2*score; // Priorizamos las lineas de la IA
            
        if(opponentCount > 0) 
            return -(Math.pow(10, opponentCount) + 2*score); // Penalizamos las lineas del oponente
        
        return 0.0;
    }

    // Metodo para evaluar las amenazas del oponente 
    private int evaluateThreats(Connect6Board board, char opponent){
        char[][] state = board.getBoard();
        int maxThreat = 0;
        
        for(int row = 0; row < 19; row++){
            for(int col = 0; col < 19; col++){
                // Se calcula el maximo entre maxThreat y la evaluacion por linea
                maxThreat = Math.max(maxThreat, (int)evaluateLine(state, row, col, 1, 0, opponent, '-')); // Horizontal
                maxThreat = Math.max(maxThreat, (int)evaluateLine(state, row, col, 0, 1, opponent, '-')); // Vertical
                maxThreat = Math.max(maxThreat, (int)evaluateLine(state, row, col, 1, 1, opponent, '-')); // Diagonal
                maxThreat = Math.max(maxThreat, (int)evaluateLine(state, row, col, 1, -1, opponent, '-')); // Diagonal inversa
            }
        }
        return maxThreat;
    }

    // Funcion que selecciona el nodo
    private Node selection(Node node){
        while(!node.children.isEmpty()){
            node = bestChild(node, Math.sqrt(2));
        }
        return node;
    }

    // Funcion que verifica si existen piezas cerca 
    private boolean isNearExistingPiece(int[] move, Connect6Board board){
        int row = move[0];
        int col = move[1];
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                int r = row + i, c = col + j;
                if(r >= 0 && r < 19 && c >= 0 && c < 19 && board.getBoard()[r][c] != '-') return true;
            }
        }
        return false;
    }
    
    // Funcion que evalua las poisibles amenazas del oponente
    private double evaluateThreatsForMove(Connect6Board board, int[] move, char opponent){
        Connect6Board testBoard = board.clone(); // Clonamos el estado del tablero
        testBoard.makeMove(move, null, board.getCurrentPlayer()); // Se aplica temporalmente el movimiento
        return evaluateThreats(testBoard, opponent); // Se calculan las amenazas
    }
        
    // Funcion que obtiene las jugadas mas "prometedoras" 
    private List<int[]> prioritizeMoves(List<int[]> moves, Connect6Board board){
        // Determinamos el jugador actual y el oponente
        char currentPlayer = board.getCurrentPlayer();
        char opponent = (currentPlayer == 'B') ? 'W' : 'B';
        
        // Cola de prioridad para los movimientos
        PriorityQueue<ScoredMove> pq = new PriorityQueue<>(
            (a,b) -> Double.compare(b.score, a.score) // Mientras mas grande el score, mayor prioridad
        );
        
        // Evalua cada movimiento
        for(int[] move : moves){
            double score = 0.0;
        
            // Prioridad por cercania a fichas existentes
            if(isNearExistingPiece(move, board)) score += 50;
        
            // Prioridad por cercania al centro
            score += proximityToCenter(move[0], move[1]);
        
            // Se evalua si este movimiento contribuye a extender lineas del jugador oponente
            score += evaluateThreatsForMove(board, move, opponent);

            // Penalizamos las esquinas, priorizando el juego sobre el centro del tablero 
            if(move[0] == 0 || move[0] == 18 || move[1] == 0 || move[1] == 18)
                score -= 100;
            
            // Se agrega el movimiento junto con su puntuacion
            pq.add(new ScoredMove(move, score));
        }
            
        // Se crea la lista priorizada
        List<int[]> prioritizedMoves = new java.util.ArrayList<>();
        while(!pq.isEmpty()){ // Mientras la cola no esta vacia, desencola los elementos y los agrega a la lista
            prioritizedMoves.add(pq.poll().move);
        }
        return prioritizedMoves;
    } 
    
    // Funcion que expande a otros nodos
    private void expansion(Node node, char currentPlayer){
        List<int[]> moves = node.state.getAvailableMoves(); // Movimuientos posibles
            moves = prioritizeMoves(moves, node.state); // Prioridades
        
        int limit = 1000000000; // Limite de nodos explorados
        int count = 0; // Contador
        
        for(int i = 0; i < moves.size(); i++){
            for(int j = i + 1; j < moves.size(); j++){
                if(count >= limit) return; // Se detiene la expansion despues de alcanzar el limite
                    
                Connect6Board newState = node.state.clone(); // Se clona el estado actual
                newState.makeMove(moves.get(i), moves.get(j), currentPlayer); // Se realiza el movimiento sobre el estado clonado
                // Evalamos el estado
                double moveScore = evaluate(newState);
                if(moveScore > 0){ // Solo expande para estados de evaluaciones positiva (poda)
                    Node child = new Node(newState);
                    child.parent = node;
                    node.children.add(child);
                    count++;
                }
            }
        }
    }

    // Funcion que determina que movimientos bloquena a los movimientos del oponente 
    private int[][] findBlockingMoves(Connect6Board board, char opponent, char aiPlayer){
        List<int[]> moves = board.getAvailableMoves();
        int bestThreatLevel = 0;
        int[][] bestBlockingMoves = null;
        
        for(int i = 0; i < moves.size(); i++){
            for(int j = i + 1; j < moves.size(); j++){
                // Se clona el estado actual, y se realiza el movimiento sobre este
                Connect6Board testBoard = board.clone();
                testBoard.makeMove(moves.get(i), moves.get(j), opponent);
        
                // Se valua la amenaza del oponente
                int threatLevel = evaluateThreats(testBoard, opponent);
                if(threatLevel > bestThreatLevel){
                    bestThreatLevel = threatLevel;
                    bestBlockingMoves = new int[][]{moves.get(i), moves.get(j)};
                }
            }
        }
        return bestBlockingMoves; 
    }
        
    // Funcion que encuentra la "mejor jugada" para la IA (se usa en Main.java, en handleIAMove)
    public int[][] findBestMove(Connect6Board board){
        char currentPlayer = board.getCurrentPlayer();
        char opponent = (currentPlayer == 'B') ? 'W' : 'B';

        // S verifica si la IA puede ganar en este turno
        List<int[]> availableMoves = board.getAvailableMoves();
        for(int i = 0; i < availableMoves.size(); i++){
            for(int j = i + 1; j < availableMoves.size(); j++){
                int[] move1 = availableMoves.get(i);
                int[] move2 = availableMoves.get(j);

                // Simula los movimientos
                Connect6Board testBoard = board.clone();
                testBoard.makeMove(move1, move2, currentPlayer);
                if(testBoard.isWinner(currentPlayer)){
                    return new int[][]{move1, move2}; // Si hay jugadas ganadoras inmediatas
                }
            }
        }

        // Si detecta "amenazas inminentes", bloquea
        int[][] blockingMoves = findBlockingMoves(board, opponent, currentPlayer); 
        if(blockingMoves != null) return blockingMoves; 
        
        // Si no hay amenazas inminentes, se aplica MCTS normal
        Node root = new Node(board);
        long startTime = System.currentTimeMillis();
        
        // Mientras no se haya acabado el tiempo establecido
        while(System.currentTimeMillis() - startTime < timeLimitMillis){
            Node selectedNode = selection(root);
            if(!selectedNode.state.isWinner('B') && !selectedNode.state.isWinner('W')) // Si no hay ganador
                expansion(selectedNode, board.getCurrentPlayer()); // Expande
            
            double result = simulation(selectedNode, board.getCurrentPlayer()); // Realiza la simulacion
            backpropagation(selectedNode, result); // Propaga los valores obtenidos
        }
        return bestChild(root, Math.sqrt(2)).state.getLastMoves();
    }   
}
