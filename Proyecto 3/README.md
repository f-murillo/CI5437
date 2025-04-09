# Resolvedor de Sudoku con SAT

Franco Murillo - 1610782

----------------------------------------------------------------------------------------------------------------------------

El objetivo del proyecto es el de transformar el problema del Sudoku a fórmulas lógicas para ser resueltos mediante un resolvedor de problemas lógicos SAT, para posteriormente transformar la solución obtenida a Sudoku, obteniendo la solución del tablero.  

Es un problema algo complejo de entender. Así que primero, un poco de contexto:

----------------------------------------------------------------------------------------------------------------------------

## Qué es Sudoku

- El sudoku es un juego matemático que consta de una cuadrícula 9x9, que se divide en 9 subcuadrículas 3x3. En cada cuadro de la cuadrícula puede ir un número entre 1 y 9.

- En un inicio, la cuadrícula se encuentra parcialmente vacía, con algunos números ingresados:

![Estado Inicial de Sudoku](https://github.com/f-murillo/CI5437/blob/main/Proyecto%203/images/Estado%20inicial.png)

- El objetivo es rellenar el resto de la cuadrícula de tal forma que:

    - En cada cuadro de cada fila haya un número distinto.
    - En cada cuadro de cada columna haya un número distinto.
    - En cada cuadro de cada subcuadrícula 3x3 haya un número distinto.

![Estado Final de Sudoku](https://github.com/f-murillo/CI5437/blob/main/Proyecto%203/images/Estado%20final.png)

----------------------------------------------------------------------------------------------------------------------------

## Qué es el problema SAT

El problema SAT (Satisfacibilidad Booleana) es un problema de la lógica proposicional. Es importante dentro de la teoría de la complejidad computacional, y se encuentra entre los problemas NP-completos. Se plantea de la siguiente manera:

- Dada una fórmula booleana, se quiere saber si __existe una asignación de valores__ de verdad (true o false) a las variables de la fórmula, tal que la fórmula __evalúe a true__.

- Usualmente se trabaja con fórmulas expresadas en Forma Normal Conjuntiva (CNF). 
        
  - Así, cada fórmula es una conjunción (AND) de varias cláusulas, y cada cláusula es una disyunción (OR) de __literales__.

  - Un __literal__ es una variable booleana o su negación.

- Por ejemplo: sea la fórmula (v₁ ∨ ¬v₂ ∨ v₃) ∧ (¬v₁ ∨ v₂) expresada en CNF. 
  - En este caso, la primera cláusula se satisface si al menos uno de los literales v₁, ¬v₂ o v₃ es verdadero, y la segunda cláusula debe satisfacerse con al menos uno de ¬v₁ o v₂.

----------------------------------------------------------------------------------------------------------------------------

### Cómo se resuelve el problema SAT

Existen varias formas de resolver el problema SAT, pero en este proyecto se enfocó en el algoritmo Davis–Putnam–Logemann–Loveland (__DPLL__).

----------------------------------------------------------------------------------------------------------------------------

### Algoritmo DPLL

- Es un __algoritmo completo__, basado en __bactracking__, que verifica si una fórmula de lógica proposicional (en una forma CNF) es o no satisfacible, es decir, si existe o no alguna asignación de variables que hacen que la fórmula evalúe a true.

- Funciona de la siguiente manera:

    - __Verificación de los casos base__:

        - Fórmula vacía: Si todas las cláusulas han sido satisfechas (es decir, se han eliminado de la fórmula) significa que existe una asignación válida.

        - Cláusula vacía: Si alguna cláusula queda vacía, quiere decir que se ha producido un conflicto (ninguna asignación posible puede satisfacer esa cláusula).

    - __Propagación unitaria__:

        - Una __cláusula unitaria__ es aquella que contiene únicamente un literal.

        - Dado que esa cláusula solo puede satisfacerse asignando a esa variable el valor correspondiente (verdadero si el literal es positivo, o falso si es negativo), el algoritmo asigna el valor forzadamente.

        - Esto propaga automáticamente restricciones a otras cláusulas y simplifica la fórmula.

        - Como resultado, se eliminan las cláusulas satisfechas y se remueve la presencia del literal opuesto en las cláusulas restantes.

    - __Eliminación de literales puros__:

        - Un literal es __puro__ si aparece en la fórmula únicamente con una misma polaridad (solo positivo o solo negativo).

        - Al ser el literal puro "inofensivo" para la fórmula, se puede asignar de forma directa (por ejemplo, se asigna verdadero si el literal aparece solo en forma positiva), y eliminar todas las cláusulas en las que aparece.

        - De esta forma, se reduce el espacio de búsqueda, sin riesgos de generar conflictos.

    - __Bifurcación (branching)__:

        - Si la fórmula aún contiene cláusulas sin satisfacer, se __elige un literal arbitrario__ (se puede elegir usando alguna heurística, pero para el proyecto se optó por lo más sencillo, que fue escoger el primer literal disponible).

        - Se prueba, en primer lugar, asignando el literal como __verdadero__. Se simplifica la fórmula con esa asignación y se invoca recursivamente a DPLL.

            - Si esta asignación conduce a que se satisfaga la fórmula, se concluye que la instancia es satisfacible.

            - Si no funciona, se realiza la prueba asignando el literal como __falso__ (que sería equivalente a asignar el valor opuesto) y se vuelve a invocar recursivamente a DPLL.

    - __Backtracking__:

        - Si ninguna de las asignaciones (literal verdadero o falso) resulta en una fórmula satisfacible, se retrocede y se prueba otra opción en la rama anterior.

        - Esto permite explorar otras asignaciones, hasta agotar todas las posibilidades o encontrar una solución.

- De esta forma, se puede pasar al algoritmo DPLL una fórmula en CNF, y ésta determina si es o no satisfacible.

----------------------------------------------------------------------------------------------------------------------------

## Cómo se usa DPLL para resolver el Sudoku

- Lo interesante del algoritmo DPLL, y de cualquier algoritmo que resuelva el problema SAT, es que, si podemos transformar un problema cualquiera a uno de tipo SAT, estos algoritmos lo pueden resolver, __sin necesidad de ser adaptados__ al problema original que se quiere resolver.

- Para el caso del Sudoku, la idea es la siguiente:

    - Dada una instancia de Sudoku (un tablero), se debe "traducir" la instancia a una fórmula lógica en CNF.

    - Ya con la fórmula en CNF, se aplica el algoritmo DPLL para obtener la asignación para la cual la fórmula evalúa a true.

    - Teniendo la asignación de variables, se "traduce" de vuelta a la instancia Sudoku, obteniendo así la solución del tablero.

- Así, ya teniendo el solver para SAT, la dificultad está en la __traducción de los problemas a SAT__, y la __traducción de la solución obtenida__.

----------------------------------------------------------------------------------------------------------------------------

### Cómo se traduce de Sudoku a CNF

- A cada celda del Sudoku se le asocia una de nueve posibilidades (dígitos del 1 al 9), y a cada posibilidad se le asigna una variable booleana. 

- La idea es que “la variable X es verdadera” signifique que en esa celda se asigna el dígito correspondiente.

- Para garantizar que la solución del SAT sea un sudoku válido, se formulan las reglas del juego en forma de cláusulas lógicas en CNF:

    - __Una cifra por celda__: 
        - Se generan cláusulas que obligan a que, para cada celda, al menos una de sus nueve variables (cada una representando un dígito distinto) sea verdadera. 
        - Además, para evitar asignar dos dígitos a la misma celda, se añaden cláusulas que impiden que dos variables de la misma celda puedan ser verdaderas simultáneamente.

    - __Unicidad en filas, columnas y bloques__:
        - Para cada fila, columna y bloque 3×3, se imponen restricciones para que cada dígito aparezca exactamente una vez. 
        
        - Esto se hace de dos formas:

            - Se asegura que, en cada conjunto (fila, columna o bloque), cada dígito aparezca al menos una vez (agregando cláusulas que unen todas las celdas del conjunto para ese dígito).

            - Se incluye que ningún dígito se repita (añadiendo cláusulas que evitan que dos celdas del mismo conjunto contengan el mismo dígito).

- Por último, los dígitos que ya están dados en el tablero de Sudoku inicial se incluyen como condiciones fijas (cláusulas unitarias) que fijan el valor de la celda correspondiente.

- El resultado es una fórmula en CNF que es satisfacible si y solo si el Sudoku tiene solución, y cuya solución (si existe) corresponde directamente a una asignación de dígitos en el tablero.

----------------------------------------------------------------------------------------------------------------------------

### Cómo se traduce de vuelta a Sudoku

- Una vez que el algoritmo DPLL encuentra una asignación que satisface la fórmula, la idea es revertir el proceso para obtener la solución del Sudoku. Esto se hace de la siguiente manera:

    - La asignación de variables (cada variable verdadera o falsa) tiene una correspondencia directa con la afirmación “la celda (i, j) contiene el dígito d”. Por ello, se recorre cada celda y se examinan las variables asociadas a ella.

    - Para cada celda, se busca el dígito para el cual la variable correspondiente fue asignada como verdadera. Ese dígito es el que se coloca en la solución del tablero. Al hacer esto para todas las celdas, se reconstruye el sudoku resuelto.

- Así, se obtiene de vuelta un tablero Sudoku, que corresponde a la solución del tablero inicial.

----------------------------------------------------------------------------------------------------------------------------

## Sobre el Proyecto

- El proyecto fue realizado en __Java__.

- El proyecto consta de __dos actividades__:

    - Primera Actividad:
    
        - Implementar DPLL, y poder determinar la satisfacibilidad de fórmulas CNF. 

        - Para las pruebas se usaron los archivos con fórmulas CNF que se encuentran en: https://www.cs.ubc.ca/~hoos/SATLIB/benchm.html 

    - Segunda Actividad:

        - Implementar un programa que traduzca instancias de Sudoku a una fórmula CNF.

            - Una instancia de Sudoku se representa como un String de 81 caracteres:

                - Cada 9 caracteres corresponden a una fila de la cuadrícula 9x9.

                - Las casillas vacías se representan como un "."

                - Ejemplo de instancia: ...1.9.........6.34....3.....75.6.....8...3..92..8..14.....1.......7...8.1..4..7.

                  - En el archivo "20_instancias_de_sudoku.txt" se encuentran varias instancias para probar.
                    
        - Pasar la fórmula al algoritmo DPLL realizado en la actividad anterior.

        - Obtener la asignación de variables que evalúan a true la fórmula, y traducirla de vuelta a Sudoku.

            - Por ejemplo, la solución para la instancia mostrada antes sería: 735169482281754693469823751347516829158492367926387514872631945694275138513948276

- El proyecto tiene __4 archivos__:

    - __dpllLib.java__

        - Contiene la solución al problema SAT, usando DPLL.
    
    - __dpllSolver.java__

        - Recibe un archivo con una fórmula CNF, y verifica si es o no satisfacible.

        - Además, indica el tiempo (en segundos) que tomó determinar si es satisfacible o no.

    - __sudokuSAT.java__

        - Contiene las funciones que traducen instancias de Sudoku a CNF, y que traducen las soluciones a instancias de Sudoku.

    - __sudokuSolver.java__

        - Recibe una instancia de Sudoku, y llama a las funciones para traducirla, resolverla y traducirla de vuelta.

        - Además, indica el tiempo (en segundos) que tomó encontrar la solución.

----------------------------------------------------------------------------------------------------------------------------

## Compilación (teniendo instalado JDK del 17 en adelante)

```
javac sudokuSolver.java sudokuSAT.java dpllSolver.java dpllLib.java
```

----------------------------------------------------------------------------------------------------------------------------

## Ejemplos de Uso

### dpllSolver

```
java dpllSolver uf20-0999.cnf
SATISFIABLE
Tiempo de resolucion: 0.0015887 segundos
```

### sudokuSolver

```
java sudokuSolver ...1.9.........6.34....3.....75.6.....8...3..92..8..14.....1.......7...8.1..4..7.
735169482281754693469823751347516829158492367926387514872631945694275138513948276
Tiempo de resolucion: 0.2078776 segundos
```
