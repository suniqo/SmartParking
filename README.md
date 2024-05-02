
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

Tras comprobar la validez del gestor, si no se consigue reservar hueco en la zona deseada, se comienza a buscar en los alrededores. De este modo comenzamos a recorrer las distintas distancias con el bucle principal:

```java
                for (int dist = 1; dist <= radio && !reservado; dist++) {
                    ...
                }

```




### Implementación
