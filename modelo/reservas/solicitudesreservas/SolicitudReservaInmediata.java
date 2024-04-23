package modelo.reservas.solicitudesreservas;

import java.time.LocalDateTime;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.vehiculos.Vehiculo;

public class SolicitudReservaInmediata extends SolicitudReserva {
    
    private int radio;

    public SolicitudReservaInmediata(int i, int j, LocalDateTime tI, LocalDateTime tF, Vehiculo vehiculo, int radio) {
        super(i, j, tI, tF, vehiculo);
        this.radio = radio;
    }
    
    private int maximo(int x, int y) {
        return x > y ? x : y;
    }

    @Override
    public boolean esValida(GestorLocalidad gestor) {
        if(radio > 0 && super.esValida(gestor)) {
            int iZona = super.getIZona();
            int jZona = super.getJZona();
            int distMax = maximo(iZona, gestor.getRadioMaxI() - iZona) + maximo(jZona, gestor.getRadioMaxJ() - jZona);

            return radio <= distMax;
        }
        return false;
    }
    
    // TODO muy ineficiente!
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

                    generarVecinos(coords, dist);
                    ordenarPorPrecio(coords, gestor, iZona, jZona);

                    for (int i = 0; i < coords.length && !reservado; i++) {
                        int[] coord = coords[i];
                        if (gestor.existeZona(coord[0], coord[1])) {
                            GestorZona gestorZona = gestor.getGestorZona(coord[0] + iZona, coord[1] + jZona);
                            
                            if (gestorZona.reservarHueco(super.getTInicial(), super.getTFinal()) != null)
                                reservado = true;
                        }
                    }
                }
            }
        }
    }

    private void generarVecinos(int[][] coords, int dist) {
        int coordI = 0;
        int coordJ = -dist;

        for (int offset = 0; offset < dist ; offset++) {
            coords[0 * dist + offset] = new int[] {coordI, coordJ};
            coords[1 * dist + offset] = new int[] {-coordJ, coordI};
            coords[2 * dist + offset] = new int[] {-coordI, -coordJ};
            coords[3 * dist + offset] = new int[] {coordJ, -coordI};

            coordI++;
            coordJ++;
        }
    }

    private void ordenarPorPrecio(int[][] coords, GestorLocalidad gestor, int iZona, int jZona) {
        int longitud = coords.length;

        for (int i = 0; i < longitud - 1; i++) {

            boolean ordenado = true;
            for (int j = 0; j < longitud - i - 1; j++) {
                int[] coord1 = coords[j];
                int[] coord2 = coords[j + 1];

                GestorZona gestorZona1 = gestor.getGestorZona(coord1[0] + iZona, coord1[1] + jZona);
                GestorZona gestorZona2 = gestor.getGestorZona(coord2[0] + iZona, coord2[1] + jZona);

                if (gestorZona1.getPrecio() < gestorZona2.getPrecio()) {
                    int aux1 = coord1[0];  
                    int aux2 = coord2[1];  

                    coords[j] = coords[j + 1];
                    coords[j + 1] = new int[] {aux1, aux2};

                    ordenado = false;
                }
            }

            if (ordenado) break;
        }
    }
}
