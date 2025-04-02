# Solver del 15-puzzle

Franco Murillo - 1610782  
Alejandro Zambrano - 1710684

----------------------------------------------------------------------------------------------------------------------------

## Qué es el 15-puzzle

- El 15-puzzle es un problema matemático que consta de un tablero 4x4, con una configuración de piezas enumeradas del 1 al 15, y un espacio en blanco.

- El objetivo es ir moviendo las piezas hacia el espacio en blanco, hasta llegar a una configuración meta establecida.

- Ejemplo:

  ![Ejemplo de configuración inicial y configuración meta](https://media.geeksforgeeks.org/wp-content/uploads/15-puzzle.png)

----------------------------------------------------------------------------------------------------------------------------

## Sobre el Proyecto

- El proyecto fue realizado en **Java**.

- Se modularizó el código con respecto al código entregado, para una mejor comprehensión y una mayor facilidad para su implementación.

- El programa pide al usuario ingresar una secuencia de 16 números, del 0 al 15, separadas por un espacio.

- Se asume que la secuencia ingresada es correcta.

- A partir de dicha secuencia, se crea el tablero, así como las instancias correspondientes para hallar la solución.
  
- El objetivo es poder computar la solución de las instancias propuestas en el artículo [[1]](#bibliografía) de manera competitiva con respecto a la solución propuesta en éste, que fue usar el algoritmo Bidirectional A* (__BA*__,  la cual aplica una búsqueda A* desde el estado inicial y el estado final, hasta que ambas búsquedas se encuentren en un punto intermedio), y haciendo uso de la heurística Hybrid Heuristic (HH):  

```math
    HH = {md(s) \over 3} + wd(s) + lc(s)   
```
- donde:  md = Manhattan Distance; wd = Walking Distance; lc = Linear Conflict (ver funcionamiento de las heursísticas en [[1]](#bibliografía)).

- El algoritmo escogido para el proyecto fue Iterative Deepening A* (__IDA*__), el cual establece un límite heurístico al principio (la heurística calculada sobre el estado inicial), y en cada iteración se realiza un recorrido DFS sobre los estados, actualizando dicho límite, hasta llegar al estado meta.

- La función verifica si se llegó a la solución, si al hacer la llamada recursiva que usa DFS, se obtiene como retorno -1

- OBSERVACIÓN: el código fue hecho considerando la solución como todos los números ordenados de 1 a 15, y con el el espacio en blanco al final (abajo y a la derecha).
  - Por esto, no funcionará para la tabla 6 de [[1]](#bibliografía), pues dicha tabla de casos considera la solución con el espacio en blanco al principio (arriba y a la izquierda).
  - Además, se asume que la secuencia ingresada (que representa un tablero) tiene solución. Esto, porque IDA* es un __algoritmo completo__, es decir, garantiza que encontrará una solución, si ésta existe.
  - Por lo que, si se ingresa una configuración que no tiene solución, se ejecutará el programa hasta que el contador de estados generados supere al máximo entero que puede representarse en Java (2.147.483.647).
  - Esto se puede solucionar implementando una función que verifique la paridad del tablero antes de intentar hallar la solución (pronto agraré una sección que hable sobre la paridad de un tablero), o simplemente agregando un contador máximo para los estados generados, o un temporizador para hallar la solución (en un futuro se probarán ambas alternativas también). 

- Así, el código funciona correctamente para las  tablas 3 y 4 de [[1]](#bibliografía), con la observación de que en la tabla 4, los primeros 11 casos están hechos considerando la solución con el espacio en blanco al principio, por lo que debe ser probado a partir del caso 12 hasta el 28.

>[!NOTE]
> En el archivo "pruebas.txt" se encontrarán todos los casos de prueba que se sacaron de [1], así como los casos usados para la evaluación del proyecto.
 
- La idea original era usar la heurística HH sobre el algoritmo IDA*; sin embargo, se vió que, si bien se obtenía soluciones de longitud óptima, el número de estados generados y el tiempo de respuesta no lo eran (se generaba un número enorme de estados, y podía tomar varios minutos en encontrar una solución). 

- Por esto, se investigó sobre distintas heurísticas, hasta que se encontró la heurística **Corner-Tile** en el artículo [[2]](#bibliografía), que habla sobre el 24-puzzle, y que considera lo siguiente:

    - En el problema del n-puzzle, las piezas más complicadas de ubicar en la solución son las esquinas.
    - La heurística verifica si las casillas en las esquinas de un estado corresponden a las esquinas de la solución.
    - Si no lo están, suman un valor extra a la heurística de dichas casillas, dándoles una prioridad sobre las demás casillas.
- De esta manera, el algoritmo va guiándose a través de aquellos estados donde las esquinas estén más próximas a estar en su lugar.
- OBSERVACIÓN: se vio que, para la tabla 3 de [[1]](#bibliografía), es mejor sumar 2 a dicha heurística; mientras que para la tabla 4, es mejor sumar 4 (por defecto, se decidió dejarlo sumando 4).
- Hay que tomar en cuenta que, al agregar esta heurística, se reduce el número de estados generados, pero aumenta ligeramente el número de pasos a la solución.

- Así, se sustituyó la heurística Walking Distance por la heurística Corner-Tile, obteniendo la siguiente fórmula HH:
    ```math
      HH = {md(s) \over 3} + lc(s) + ct(s) 
    ```
    
- donde: ct = Corner-Tile

- De esta forma, se redujo el número de estados generados. 

- Sin embargo, esto no fue suficiente, por lo que se probó con asignar pesos a las heurísticas. Esto es, multiplicando las heurísticas por un entero mayor que 1 (asumiendo el riesgo de aumentar aún más la longitud del camino obtenido). 

- Finalmente, se vió que la siguiente fórmula reduce enormemente el número de estados generados, y obtiene soluciones de longitud competitiva respecto a BA*:
  
```math
HH = {4md(s) \over 3} + lc(s) + ct(s) 
```

----------------------------------------------------------------------------------------------------------------------------

## Compilación del programa (teniendo instalado JDK 17 en adelante)

```
>javac Main.java Puzzle.java Heuristica.java IDAStar.java
```


## Ejecución 

```
>java Main
```

## Ejemplo de uso

```
Ingresa una secuencia de 16 numeros (del 0 al 15) separados por espacios:
1 2 3 4 5 6 7 8 9 10 11 12 13 14 0 15

Secuencia de movimientos:
1 2 3 4
5 6 7 8
9 10 11 12
13 14 15 0

Numero de pasos hasta llegar a la meta: 1
Numero de estados generados: 1
```

## Bibliografía

[1] - [The Fifteen Puzzle - A New Approach through Hybridizing Three Heuristics Methods. Hasan, Aladdin, Talabani, Rashid, Mirjalili. 2023](https://www.researchgate.net/publication/366855720_The_Fifteen_Puzzle-A_New_Approach_through_Hybridizing_Three_Heuristics_Methods)

[2] - [Finding Optimal Solutions to the Twenty-Four Puzzle. Korf, Taylor. 1996](https://courses.cs.washington.edu/courses/csep573/10wi/korf96.pdf)
