package modelo.reservas.solicitudesreservas;

import java.time.LocalDateTime;

import anotacion.Programacion2;
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

public class SolicitudReserva {
	private int iZona;
	private int jZona;
	private LocalDateTime tInicial;
	private LocalDateTime tFinal;
	private Vehiculo vehiculo;
	private GestorZona gestorZona; // se inicializa al gestionar la solicitud
	private Hueco hueco; // se deja a null hasta que se completa la reserva

    /**
     * Constructor de SolicitudReserva
     * @param i Componente i de la zona
     * @param j Componente j de la zona
     * @param tI Comienzo de la solicitud de reserva
     * @param tF Fin de la solicitud de reserva
     * @param vehiculo Vehículo con el que se desea aparcar
     */
	protected SolicitudReserva(int i, int j, LocalDateTime tI, 
			LocalDateTime tF, Vehiculo vehiculo) {
		this.iZona = i;
		this.jZona = j;
		this.tInicial = tI;
		this.tFinal = tF;
		this.vehiculo = vehiculo;
	}

    /** Método toString de SolicitudReserva
     * @return String representativo de una solicitud de reserva
     */
	public String toString() {
		return "(Sol:" + iZona + " " + jZona + " " + tInicial.toLocalTime() + " " + tFinal.toLocalTime() 
		+ " " + this.vehiculo.getMatricula() +  ")";
	}
	
    /**
     * Método setter del atributo hueco
     * @param hueco
     */
	public void setHueco(Hueco hueco) {
		this.hueco = hueco;		
	}

    /**
     * Método getter del atributo hueco
     * @return Hueco atributo
     */
	public Hueco getHueco() {
		return hueco;
	}
	
    /**
     * Método setter del atributo gestorZona
     * @param gestor
     */
	public void setGestorZona(GestorZona gestor) {
		this.gestorZona = gestor;		
	}
	
    /**
     * Método getter del atributo gestorZona
     * @return GestorZona atributo
     */
	public GestorZona getGestorZona() {
		return this.gestorZona;
	}
	
    /**
     * Método getter del atributo iZona
     * @return Componente i de la zona
     */
	public int getIZona() {
		return iZona;
	}

    /**
     * Método getter del atributo jZona
     * @return Componente j de la zona
     */
	public int getJZona() {
		return jZona;
	}

    /**
     * Método getter del atributo tInicial
     * @return Comienzo de la solicitud de reserva
     */
	public LocalDateTime getTInicial() {
		return tInicial;
	}

    /**
     * Método getter del atributo tFinal
     * @return Fin de la solicitud de reserva
     */
	public LocalDateTime getTFinal() {
		return tFinal;
	}

    /**
     * Método getter del atributo hueco
     * @return Vehiculo que se desea aparcaar en la solicitud
     */
	public Vehiculo getVehiculo() {
		return vehiculo;
	}
	
    /**
     * Indica si la solicitud de reserva es válida en el gestor de localidad provisto
     * @param gestorLocalidad GestorLocalidad dónde se desea comprobar la validez de la reserva
     * @return True si existe la zona de la solicitud en gestorLocalidad, el comienzo de la reserva es previo al fin y el vehículo no está sancionado, e.o.c. false 
     */
	public boolean esValida(GestorLocalidad gestorLocalidad) {
        return gestorLocalidad.existeZona(iZona, jZona)
            && tInicial.isBefore(tFinal)
            && !vehiculo.getSancionado();
	}
	
    /**
     * Se intenta llevar a cabo la solicitud y obtener un hueco válido en el gestor de localidad provisto
     * @param gestor GestorLocalidad donde se desea llevar a cabo la reserva
     */
	public void gestionarSolicitudReserva(GestorLocalidad gestor) {
        gestorZona = gestor.getGestorZona(iZona, jZona);
        hueco = gestorZona.reservarHueco(tInicial, tFinal);
	}
}
