
<h1 align="center"> Documentación adicional </h1>

## GestionarSolicitudReserva

En este apartado nos gustaría dar información adicional sobre el algoritmo que utilizamos en la clase ***reservaInmediata***, que dados el origen $\bar{o} = ( i, j )$ 
y un radio máximo $n$, genera los vecinos a $\bar{o}$ y los ordena en sentido antihorario, de menor a mayor distacia, en el espacio métrico del taxista en $\mathbb{R}^2$.

### Overview

Para realizar la reserva, el proceso que seguimos en pseudocódigo es:

``` python
Input: radioMax = n, o = [i, j]  
Init: reservado <- False, dist <- 1

while not reservado and dist <= n:
    generarVecinos(radio = dist, origen = [i, j])
    quitarVecinosFueraDeRango()
    ordenarPorPrecio()

    reservado <- intentarReservarEnOrden()
    dist <- dist + 1
```

De este modo, en cada iteración $i$, generamos los vecinos con $dist = i \in \lbrack 1, \dots, n \rbrack$ en orden antihorario.
Tras eliminar aquellos fuera de rango y reordenarlos por precio, intentamos reservar en cada uno de los vecinos ordenadamente.
Si se logra reservar, se sale del bucle; en otro caso se repite el proceso con $dist = i + 1$.
La ejecución finaliza si o bien se logra reservar en cualquiera de las iteraciones, o bien no se logra reservar en ningún vecino con $dist \leq n$.

### Desarrollo del método

La función principal es la siguiente:
```java

    @Override
    public void gestionarSolicitudReserva(GestorLocalidad gestor) {
        if (esValida(gestor)) {
    
            super.gestionarSolicitudReserva(gestor);

            if (super.getHueco() == null) {

                boolean reservado = false;

                for (int dist = 1; dist <= radio && !reservado; dist++) {

                    int[][] coords = new int[dist * 4][2];
                    generarVecinos(coords, dist, super.getIZona(), super.getJZona());

                    ArrayList<int[]> coordsValidas = new ArrayList<int[]>();
                    quitarCoordsFueraDeRango(coordsValidas, coords, gestor);
                    ordenarPorPrecio(coordsValidas, gestor);

                    reservado = intentarReservar(coordsValidas, gestor);
                }
            }
        }
    }

```

Tras comprobar la validez del gestor, si no se consigue reservar hueco en la zona deseada se comienza a buscar en los alrededores. De este modo comenzamos a recorrer las distintas distancias con el bucle principal, creando en cada iteración un *array* bidimensional de tamaño $4·dist \times 2$, que almacene los vecinos a distancia $dist$. A continuación se llama a ***generarVecinos()*** que rellena a **coords** de estos vecinos, colocándolos en orden antihorario.

```java
for (int dist = 1; dist <= radio && !reservado; dist++) {

    int[][] coords = new int[dist * 4][2];
    generarVecinos(coords, dist, super.getIZona(), super.getJZona());

    ...

}

```

En la geometría de Manhattan $dist(\bar{x},\bar{x}') = |x - x'| + |y - y'|$, por lo todos lo que todos los puntos del conjunto $B_n(\bar{o}) = \lbrace (x, y): dist(\bar{o}, \bar{x}) = n\rbrace$ forman un cuadrado en lugar de un círculo. Además, se verifica que el número de puntos o celdas en $B_n(\bar{o})$, será $4·n$, es decir, que el diámetro de un círculo de radio $n$ será $4·n$por lo que el análogo a $\pi$ en está geometría será 2. Es por esto que se elige este tamaño para el array **coords**.\
De ahora en adelante se define $\pi' = 2$.

Adicionalmente nótese que el número de celdas dentro del conjunto $\overline{B_n}(\bar{o}) = \lbrace(x, y): dist(\bar{o}, \bar{x}) \leq n\rbrace$, por lo discutido, será: $$4·1 + 4·2 + \dots + 4·n = 4·\displaystyle\sum_{i=1}^{n} i = 4·\frac{n(n + 1)}{2} = 2n(n + 1) = \pi'n(n + 1)$$

Es conclusión, en el espacio Manhattan un círculo de radio $r$ tiene un diámetro de $2·\pi'·n$, y un área de $\pi'n(n + 1)$.

```java
for (int dist = 1; dist <= radio && !reservado; dist++) {

    ...

    ArrayList<int[]> coordsValidas = new ArrayList<int[]>();
    quitarCoordsFueraDeRango(coordsValidas, coords, gestor);
    ordenarPorPrecio(coordsValidas, gestor);   //BubbleSort

    reservado = intentarReservar(coordsValidas, gestor);
}

```

Después de generar los vecinos, se añaden al *ArrayList* **coordsValidas** aquellas coordenadas que existan en el *GestorLocalidad* **gestor** en el método ***quitarCoordsFueraDeRango*** y se ordenan utilizando un BubbleSort en ***ordenarPorPrecio()***.
Como las coordenadas de **cords** se colocan en el *array* en orden antihorario y se colocan en el *ArrayList* del mismo modo, al ordenarlos en el BubbleSort, como solo se intercambian dos elementos si uno tiene mayor precio que el siguiente, si dos coordenadas tienen el mismo precio se quedarán ordenadas en sentido antihorario.

Finalmente, una vez se tienen todas las coordenadas válidas, se intenta hacer una reserva en cada auna de forma ordenada en ***intentarReserva()*** hasta que se tenga éxito o se fracase con todas las coordenadas con $dist \leq n$, como se comentó previamente.

#### Función generarVecinos

De todas las funciones auxiliares, tan solo se comentará ***generarVecinos()*** ya que el resto son bastante triviales, y no hay mucho contenido que explicar. La función, que a su vez tiene otra función auxiliar, es del siguiente modo:

```java
private void generarVecinos(int[][] coords, int dist, int iZona, int jZona) {

    int[] inicio = {0, -dist};
    int[] vecDir = {1, 1};

    int[] orig = {iZona, jZona};

    for (int offset = 0; offset < dist; offset++) {
        coords[dist*0 + offset] = new int[] {     inicio[0] + orig[0],      inicio[1] + orig[1]};
        coords[dist*1 + offset] = new int[] {-1 * inicio[1] + orig[0],      inicio[0] + orig[1]};
        coords[dist*2 + offset] = new int[] {-1 * inicio[0] + orig[0], -1 * inicio[1] + orig[1]};
        coords[dist*3 + offset] = new int[] {     inicio[1] + orig[0], -1 * inicio[0] + orig[1]};

        sumaVec(inicio, vecDir);
    }
}

private void sumaVec(int[] vecI, int[] vecDir) {
    for (int i = 0; i < vecI.length; i++) {
        vecI[i] += vecDir[i];
    }
}

```

