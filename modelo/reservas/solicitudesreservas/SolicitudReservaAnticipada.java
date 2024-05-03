
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

    /**
     * Constructor de SolicitudReservaAnticipada
     * @param i Componente i de la zona dónde se desea reservar
     * @param j Componente j de la zona dónde se desea reservar
     * @param tI Comienzo de la solicitud de reserva
     * @param tF Fin de la solicitud de reserva
     * @param vehiculo Vehículo con el que se desea aparcar
     */
    public SolicitudReservaAnticipada(int i, int j, LocalDateTime tI, LocalDateTime tF, Vehiculo vehiculo) {
        super(i, j, tI, tF, vehiculo);
    }

    /**
     * Se intenta gestionar la solicitud en la zona deseada, si falla se añade la solicitud a la lista de espera
     * @param gestor GestorLocalidad dónde se desea realizar la reserva
     */
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
