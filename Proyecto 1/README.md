# Solver del 15-puzzle

Franco Murillo - 1610782  
Alejandro Zambrano - 1710684

- El proyecto fue realizado en **Java**

- El programa asume que la entrada es correcta (esto es, una secuencia de 16 números, cada uno separado por un espacio, del 0 al 15)

- El objetivo es poder computar la solución de las instancias propuestas en el artículo  de manera competitiva con respecto a la solución propuesta por éstos, que fue usar el algoritmo Bidirectional A* (BA*), haciendo uso de la heurística Hybrid Heuristic (HH), la cual es:  $x /3$

    $ HH = (md(s)/3) + wd(s) + lc(s) $, donde:

    md = Manhattan Distance; wd = Walking Distance; lc = Linear Conflict

- El algoritmo escogido para el proyecto fue Iterative Deepening A* (IDA*), la cual establece un límite heurístico inicial (la heurística calculada sobre el estado inicial), y en cada iteración se realiza un recorrido DFS sobre los estados, actualizando el límite heurístico al final de cada iteración, hasta llegar al estado meta.

- La función verifica si se llegó a la solución si al hacer la llamada recursiva dfs retorna -1 

- OBSERVACIÓN: el código de la solución fue hecho considerando el estado solución como todos los elementos ordenados de 1 a 15, con el último espacio ocupado con el espacio en blanco. Por esto, no funcionará para la tabla 3 del artículo de Hassan, pues dicha tabla de casos está hecha considerando la solución con el espacio en blanco al principio. 

- Así, el código funciona correctamente para las otras dos tablas del artículo [1], con la observación de que en la segunda tabla, los primeros 11 casos están hechos considerando la solución con el espacio en blanco al principio, por lo que debe ser probado a partir del caso 12 hasta el 28.

>[!NOTE]
> En el archivo "pruebas.txt" se encontrarán todos los casos de prueba que se sacaron de [1], así como los casos usados para la evaluación del proyecto.
 
- La idea original era la de usar la propia heurística HH sobre el algoritmo IDA*, sin embargo, se vió que, si bien se obtenía soluciones de longitud óptima, la generación de estados y el tiempo de obtención de respuesta no eran óptimos, tardando hasta varios minutos en computar una solución. 

- Por esto, se investigó sobre distintas heurísticas, hasta que se encontró con la heurística Corner-Tile en el artículo [2] que habla sobre el 24-puzzle, la cual considera lo siguiente:

    - En el problema del n-puzzle, las piezas más complicadas de ubicar en la solución son las esquinas.
    - Luego, la heurística Corner-Tile verifica si las casillas en las esquinas de un estado corresponden a las esquinas de la solución.
    - Si no lo están, suman un valor extra a la heurística de dichas casillas, dándoles una prioridad sobre las demás casillas.
    - De esta manera, el algoritmo va guiándose a través de aquellos estados donde las esquinas estén más próximas a estar en su lugar.
    - Se encontró que, para la primera tabla de [1], es mejor sumar 2 a la heurística; mientras que para la segunda tabla, es mejor sumar 4.
    - Hay que tomar en cuenta que, al agregar esta heurística, se reduce el número de estados generados, pero aumenta ligeramente el número de pasos a la solución.

- Así, se sustituyó la heurística Walking Distance por la heurística Corner-Tile, quedando la fórmula HH de la siguiente manera:
    
    - HH = (md(s)/3) + lc(s) + ct(s), donde: ct = Corner-Tile

- De esta forma, se redujo bastante el número de estados generados. 

- Sin embargo, no fue suficiente, por lo que se probó con asignar pesos a las heurísticas, esto es, multiplicando las heurísticas por un entero mayor que 1 (sabiendo el riesgo de alargar aún más la longitud del camino obtenido). 

- Se vió que la fórmula HH = 4*(md(s)/3) + lc(s) + ct(s) reduce enormemente el número de estados generados, y obtiene soluciones de longitud competitiva respecto a BA*.


## Compilación del programa (teniendo instalado JDK 17 en adelante)

```
>javac Main.java Puzzle.java Heuristica.java IDAStar.java
```


## Ejecución 

```
>java Puzzle15
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

[1] - The Fifteen Puzzle - A New Approach through Hybridizing Three Heuristics Methods. Hassan, Talabani, Mirjalili. 2023

[2] - Finding Optimal Solutions to the Twenty-Four Puzzle. Korf, Taylor. 1996
