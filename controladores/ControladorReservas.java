package controladores;

import controladores.excepciones.PlazaOcupada;
import controladores.excepciones.ReservaInvalida;
import controladores.excepciones.SolicitudReservaInvalida;
import list.IList;
import list.ArrayList;
import modelo.gestoresplazas.GestorLocalidad;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.EstadoValidez;
import modelo.reservas.Reserva;
import modelo.reservas.Reservas;
import modelo.reservas.solicitudesreservas.SolicitudReserva;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;
import modelo.vehiculos.Vehiculo;

import anotacion.Programacion2;

@Programacion2 (
		nombreAutor1 = "Andrés",
		apellidoAutor1 = "Súnico Sánchez",
		emailUPMAutor1 = "andres.sunico",
		nombreAutor2 = "Samuel",
		apellidoAutor2 = "Álvarez Salido",
		emailUPMAutor2 = "samuel.alvarez"

		)

public class ControladorReservas {
	private Reservas registroReservas;
	private GestorLocalidad gestorLocalidad;

	public GestorLocalidad getGestorLocalidad() {
		return gestorLocalidad;
	}

	public Reservas getRegistroReservas() {
		return registroReservas;
	}

	public boolean esValidaReserva(int i, int j, int numPlaza, int numReserva, String noMatricula) {
		Reserva reserva = this.registroReservas.obtenerReserva(numReserva);
		if (reserva == null)
			return false;
		reserva.validar(i, j, numPlaza, noMatricula, gestorLocalidad);
		return reserva.getEstadoValidez() == EstadoValidez.OK;
	}

	//TO-DO alumno obligatorio

	public ControladorReservas(int[][] plazas, double[][] precios) {
        gestorLocalidad = new GestorLocalidad(plazas, precios);
        registroReservas = new Reservas();
	}


	//PRE: la solicitud es válida
	public int hacerReserva(SolicitudReserva solicitud) throws SolicitudReservaInvalida {
        if (!solicitud.esValida(gestorLocalidad)) {
            throw new SolicitudReservaInvalida("Solicitud inválida, o bien la zona seleccionada no existe en la localidad, " + 
                    "el tiempo inicial excede al tiempo final o su vehiculo está sancionado.");
        }
        solicitud.gestionarSolicitudReserva(gestorLocalidad);
        return solicitud.getHueco() == null ? -1 : registroReservas.registrarReserva(solicitud);
	}

	public Reserva getReserva(int numReserva) {
		return registroReservas.obtenerReserva(numReserva);
	}

	//PRE: la plaza dada está libre y la reserva está validada
	public void ocuparPlaza(int i, int j, int numPlaza, int numReserva, Vehiculo vehiculo) throws PlazaOcupada, ReservaInvalida {
        Reserva reserva = registroReservas.obtenerReserva(numReserva);
        Hueco hueco = reserva.getHueco();
        if (hueco.getPlaza().getVehiculo() != null) {
            throw new PlazaOcupada("La plaza ya está ocupada");
        }
        if (!esValidaReserva(i, j, numPlaza, numReserva, vehiculo.getMatricula())) {
            throw new ReservaInvalida("La reserva con número " + numReserva +" no es válida.");
        }
        hueco.getPlaza().setVehiculo(vehiculo);
	}


	//TO-DO alumno opcional

	public void desocuparPlaza(int numReserva) {
        Reserva reserva = registroReservas.obtenerReserva(numReserva);
        Hueco hueco = reserva.getHueco();
        hueco.getPlaza().setVehiculo(null);
        reserva.getGestorZona().liberarHueco(hueco);
	}

	public void anularReserva(int numReserva) {
        Reserva reserva = registroReservas.obtenerReserva(numReserva);
        reserva.getGestorZona().liberarHueco(reserva.getHueco());
        registroReservas.borrarReserva(numReserva);
	}

		
	// PRE (no es necesario comprobar): todas las solicitudes atendidas son válidas.
	public IList<Integer> getReservasRegistradasDesdeListaEspera(int i, int j){
        IList<SolicitudReservaAnticipada> solicitudesAtendidas = gestorLocalidad.getGestorZona(i, j).getSolicitudesAtendidasListaEspera();
        IList<Integer> res = new ArrayList<Integer>();

        for (int k = 0; k < solicitudesAtendidas.size(); k++) {
            res.add(k, registroReservas.registrarReserva(solicitudesAtendidas.get(k)));
        }

		return res;
	}
}
