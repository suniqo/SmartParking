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
                int iZona = super.getIZona();
                int jZona = super.getJZona();

                boolean reservado = false;

                for (int dist = 1; dist <= radio && !reservado; dist++) {

                    int[][] coords = new int[dist * 4][2];
                    generarVecinos(coords, dist, iZona, jZona);

                    ArrayList<int[]> coordsValidas = new ArrayList<int[]>();
                    quitarCoordsFueraDeRango(coordsValidas, coords, gestor);
                    ordenarPorPrecio(coordsValidas, gestor);

                    reservado = intentarReservar(coordsValidas, gestor);
                }
            }
        }
    }


    /*** Métodos Auxiliares ***/

    private void generarVecinos(int[][] coords, int dist, int iZona, int jZona) {
        
        int[] orig = {iZona, jZona};
        int[] inicio = {0, -dist};
        int[] vecDir = {1, 1};
        
        for (int offset = 0; offset < dist; offset++) {
            coords[dist*0 + offset] = new int[] {     inicio[0],      inicio[1]};
            coords[dist*1 + offset] = new int[] {-1 * inicio[1],      inicio[0]};
            coords[dist*2 + offset] = new int[] {-1 * inicio[0], -1 * inicio[1]};
            coords[dist*3 + offset] = new int[] {     inicio[1], -1 * inicio[0]};

            sumVec(inicio, vecDir);
        }

        for (int[] coord : coords) {
            sumVec(coord, orig);
        }
    }

    private void sumVec(int[] vecI, int[] vecDir) {
        for (int i = 0; i < vecI.length; i++) {
            vecI[i] += vecDir[i];
        }
    }

    private void quitarCoordsFueraDeRango(ArrayList<int[]> coordsValidas, int[][] coords, GestorLocalidad gestor) {
        for (int[] coord: coords) {
            if (gestor.existeZona(coord[0], coord[1])) {
                coordsValidas.add(coordsValidas.size(), coord);
            }
        }
    }

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
