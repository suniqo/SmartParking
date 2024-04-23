
package modelo.reservas.solicitudesreservas;


import java.time.LocalDateTime;

import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.vehiculos.Vehiculo;

public class SolicitudReservaAnticipada extends SolicitudReserva {

    public SolicitudReservaAnticipada(int i, int j, LocalDateTime tI, LocalDateTime tF, Vehiculo vehiculo) {
        super(i, j, tI, tF, vehiculo);
    }

    @Override
    public void gestionarSolicitudReserva(GestorLocalidad gestor) {
        if (super.esValida(gestor)) {

            super.gestionarSolicitudReserva(gestor);

            if (super.getHueco() == null) {
                GestorZona gestorZona = super.getGestorZona();
                gestorZona.meterEnListaEspera(this);
            }
        }
    }
}
