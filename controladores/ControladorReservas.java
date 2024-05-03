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

    /**
     * Método getter del atributo gestorLocalidad
     * @return gestorLocalidad atributo
     */
	public GestorLocalidad getGestorLocalidad() {
		return gestorLocalidad;
	}

	/**
     * Método getter del atributo registroReservas
     * @return registroReservas atributo
     */
	public Reservas getRegistroReservas() {
		return registroReservas;
	}

	/**
     * Método que comprueba si una reserva asociada a una zona, num de reserva y num de matrícula es válida
     * @return True en caso de que exitsa reserva con ese  num de reserva y pueda validarse, e.o.c False
     */
	public boolean esValidaReserva(int i, int j, int numPlaza, int numReserva, String noMatricula) {
		Reserva reserva = this.registroReservas.obtenerReserva(numReserva);
		if (reserva == null)
			return false;
		reserva.validar(i, j, numPlaza, noMatricula, gestorLocalidad);
		return reserva.getEstadoValidez() == EstadoValidez.OK;
	}

    /**
     * Constructor de ControladorReservas
     * @param plazas Matriz con el número de plazas disponibles de cada gestor de zona del GestorLocalidad
     * @param precios Matriz con el precio de una plaza en cada gestor de zona del GestorLocalidad
     */
	public ControladorReservas(int[][] plazas, double[][] precios) {
        gestorLocalidad = new GestorLocalidad(plazas, precios);
        registroReservas = new Reservas();
	}


    /**
     * Gestiona la solicitud de reserva dada
     * @param solicitud Solicitud de reserva que se desea llevar a cabo
     * @throws SolicitudReservaInvalida En caso de que la solicitud sea inválida
     * @return Si se consigue registrar la solicitud de reserva se devuelve el número de reserva, e.o.c. -1
     */
	public int hacerReserva(SolicitudReserva solicitud) throws SolicitudReservaInvalida {
        if (!solicitud.esValida(gestorLocalidad)) {
            throw new SolicitudReservaInvalida("Solicitud inválida, o bien la zona seleccionada no existe en la localidad, " + 
                    "el tiempo inicial excede al tiempo final o su vehiculo está sancionado.");
        }
        solicitud.gestionarSolicitudReserva(gestorLocalidad);
        return solicitud.getHueco() == null ? -1 : registroReservas.registrarReserva(solicitud);
	}

    /**
     * Devuelve la reserva asociada al número de reserva dado
     * @param numReserva Número de reserva que se desea comprobar
     * @return Reserva asociada al número provisto si existe, e.o.c. null
     */
	public Reserva getReserva(int numReserva) {
		return registroReservas.obtenerReserva(numReserva);
	}

	//PRE: la plaza dada está libre y la reserva está validada

    /**
     * Ocupa la plaza asociada al número de reserva dado con el vehiculo correspondiente, en el gestor de zona [i, j], si los datos provistos son válidos
     * @param i Componenete i de la zona
     * @param j Componenete j de la zona
     * @param numPlaza Número de plaza del gestor de zona
     * @param numReserva Número de reserva
     * @param vehiculo Vehículo con el que se desea ocupar la plaza
     * @throws PlazaOcupada En caso de que la plaza que se desee ocupar ya tenga un vehículo asociado
     * @throws ReservaInvalida Si los datos provistos de la reserva no son válidos
     */
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


    /**
     * Se libera el hueco asociado a la reserva con número de reserva dado, y se establece el vehículo de la plaza como null
     * @param numReserva Número de reserva asociado a la reserva cuyo hueco se desea liberar
     */
	public void desocuparPlaza(int numReserva) {
        Reserva reserva = registroReservas.obtenerReserva(numReserva);
        Hueco hueco = reserva.getHueco();
        hueco.getPlaza().setVehiculo(null);
        reserva.getGestorZona().liberarHueco(hueco);
	}

    /**
     * Se libera el hueco asociado a la reserva, y se borra del registro de reservas
     * @param numReserva Número de reserva asociado a la reserva que se desea anular
     */
	public void anularReserva(int numReserva) {
        Reserva reserva = registroReservas.obtenerReserva(numReserva);
        reserva.getGestorZona().liberarHueco(reserva.getHueco());
        registroReservas.borrarReserva(numReserva);
	}

	// PRE (no es necesario comprobar): todas las solicitudes atendidas son válidas.
    
    /**
     * Se obtienen las reservas que pueden ser atendidas de la zona indicada, y se registran en el registro de reservas
     * @param i Componente i de la zona del gestor de localidad
     * @param j Componente j de la zona del gestor de localidad
     * @return Lista de reservas que han podido ser atendidas
     */
	public IList<Integer> getReservasRegistradasDesdeListaEspera(int i, int j){
        IList<SolicitudReservaAnticipada> solicitudesAtendidas = gestorLocalidad.getSolicitudesAtendidasListaEspera(i, j);
        if (solicitudesAtendidas == null) return null;

        IList<Integer> res = new ArrayList<Integer>();

        for (int k = 0; k < solicitudesAtendidas.size(); k++) {
            res.add(k, registroReservas.registrarReserva(solicitudesAtendidas.get(k)));
        }

		return res;
	}
}
