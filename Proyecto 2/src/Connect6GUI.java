import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Connect6GUI extends JFrame{
    private Color humanColor; // Color para las fichas del humano
    private Color aiColor; // Color para las fichas de la IA

    public Color getHumanColor(){
        return humanColor;
    }
    
    public Color getAIColor(){
        return aiColor;
    }
    
    private static final int BOARD_SIZE = 19; // Tamaño del tablero
    private static final int CELL_SIZE = 40; // Tamaño de cada celda
    private static final int BUTTON_SIZE = 12; // Tamaño del botón en las intersecciones

    private JPanel boardPanel; // Panel que contiene el tablero
    private Connect6Game game; // Referencia a la clase principal que controla el juego

    public Connect6GUI(Connect6Game game, char hColor, char aColor){
        this.game = game;
        this.humanColor = hColor == 'B' ? Color.BLACK : Color.WHITE;
        this.aiColor = aColor == 'B' ? Color.BLACK : Color.WHITE;
        setTitle("Connect 6");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLayout(new BorderLayout());

        boardPanel = new BoardPanel();
        boardPanel.setLayout(null); // Posicionamiento absoluto para los botones

        // Crear los botones en las intersecciones
        for (int row = 0; row < BOARD_SIZE; row++){
            for (int col = 0; col < BOARD_SIZE; col++){
                JButton button = new JButton();
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setBounds(
                    col * CELL_SIZE + CELL_SIZE / 2 - BUTTON_SIZE / 2,
                    row * CELL_SIZE + CELL_SIZE / 2 - BUTTON_SIZE / 2,
                    BUTTON_SIZE, BUTTON_SIZE
                );

                button.setActionCommand(row + "," + col);
                boardPanel.add(button);

                // Enlaza el botón con el manejador de movimientos del humano
                button.addActionListener((ActionEvent e) ->{
                    String[] parts = button.getActionCommand().split(",");
                    int rowPos = Integer.parseInt(parts[0]);
                    int colPos = Integer.parseInt(parts[1]);
                    game.handleHumanMove(rowPos, colPos); 
                });
            }
        }

        // Etiquetas de filas
        JPanel rowLabels = new JPanel(new GridLayout(BOARD_SIZE, 1));
        for (int i = 1; i <= BOARD_SIZE; i++){
            rowLabels.add(new JLabel(String.valueOf(i), SwingConstants.CENTER));
        }

        // Etiquetas de columnas
        JPanel colLabels = new JPanel(new GridLayout(1, BOARD_SIZE));
        for (int i = 0; i < BOARD_SIZE; i++){
            colLabels.add(new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER));
        }

        add(rowLabels, BorderLayout.WEST);
        add(colLabels, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);

        setVisible(true);
    }

        // Clase personalizada para dibujar el tablero (lineas y fondo)
        private class BoardPanel extends JPanel{
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
    
                // Pintamos el fondo del tablero
                g2.setColor(new Color(193, 154, 107)); 

                g2.fillRect(0, 0, getWidth(), getHeight());
    
                // Dibuja las líneas del tablero
                g2.setColor(Color.BLACK);
                for (int i = 0; i < BOARD_SIZE; i++){
                    // Líneas horizontales
                    g2.drawLine(CELL_SIZE / 2, i * CELL_SIZE + CELL_SIZE / 2,
                                (BOARD_SIZE - 1) * CELL_SIZE + CELL_SIZE / 2, i * CELL_SIZE + CELL_SIZE / 2);
                    // Líneas verticales
                    g2.drawLine(i * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 2,
                                i * CELL_SIZE + CELL_SIZE / 2, (BOARD_SIZE - 1) * CELL_SIZE + CELL_SIZE / 2);
                }
            }
    
            @Override
            public Dimension getPreferredSize(){
                return new Dimension(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE);
            }
        }

        // Funcion que actualiza el boton despues de seleccionarlo en el tablero
        public void updateButton(int[] position, Color color){
            for (Component comp : boardPanel.getComponents()){
                if (comp instanceof JButton){
                    JButton btn = (JButton) comp;
                    String[] parts = btn.getActionCommand().split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
        
                    if (row == position[0] && col == position[1]){
                        btn.setOpaque(true);
                        btn.setContentAreaFilled(true);
                        btn.setBackground(color);
                        btn.setEnabled(false);
                        boardPanel.repaint(); // Esto fuerza la actualizacion del tablero
                        break;
                    }
                }
            }
        }
        

    // Funcion que imprime un mensaje
    public void showMessage(String message){
        JOptionPane.showMessageDialog(this, message);
    }

}

