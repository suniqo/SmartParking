package modelo.reservas.solicitudesreservas;

import java.time.LocalDateTime;

import anotacion.Programacion2;
import list.ArrayList;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.vehiculos.Vehiculo;

@Programacion2 (
		nombreAutor1 = "Andrés",
		apellidoAutor1 = "Súnico Sánchez",
		emailUPMAutor1 = "andres.sunico",
		nombreAutor2 = "Samuel",
		apellidoAutor2 = "Álvarez Salido",
		emailUPMAutor2 = "samuel.alvarez"
		)

public class SolicitudReservaInmediata extends SolicitudReserva {
    
    private int radio;

    /**
     * Constructor de SolicitudReservaInmediata
     * @param i Componente i de la zona dónde se desea reservar
     * @param j Componente j de la zona dónde se desea reservar
     * @param tI Comienzo de la solicitud de reserva
     * @param tF Fin de la solicitud de reserva
     * @param vehiculo Vehículo con el que se desea aparcar
     * @param radio Radio máximo en el que se desea comprobar zonas, en caso de fallo al reservar
     */
    public SolicitudReservaInmediata(int i, int j, LocalDateTime tI, LocalDateTime tF, Vehiculo vehiculo, int radio) {
        super(i, j, tI, tF, vehiculo);
        this.radio = radio;
    }
    
    /**
     * Indica si la solicitud de reserva es válida en el gestor de localidad provisto
     * @param gestor GestorLocalidad dónde se desea comprobar la validez de la reserva
     * @return Si el padre valida la solicitud, true si el radio es menor que la distancia máxima a una zona de la localidad, e.o.c. false.
     */
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
    
    /**
     * Método auxiliar para calcular el máximo de dos números
     * @param x Primer dígito a comprobar
     * @param y Segundo dígito a comprobar
     * @return Máximo entre x e y
     */
    private int max(int x, int y) {
        return x > y ? x : y;
    }

    /**
     * Se intenta gestionar la solicitud en la zona deseada, si falla se busca en las zonas a distancia menor o igual al atributo radio,
     * comprobando primero las mas cercanas, en orden antihorario y de menor a mayor precio
     * @param gestor GestorLocalidad dónde se desea realizar la reserva
     */
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
                    anadirCoordsValidas(coordsValidas, coords, gestor);
                    ordenarPorPrecio(coordsValidas, gestor);

                    reservado = intentarReservar(coordsValidas, gestor);
                }
            }
        }
    }


    /** 
     * Se almacenan en el array coords las coordenadas de todos los puntos a la distancia dada, en orden antihorario
     * @param coords Array donde se almacenan las coordenadas
     * @param dist Distancia en la cual se quieren generar los puntos
     * @param iZona Componente i del centro donde se comienza a buscar
     * @param jZona Componente j del centro donde se comienza a buscar
     */
    private void generarVecinos(int[][] coords, int dist, int iZona, int jZona) {
        
        int[] inicio = {0, -dist};
        int[] vecDir = {1, 1};

        int[] orig = {iZona, jZona};
        
        for (int offset = 0; offset < dist; offset++) {
            coords[dist*0 + offset] = new int[] {     inicio[0] + orig[0],      inicio[1] + orig[1]};
            coords[dist*1 + offset] = new int[] {-1 * inicio[1] + orig[0],      inicio[0] + orig[1]};
            coords[dist*2 + offset] = new int[] {-1 * inicio[0] + orig[0], -1 * inicio[1] + orig[1]};
            coords[dist*3 + offset] = new int[] {     inicio[1] + orig[0], -1 * inicio[0] + orig[1]};

            inicio[0] += vecDir[0];
            inicio[1] += vecDir[1];
        }
    }

    /** 
     * Se añaden a un ArrayList las coords válidas, ya que no se conoce de antemano cuantas válidas habrán
     * @param coordsValidas ArrayList donde se almacenan las coordenadas válidas, respetando el orden antihorario
     * @param coords Array con todas las coordenadas, válidas o no, a una determinada distancia en orden antihorario
     * @param gestor GestorLocalidad donde se comprueba la validez de las coordenadas
     */
    private void anadirCoordsValidas(ArrayList<int[]> coordsValidas, int[][] coords, GestorLocalidad gestor) {
        for (int[] coord: coords) {
            if (gestor.existeZona(coord[0], coord[1])) {
                coordsValidas.add(coordsValidas.size(), coord);
            }
        }
    }

    /**
     * BubbleSort para ordenar las coordenadas válidas de menor a mayor precio
     * (en caso de empate no hay intercambios, respetando el orden antihorario)
     * @param coordsValidas ArrayList de coordenadas válidas que se desea reordenar
     * @param gestor GestorLocalidad dónde se desea consultar el precio de cada coordenada válida
     */
    private void ordenarPorPrecio(ArrayList<int[]> coordsValidas, GestorLocalidad gestor) {
        int length = coordsValidas.size();

        boolean ordenado = false;
        for (int i = 0; i < length - 1 && !ordenado; i++) {

            ordenado = true;
            for (int j = 0; j < length - i - 1; j++) {
                int[] coord1 = coordsValidas.get(j);
                int[] coord2 = coordsValidas.get(j + 1);

                GestorZona gestorZona1 = gestor.getGestorZona(coord1[0], coord1[1]);
                GestorZona gestorZona2 = gestor.getGestorZona(coord2[0], coord2[1]);

                if (gestorZona1.getPrecio() > gestorZona2.getPrecio()) {
                    int[] aux = {coord1[0], coord1[1]};
                    coordsValidas.set(j, coord2);
                    coordsValidas.set(j + 1, aux);

                    ordenado = false;
                }
            }
        }
    }

    /**
     * Se intenta realizar las reservas de las coordenadas válidas ordenadamente, en caso de lograrlo se sale del bucle
     * @param coordsValidas ArrayList con las coordenadas válidas ordenadas correctamente
     * @param gestor GestorLocalidad dónde se desea intentar reservar en las zonas almacenadas en el ArrayList
     * @return True si se logra reservar en alguna de las coordenadas válidas, e.o.c false
     */
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
