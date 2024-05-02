# Documentación adicional

## Generaración de vecinos, espacio Manhattan

En este apartado nos gustaría dar información adicional sobre el algoritmo que utilizamos en la clase ***reservaInmediata***, que dados el origen $\bar{o} = ( i, j )$ 
y un radio máximo $n$, genera los vecinos a $\bar{o}$ y los ordena en sentido antihorario, de menor a mayor distacia, en el espacio métrico del taxista de $\mathbb{R}^2$.

### Overview

Para realizar la reserva, el proceso que seguimos es:

**for** $dist \in {1, \dots, n}:$
    generarVecinos($radio = dist$)
    quitarVecinosFueraDeRango()
    ordenarPorPrecio()
    intentarReservarEnOrden()
    **if** reservaExitosa:
        break
    **else**
        continue  


### Funcionamiento

### Implementación
