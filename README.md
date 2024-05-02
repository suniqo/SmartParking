# Documentación adicional

## Generaración de vecinos, espacio Manhattan

En este apartado nos gustaría dar información adicional sobre el algoritmo que utilizamos en la clase ***reservaInmediata***, que dados el origen $\bar{o} = ( i, j )$ 
y un radio máximo $n$, genera los vecinos a $\bar{o}$ y los ordena en sentido antihorario, de menor a mayor distacia, en el espacio métrico del taxista de $\mathbb{R}^2$.

### Overview

Para realizar la reserva, el proceso que seguimos es:

``` python
Input: radioMax, o = [i, j]  
Init: reservado = False, dist = 1

while not reservado and dist < n:
    generarVecinos(radio = dist, origen = o)
    quitarVecinosFueraDeRango()
    ordenarPorPrecio()

    reservado = intentarReservarEnOrden()
    dist++
```
> **while** not reservado
> $dist \in \lbrack 1, \dots, n \rbrack :$
>
> - generarVecinos(radio = $dist$)
> - quitarVecinosFueraDeRango()
>
> &nbsp;&nbsp;&nbsp;&nbsp;**if** reservaExitosa():
> - break


### Funcionamiento

### Implementación
