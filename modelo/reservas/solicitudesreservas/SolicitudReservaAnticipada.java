
package modelo.reservas.solicitudesreservas;


import java.time.LocalDateTime;

import anotacion.Programacion2;
import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.GestorZona;
import modelo.vehiculos.Vehiculo;

@Programacion2 (
		nombreAutor1 = "Andrés",
		apellidoAutor1 = "Súnico Sánchez",
		emailUPMAutor1 = "andres.sunico",
		nombreAutor2 = "Samuel",
		apellidoAutor2 = "Álvarez Salido",
		emailUPMAutor2 = "samuel.alvarez"
		)

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
