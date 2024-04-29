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
    
    @Override
    public void gestionarSolicitudReserva(GestorLocalidad gestor) {
        if (esValida(gestor)) {
    
            super.gestionarSolicitudReserva(gestor);

            if (super.getHueco() == null) {
                int iZona = super.getIZona();
                int jZona = super.getJZona();

                boolean reservado = false;

                for (int dist = 1; dist <= radio && !reservado; dist++) {
                    ArrayList<int[]> coords = new ArrayList<int[]>();

                    generarVecinos(coords, dist, iZona, jZona);
                    quitarCoordsFueraDeRango(coords, gestor);
                    ordenarPorPrecio(coords, gestor);

                    Hueco hueco;

                    for (int i = 0; i < coords.size() && !reservado; i++) {
                        int[] coord = coords.get(i);
                        GestorZona gestorZona = gestor.getGestorZona(coord[0], coord[1]);

                        if ((hueco = gestorZona.reservarHueco(super.getTInicial(), super.getTFinal())) != null) {
                            super.setGestorZona(gestorZona);
                            super.setHueco(hueco);

                            reservado = true;
                        }
                    }
                }
            }
        }
    }

    private void generarVecinos(ArrayList<int[]> coords, int dist, int iZona, int jZona) {
        int coordI = 0;
        int coordJ = -dist;

        for (int offset = 0; offset < dist ; offset++) {
            
            coords.add(0 * dist + offset, new int[] {coordI + iZona, coordJ + jZona});
            coords.add(1 * dist + offset, new int[] {-coordJ + iZona, coordI + jZona});
            coords.add(2 * dist + offset, new int[] {-coordI + iZona, -coordJ + jZona});
            coords.add(3 * dist + offset, new int[] {coordJ + iZona, -coordI + jZona});

            coordI++;
            coordJ++;
        }
    }

    private void quitarCoordsFueraDeRango(ArrayList<int[]> coords, GestorLocalidad gestor) {
        for (int i = 0; i < coords.size(); i++) {
            int[] coord = coords.get(i);
            if (!gestor.existeZona(coord[0], coord[1])) {
                coords.remove(coord);
            }
        }
    }

    private void ordenarPorPrecio(ArrayList<int[]> coords, GestorLocalidad gestor) {
        int longitud = coords.size();

        for (int i = 0; i < longitud - 1; i++) {

            boolean ordenado = false;
            for (int j = 0; j < longitud - i - 1; j++) {
                int[] coord1 = coords.get(j);
                int[] coord2 = coords.get(j + 1);

                GestorZona gestorZona1 = gestor.getGestorZona(coord1[0], coord1[1]);
                GestorZona gestorZona2 = gestor.getGestorZona(coord2[0], coord2[1]);

                if (gestorZona1.getPrecio() > gestorZona2.getPrecio()) {
                    int[] aux = {coord1[0], coord1[1]};
                    coords.set(j, coord2);
                    coords.set(j + 1, aux);

                    ordenado = true;
                }
            }

            if (ordenado) break; // TODO preguntar es ineficiente?
        }
    }
}
