# Agente de IA para jugar Connect 6

Franco Murillo - 1610782  
Alejandro Zambrano - 1710684

----------------------------------------------------------------------------------------------------------------------------

## Qué es el Connect 6

- __Connect 6__ es un juego de mesa con un tablero 18X18, donde a cada jugador le corresponden unas fichas (negras o blancas).

- El objetivo de cada jugador es colocar sus fichas en las casillas del tablero, de tal forma que conecten 6 o más fichas de su color.

- Siempre comienza el jugador con las __fichas negras__. 
    - En la primera jugada, el jugador debe colocar una ficha negra en cualquier casilla del tablero.

![Ejemplo de Estado Inicial del Juego](https://github.com/f-murillo/CI5437/blob/main/Proyecto%202/images/EstadoInicial.png)

- Luego de la primera jugada, el jugador con las fichas blancas debe colocar dos fichas en cualquier parte del tablero.

![Ejemplo de la Segunda Jugada](https://github.com/f-murillo/CI5437/blob/main/Proyecto%202/images/PrimeraJugadaBlancas.png)

- Ya después de haber realizado la jugada que comienza el juego, el jugador con negras ya puede colocar dos fichas donde quiera.

![Ejemplo de la Segunda Jugada de Negras](https://github.com/f-murillo/CI5437/blob/main/Proyecto%202/images/SegundaJugadaNegras.png)

- Finalmente, si algún jugador logra conectar 6 o más fichas de su color, gana el juego.

![Ejemplo de Estado Final del Juego](https://github.com/f-murillo/CI5437/blob/main/Proyecto%202/images/EstadoFinal.png)

----------------------------------------------------------------------------------------------------------------------------

## Sobre el proyecto

- El proyecto fue realizado en __Java__.

- El objetivo fue programar un agente de IA que pudiese jugar al Connect 6 con el usuario, ya sea con fichas negras, o blancas.

- Además, se agregó un temporizador máximo (en segundos) para que el agente decidiera qué jugadas realizar.

- Se creó la interfaz gráfica de usuario (__GUI__) usando la librería __JFrame__ de Java.

- El agente hace uso del algoritmo __Monte Carlo Tree Search (MCTS)__ para escoger las jugadas a realizar. 

----------------------------------------------------------------------------------------------------------------------------

## Sobre Monte Carlo Tree Search (MCTS)

MCTS funciona de la siguiente manera:

- Se parte desde el estado inicial.

- A partir de éste, escoge el siguiente nodo a expandir mediante la fórmula Upper Confidence Bound for Trees (UCT):

```math
UCT = {w_i \over {s_i}} + {c\sqrt{ln(s_p) \over s_i}} 
```

- Donde:
  - w_i = número de simulaciones con victorias para el estado
  - s_i = total de simulaciones para el estado  
  - s_p = total de simulaciones para el estado padre
  - c = parámetro de exploración. Generalmente se escoge: c = √2

- Si el nodo escogido no ha sido completamente explorado, se agregan los posibles movimientos como hijos en el árbol de búsqueda.

    - Simula una partida desde el nuevo nodo hasta llegar a un estado terminal, siguiendo algunas heurísticas basadas en las reglas del juego. Durante las simulaciones, va estableciendo puntuaciones a los estados. 

    - Según el resultado de la simulación, actualiza las puntuaciones de los estados (retropropagación). Si la simulación lleva a una victoria, se aumentan las puntuaciones; si lleva a una derrota, se disminuyen.

    - Al final, se escogen aquellas jugadas con mayor puntuación.

----------------------------------------------------------------------------------------------------------------------------

## Sobre el estado actual, y las dificultades del Proyecto

- El proyecto actualmente crea efectivamente la GUI, y permite jugar con al usuario contra el agente de IA.

- Sin embargo, se encontraron algunas dificultades:

    - La IA no es perfecta jugando, muchas veces decide jugadas que parecen aleatorias o erróneas, y en algunos casos no es capaz de percibir las amenazas de victoria inminente del contrincante.

- Se plantea la posibilidad de implementar distintos algoritmos en lugar de MCTS para comprobar si la IA mejora las deficiencias expuestas anteriormente, como la Poda Alpha-Beta, o el algoritmo Probcut. 

- Sin embargo, los resultados obtenidos con la implementación actual de MCTS siguen siendo buenos, por lo que también puede plantearse agregar más heurísticas basadas en la funcionalidad del juego, para mejorar la toma de decisiones de la IA.

----------------------------------------------------------------------------------------------------------------------------

## Compilación y Ejecución del Programa (teniendo instalado JDK del 17 en adelante)

```
javac Main.java Connect6Game.java Connect6GUI.java Connect6Board.java Connect6MCTS.java
```
## Ejecución del programa 
```
java Main <color de fichas de la IA> <tiempo máximo en segundos para la IA>
```
