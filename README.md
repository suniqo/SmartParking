
<h1 align="center"> Documentación adicional </h1>

## GestionarSolicitudReserva

En este apartado nos gustaría dar información adicional sobre el algoritmo que utilizamos en la clase ***reservaInmediata***, que dados el origen $\bar{o} = ( i, j )$ 
y un radio máximo $n$, genera los vecinos a $\bar{o}$ y los ordena en sentido antihorario, de menor a mayor distacia, en el espacio métrico del taxista de $\mathbb{R}^2$.

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
La ejecución finaliza si o bien se logra reservar en cualquiera de las iteraciones, o bien no se logra reservar en ningún vecino con $dist <= n$.

### Funcionamiento

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

Tras comprobar la validez del gestor, si no se consigue reservar hueco en la zona deseada, se comienza a buscar en los alrededores. De este modo comenzamos a recorrer las distintas distancias con el bucle principal, creando en cada iteración un array bidimensional de tamaño $4·dist \times 2$, que almacene los vecinos a distancia $dist$.

```java
for (int dist = 1; dist <= radio && !reservado; dist++) {

    int[][] coords = new int[dist * 4][2];
    generarVecinos(coords, dist, super.getIZona(), super.getJZona());

    ...

}

```

En la geometría de Manhattan $dist(\bar{x},\bar{x}') = |x - x'| + |y - y'|$, por lo todos lo que todos los puntos del conjunto $B_n(\bar{o}) = \{ (x, y): dist(\bar{o}, \bar{x}) = n \} $ forman un cuadrado en lugar de un círculo. Además, se verifica que el número de puntos o celdas en $B_n(\bar{o})$ será $4·n$.\
Es por esto que se elige este tamaño para el array *coords*.

Adicionalmente nótese que el número de celdas dentro del conjunto $\overline{B_n}(\bar{o}) = \{ (x, y): dist(\bar{o}, \bar{x}) <= n \} $, por lo discutido, será: $$4·1 + 4·2 + \dots + 4·n = 4·\displaystyle\sum_{i=1}^{n} i = 4·\frac{n(n + 1)}{2} = 2·n·(n + 1) = 2n^2 + 2n$$



### Implementación
