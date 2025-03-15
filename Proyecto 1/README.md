# Solver del 15-puzzle

Franco Murillo - 1610782  
Alejandro Zambrano - 1710684

- El proyecto fue realizado en **Java**.

- Se modularizó el código con respecto al código entregado, para una mejor comprehensión y una mayor facilidad para su implementación.

- El programa pide al usuario ingresar una secuencia de 16 números, del 0 al 15, separadas por un espacio. Se asume que la secuencia ingresada es correcta.

- El objetivo es poder computar la solución de las instancias propuestas en el artículo [[1]](#bibliografía) de manera competitiva con respecto a la solución propuesta en ésta, la cual fue usar el algoritmo Bidirectional A* (__BA*__), haciendo uso de la heurística Hybrid Heuristic (HH):  

```math
    HH = {md(s) \over 3} + wd(s) + lc(s)   
```
- donde:  md = Manhattan Distance; wd = Walking Distance; lc = Linear Conflict

- El algoritmo escogido para el proyecto fue Iterative Deepening A* (__IDA*__), la cual establece un límite heurístico al principio (la heurística calculada sobre el estado inicial), y en cada iteración se realiza un recorrido DFS sobre los estados, actualizando el límite heurístico, hasta llegar al estado meta.

- La función verifica si se llegó a la solución, si al hacer la llamada recursiva que usa DFS, se obtiene como retorno -1 

- OBSERVACIÓN: el código fue hecho considerando la solución como todos los números ordenados de 1 a 15, y con el el espacio en blanco al final (abajo y a la derecha). Por esto, no funcionará para la tabla 6 de [[1]](#bibliografía), pues dicha tabla de casos considera la solución con el espacio en blanco al principio (arriba y a la izquierda). 

- Así, el código funciona correctamente para las  tablas 3 y 4 de [[1]](#bibliografía), con la observación de que en la tabla 4, los primeros 11 casos están hechos considerando la solución con el espacio en blanco al principio, por lo que debe ser probado a partir del caso 12 hasta el 28.

>[!NOTE]
> En el archivo "pruebas.txt" se encontrarán todos los casos de prueba que se sacaron de [1], así como los casos usados para la evaluación del proyecto.
 
- La idea original era la de usar la propia heurística HH propuesta en [[1]](#bibliografía) sobre el algoritmo IDA*; sin embargo, se vió que, si bien se obtenía soluciones de longitud óptima, el número de estados generados y el tiempo de respuesta no eran óptimos, generando un número enorme de estados, y tardando hasta varios minutos en computar una solución. 

- Por esto, se investigó sobre distintas heurísticas, hasta que se encontró la heurística Corner-Tile en el artículo [[2]](#bibliografía), que habla sobre el 24-puzzle, y que considera lo siguiente:

    - En el problema del n-puzzle, las piezas más complicadas de ubicar en la solución son las esquinas.
    - Luego, la heurística Corner-Tile verifica si las casillas en las esquinas de un estado corresponden a las esquinas de la solución.
    - Si no lo están, suman un valor extra a la heurística de dichas casillas, dándoles una prioridad sobre las demás casillas.
- De esta manera, el algoritmo va guiándose a través de aquellos estados donde las esquinas estén más próximas a estar en su lugar.
- Se encontró que, para la tabla 3 de [[1]](#bibliografía), es mejor sumar 2 a la heurística; mientras que para la segunda tabla, es mejor sumar 4. Por defecto, se decidió dejarlo sumando 4.
- Hay que tomar en cuenta que, al agregar esta heurística, se reduce el número de estados generados, pero aumenta ligeramente el número de pasos a la solución.

- Así, se sustituyó la heurística Walking Distance por la heurística Corner-Tile, quedando la fórmula HH de la siguiente manera:
    ```math
      HH = {md(s) \over 3} + lc(s) + ct(s) 
    ```
    
- donde: ct = Corner-Tile

- De esta forma, se redujo el número de estados generados. 

- Sin embargo, esto no fue suficiente, por lo que se probó con asignar pesos a las heurísticas, esto es, multiplicando las heurísticas por un entero mayor que 1 (sabiendo el riesgo de aumentar aún más la longitud del camino obtenido). 

- Se vió que la siguiente fórmula reduce enormemente el número de estados generados, y obtiene soluciones de longitud competitiva respecto a BA*:
  
```math
HH = {4md(s) \over 3} + lc(s) + ct(s) 
```

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
