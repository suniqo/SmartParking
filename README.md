# Documentación adicional

## Generaración de vecinos, espacio Manhattan

En este apartado nos gustaría dar información adicional sobre el algoritmo que utilizamos en la clase ***reservaInmediata***, que dados el origen $\bar{o} = ( i, j )$ 
y un radio máximo $n$, genera los vecinos a $\bar{o}$ y los ordena en sentido antihorario, de menor a mayor distacia, en el espacio métrico del taxista de $\mathbb{R}^2$.

### Overview

Para realizar la reserva, el proceso que seguimos es:

``` python
Input: radioMax = n, o = [i, j]  
Init: reservado = False, dist = 1

while not reservado and dist < n:
    generarVecinos(radio = dist, origen = [i, j])
    quitarVecinosFueraDeRango()
    ordenarPorPrecio()

    reservado = intentarReservarEnOrden()
    dist++
```

De este modo, en cada iteración $i$, generamos los vecinos con $dist = i \in \lbrack 1, \dots, n \rbrack$ en orden antihorario.
Tras eliminar aquellos fuera de rango y reordenarlos por precio, intentamos reservar en cada uno de los vecinos ordenadamente.
Si se logra reservar, se sale del bucle; en otro caso se repite el proceso con $dist = i + 1$.
La ejecución finaliza si o bien se logra reservar en cualquiera de las iteraciones, o bien si no se logra reservar en ningún vecino con $dist <= n$.

> **while** not reservado
> 
>
> - generarVecinos(radio = $dist$)
> - quitarVecinosFueraDeRango()
>
> &nbsp;&nbsp;&nbsp;&nbsp;**if** reservaExitosa():
> - break


### Funcionamiento

### Implementación
