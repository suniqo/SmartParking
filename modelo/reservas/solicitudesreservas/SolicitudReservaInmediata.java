package modelo.reservas.solicitudesreservas;

import java.time.LocalDateTime;
import list.ArrayList;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.vehiculos.Vehiculo;

public class SolicitudReservaInmediata extends SolicitudReserva {
    
    private int radio;

    public SolicitudReservaInmediata(int i, int j, LocalDateTime tI, LocalDateTime tF, Vehiculo vehiculo, int radio) {
        super(i, j, tI, tF, vehiculo);
        this.radio = radio;
    }
    
    private int max(int x, int y) {
        return x > y ? x : y;
    }

    @Override
    public boolean esValida(GestorLocalidad gestor) {
        if(radio > 0 && super.esValida(gestor)) {
            int iZona = super.getIZona();
            int jZona = super.getJZona();
            int distMax = max(iZona, gestor.getRadioMaxI() - iZona) + max(jZona, gestor.getRadioMaxJ() - jZona);

            return radio <= distMax;
        }
        return false;
    }
    
    @Override
    public void gestionarSolicitudReserva(GestorLocalidad gestor) {
        if (esValida(gestor)) {
    
            super.gestionarSolicitudReserva(gestor);

            if (super.getHueco() == null) {

                boolean reservado = false;

                for (int dist = 1; dist <= radio && !reservado; dist++) {

                    // En cada iteración se generan los vecinos a la distacia determinada por el bucle,
                    // y se añaden a un array en orden antihorario, cada una almacenada en forma {i, j}.
                    // El tamaño es dist * 4, ya que a distancia 1 hay 4 celdas, a 2: 8, a 3: 12, etc.
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


    /*** Métodos Auxiliares ***/

    // Para generar los vecinos a una distacia dada en orden antihoarario, aprovechamos
    // la simetría de las coordenadas cuando el centro es el origen {0, 0}.
    // De este modo, sea n la distancia dada, si conocemos una coordenada
    // en la posición i < n, podemos determinar las que se encuentran en las
    // posiciones n + i, 2*n + i y 3*n + i.
    // Por tanto en este algoritmo recorremos tan solo un lado de los cuatro del cuadrado
    // que forman las 4*n celdas a una distacia n, comenzando en la esquina izquierda
    // y avanzando según la dirección de vecDir, hasta recorrer n celdas (1/4 de todas),
    // y con ellas generamos el resto de forma que estén en orden antihorario.
    // Una vez se tienen los vecinos con origen enn el {0, 0}, se trasladan sumando {iZona, jZona}.
    // Luego, sean n el número de coordenadas a una distacia dada, nuestro algoritmo
    // tiene una complejidad O(n), es decir, tiene una complejidad lineal.
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

    // Se añaden a un ArrayList las coords válidas ya que no se conoce de antemano cuantas habrá que eliminar.
    private void quitarCoordsFueraDeRango(ArrayList<int[]> coordsValidas, int[][] coords, GestorLocalidad gestor) {
        for (int[] coord: coords) {
            if (gestor.existeZona(coord[0], coord[1])) {
                coordsValidas.add(coordsValidas.size(), coord);
            }
        }
    }

    // Utilizamos un BubbleSort simple para ordenar las coordenadas válidas del ArrayList
    // según el precio de su gestor de zona correspondiente.
    // Como las coordenadas ya están en orden antihorario, si dos tienen el mismo precio
    // quedarán ordenadas de forma correcta, ya que el algoritmo no alterará su orden.
    private void ordenarPorPrecio(ArrayList<int[]> coords, GestorLocalidad gestor) {
        int length = coords.size();

        boolean ordenado = false;
        for (int i = 0; i < length - 1 && !ordenado; i++) {

            ordenado = true;
            for (int j = 0; j < length - i - 1; j++) {
                int[] coord1 = coords.get(j);
                int[] coord2 = coords.get(j + 1);

                GestorZona gestorZona1 = gestor.getGestorZona(coord1[0], coord1[1]);
                GestorZona gestorZona2 = gestor.getGestorZona(coord2[0], coord2[1]);

                if (gestorZona1.getPrecio() > gestorZona2.getPrecio()) {
                    int[] aux = {coord1[0], coord1[1]};
                    coords.set(j, coord2);
                    coords.set(j + 1, aux);

                    ordenado = false;
                }
            }
        }
    }

    //Si en algún momento se logra reservar, se escapa del bucle ya que el proceso ha finalizado.
    private boolean intentarReservar(ArrayList<int[]> coordsValidas, GestorLocalidad gestor) {

        Hueco hueco;
        boolean reservado = false;

        for (int i = 0; i < coordsValidas.size() && !reservado; i++) {
            int[] coord = coordsValidas.get(i);
            GestorZona gestorZona = gestor.getGestorZona(coord[0], coord[1]);

            if ((hueco = gestorZona.reservarHueco(super.getTInicial(), super.getTFinal())) != null) {
                super.setGestorZona(gestorZona);
                super.setHueco(hueco);

                reservado = true;
            }
        }
        return reservado;
    }
}
