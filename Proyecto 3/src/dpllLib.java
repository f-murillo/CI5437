// Este archivo contiene las funciones que resuelven el problema SAT usando el algoritmo DPLL
import java.io.*;
import java.util.*;

public class dpllLib{
    /*
     * Funcion que realiza el parseo de un archivo DIMACS simplificado para obtener una formula booleana
     * Se asume que cada clausula finaliza con un 0
     */
    public static List<List<Integer>> parseDimacs(File file) throws IOException{
        List<List<Integer>> formula = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while((line = br.readLine()) != null){
            line = line.trim();
            // Se ignoran lineas vacias, la cabecera y los comentarios
            if(line.isEmpty() || line.startsWith("p") || line.startsWith("c")) 
                continue;
            
            String[] tokens = line.split("\\s+");
            List<Integer> clause = new ArrayList<>();
            for(String token : tokens){
                // Si se encuentra el token "%", se asume que es el final de la seccion de clausulas (para los tipos de pruebas SAT "uf")
                if(token.equals("%"))
                    break;
            
                int literal = Integer.parseInt(token);
                if(literal == 0)
                    break; // Fin de la clausula
                
                clause.add(literal);
            }

            // Si hay clausulas que se hayan acumulado, se agregan a la formula
            if(!clause.isEmpty()) 
                formula.add(clause);
        }
        br.close();
        return formula;
    }
    
    // Funcion que busca una clausula unitaria (con un solo literal) 
    private static Integer findUnitClause(List<List<Integer>> formula){
        for(List<Integer> clause : formula){
            if(clause.size() == 1) 
                return clause.get(0);
        }
        return null;
    }
    
    // Funcion que encuentra literales puros en la formula 
    private static Set<Integer> findPureLiterals(List<List<Integer>> formula){
        Set<Integer> positive = new HashSet<>();
        Set<Integer> negative = new HashSet<>();
        // Por cada clausula en la formula
        for(List<Integer> clause : formula){
            // Por cada literal en la clausula
            for(int literal : clause){
                if(literal > 0)
                    positive.add(literal);
                else
                    negative.add(-literal);
            }
        }

        Set<Integer> pureLiterals = new HashSet<>();
        // Literales que aparecen solo de forma positiva
        for(int lit : positive){
            if(!negative.contains(lit))
                pureLiterals.add(lit);
        }
        // Literales que aparecen solo de forma negativa
        for(int lit : negative){
            if(!positive.contains(lit)) 
                pureLiterals.add(-lit);
        }
        return pureLiterals;
    }

    /*
     * Funcion que simplifica la formula aplicando la asignacion del literal.
     * Elimina de la formula las clausulas que se satisfacen con el literal,
     * y remueve el literal opuesto en las clausulas donde aparezca.
     */
    private static List<List<Integer>> simplifyFormula(List<List<Integer>> formula, int literal){
        List<List<Integer>> newFormula = new ArrayList<>();
        for(List<Integer> clause : formula){
            if(clause.contains(literal)) // La clausula se satisface. Se omite
                continue;
            
            // Se crea una nueva clausula sin el literal en conflicto
            List<Integer> newClause = new ArrayList<>();
            for(int lit : clause){
                if(lit == -literal)
                    continue;  // Se elimina el literal opuesto
                newClause.add(lit);
            }
            // Si la clausula quedo vacia tras la eliminacion, hay conflicto.
            if(newClause.isEmpty())
                return null;
            newFormula.add(newClause);
        }
        return newFormula;
    }

    //Funcion que selecciona un literal para bifurcar. Se escoge el primer literal que aparece
    private static int chooseLiteral(List<List<Integer>> formula){
        for(List<Integer> clause : formula){
            if(!clause.isEmpty()) 
                return clause.get(0);
        }
        throw new RuntimeException("No se pudo elegir un literal.");
    }
    
    //Funcion recursiva del algoritmo DPLL
    public static boolean dpll(List<List<Integer>> formula, Map<Integer, Boolean> assignment){
        // Caso base: formula vacia: se han satisfecho todas las clausulas
        if(formula.isEmpty())
            return true;
        
        // Si existe alguna clausula vacia, se ha llegado a un conflicto
        for(List<Integer> clause : formula){
            if(clause.isEmpty())
                return false;
        }
    
        // Propagacion de clausulas unitarias
        Integer unitLiteral = findUnitClause(formula);
        while(unitLiteral != null){
            assignment.put(Math.abs(unitLiteral), unitLiteral > 0);
            formula = simplifyFormula(formula, unitLiteral);
            if(formula == null)
                return false;
            // Se vuelve a comprobar si la formula quedo vacia y si existe alguna clausula vacia
            if(formula.isEmpty())
                return true;
            for(List<Integer> clause : formula){
                if(clause.isEmpty())
                    return false;
                
            }
            unitLiteral = findUnitClause(formula);
        }
    
        // Eliminacion de literales puros (se recalcula hasta que no se encuentre ninguno)
        Set<Integer> pureLiterals = findPureLiterals(formula);
        while(!pureLiterals.isEmpty()){ // Mientras haya literales puros
            for(Integer pureLiteral : pureLiterals){
                assignment.put(Math.abs(pureLiteral), pureLiteral > 0);
                formula = simplifyFormula(formula, pureLiteral);
                if(formula == null)
                    return false;
            }
            // Se vuelve a comprobar si la formula quedo vacia y si existe alguna clausula vacia
            if(formula.isEmpty()){
                return true;
            }
            for(List<Integer> clause : formula){
                if(clause.isEmpty())
                    return false;
            }
            pureLiterals = findPureLiterals(formula);
        }
        
        // Verificacion final antes de elegir un literal
        if(formula.isEmpty()) 
            return true;
        for(List<Integer> clause : formula){
            if(clause.isEmpty())
                return false;
            
        }
    
        int literal = chooseLiteral(formula); // Literal para bifurcar
    
        // Prueba asignando el literal a verdadero
        Map<Integer, Boolean> assignmentTrue = new HashMap<>(assignment);
        assignmentTrue.put(Math.abs(literal), true);
        List<List<Integer>> formulaTrue = simplifyFormula(formula, literal);
        if(formulaTrue != null && dpll(formulaTrue, assignmentTrue)){
            assignment.clear();
            assignment.putAll(assignmentTrue);
            return true;
        }
        
        // Prueba asignando el literal a falso (-literal)
        Map<Integer, Boolean> assignmentFalse = new HashMap<>(assignment);
        assignmentFalse.put(Math.abs(literal), false);
        List<List<Integer>> formulaFalse = simplifyFormula(formula, -literal);
        if(formulaFalse != null && dpll(formulaFalse, assignmentFalse)){
            assignment.clear();
            assignment.putAll(assignmentFalse);
            return true;
        }
        
        // Si se llega a este punto, ninguna asignacion conduce a una solucion
        return false;
    }
}
