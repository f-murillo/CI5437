// Este archivo contiene la logica que traduce una instancia Sudoku en un problema SAT en forma CNF
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class sudokuSAT{

    /*
     * Funcion que asigna un numero unico a la variable correspondiente a la celda (i,j) 
     * con el digito d
     * Usamos la convencion: v(i,j,d) = 100*i + 10*j + d, donde i, j, d estan entre 1 y 9
     */
    public static int var(int i, int j, int d){
        return 100 * i + 10 * j + d;
    }

    /*
     * Funcion que transforma una instancia de Sudoku en una formula CNF
     * que cumpla las restricciones del sudoku
     */
    public static List<List<Integer>> sudokuToCNF(String sudokuInstance){
        List<List<Integer>> clauses = new ArrayList<>();

        // Restricciones
        // 1. Cada celda tiene al menos un digito.
        for(int i = 1; i <= 9; i++){
            for(int j = 1; j <= 9; j++){
                List<Integer> clause = new ArrayList<>();
                for(int d = 1; d <= 9; d++){
                    clause.add(var(i, j, d));
                }
                clauses.add(clause);
            }
        }

        /*
         *  2. Cada celda tiene a lo sumo un digito: para cada celda e, 
         *     y cada par (d, e) (con d < e) se agrega: (-v(i,j,d) OR -v(i,j,e))
         */
        for(int i = 1; i <= 9; i++){
            for(int j = 1; j <= 9; j++){
                for(int d = 1; d <= 9; d++){
                    for(int e = d + 1; e <= 9; e++){
                        List<Integer> clause = new ArrayList<>();
                        clause.add(-var(i, j, d));
                        clause.add(-var(i, j, e));
                        clauses.add(clause);
                    }
                }
            }
        }

        // 3. Restricciones de fila
        //  a) Cada digito aparece al menos una vez en cada fila.
        for(int i = 1; i <= 9; i++){
            for(int d = 1; d <= 9; d++){
                List<Integer> clause = new ArrayList<>();
                for(int j = 1; j <= 9; j++){
                    clause.add(var(i, j, d));
                }
                clauses.add(clause);
            }
        }
        // b) Cada digito no se repite en la misma fila: para cada par de columnas distintas.
        for(int i = 1; i <= 9; i++){
            for(int d = 1; d <= 9; d++){
                for(int j = 1; j <= 9; j++){
                    for(int k = j + 1; k <= 9; k++){
                        List<Integer> clause = new ArrayList<>();
                        clause.add(-var(i, j, d));
                        clause.add(-var(i, k, d));
                        clauses.add(clause);
                    }
                }
            }
        }

        // 4. Restricciones de columna
        // a) Cada digito aparece al menos una vez en cada columna.
        for(int j = 1; j <= 9; j++){
            for(int d = 1; d <= 9; d++){
                List<Integer> clause = new ArrayList<>();
                for(int i = 1; i <= 9; i++){
                    clause.add(var(i, j, d));
                }
                clauses.add(clause);
            }
        }
        // b) Cada digito no se repite en la misma columna: para cada par de filas distintas.
        for(int j = 1; j <= 9; j++){
            for(int d = 1; d <= 9; d++){
                for(int i = 1; i <= 9; i++){
                    for(int k = i + 1; k <= 9; k++){
                        List<Integer> clause = new ArrayList<>();
                        clause.add(-var(i, j, d));
                        clause.add(-var(k, j, d));
                        clauses.add(clause);
                    }
                }
            }
        }

        // 5. Restricciones de bloque 3x3
        // a) Cada digito aparece al menos una vez en cada bloque.
        for(int blockRow = 0; blockRow < 3; blockRow++){
            for(int blockCol = 0; blockCol < 3; blockCol++){
                for(int d = 1; d <= 9; d++){
                    List<Integer> clause = new ArrayList<>();
                    for(int i = 1; i <= 3; i++){
                        for(int j = 1; j <= 3; j++){
                            int row = blockRow * 3 + i;
                            int col = blockCol * 3 + j;
                            clause.add(var(row, col, d));
                        }
                    }
                    clauses.add(clause);
                }
            }
        }
        // b) Cada digito no se repite en cada bloque: para cada par de celdas distintas en el bloque.
        for(int blockRow = 0; blockRow < 3; blockRow++){
            for(int blockCol = 0; blockCol < 3; blockCol++){
                for(int d = 1; d <= 9; d++){
                    List<int[]> cells = new ArrayList<>();
                    for(int i = 1; i <= 3; i++){
                        for(int j = 1; j <= 3; j++){
                            int row = blockRow * 3 + i;
                            int col = blockCol * 3 + j;
                            cells.add(new int[]{row, col});
                        }
                    }
                    for(int a = 0; a < cells.size(); a++){
                        for(int b = a + 1; b < cells.size(); b++){
                            int[] cellA = cells.get(a);
                            int[] cellB = cells.get(b);
                            List<Integer> clause = new ArrayList<>();
                            clause.add(-var(cellA[0], cellA[1], d));
                            clause.add(-var(cellB[0], cellB[1], d));
                            clauses.add(clause);
                        }
                    }
                }
            }
        }

        // Agregamos las pistas (clausulas unitarias) de la instancia de Sudoku.
        for(int i = 0; i < 81; i++){
            char ch = sudokuInstance.charAt(i);
            if(ch != '.' && ch != '0'){
                int digit = Character.getNumericValue(ch);
                int row = i / 9 + 1;
                int col = i % 9 + 1;
                List<Integer> clause = new ArrayList<>();
                clause.add(var(row, col, digit));
                clauses.add(clause);
            }
        }
        return clauses;
    }

    /*
     * Funcion que decodifica una asignacion encontrada por DPLL y retorna la solucion
     * del Sudoku como un string de 81 digitos (fila a fila).
     */
    public static String decodeSudokuSolution(Map<Integer, Boolean> assignment){
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= 9; i++){
            for(int j = 1; j <= 9; j++){
                int digitFound = 0;
                for(int d = 1; d <= 9; d++){
                    int variable = var(i, j, d);
                    if(assignment.containsKey(variable) && assignment.get(variable)){
                        digitFound = d;
                        break;
                    }
                }
                sb.append(digitFound);
            }
        }
        return sb.toString();
    }
}
