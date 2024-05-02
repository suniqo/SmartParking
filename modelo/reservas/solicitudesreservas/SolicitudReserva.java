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

	protected SolicitudReserva(int i, int j, LocalDateTime tI, 
			LocalDateTime tF, Vehiculo vehiculo) {
		this.iZona = i;
		this.jZona = j;
		this.tInicial = tI;
		this.tFinal = tF;
		this.vehiculo = vehiculo;
	}

	public String toString() {
		return "(Sol:" + iZona + " " + jZona + " " + tInicial.toLocalTime() + " " + tFinal.toLocalTime() 
		+ " " + this.vehiculo.getMatricula() +  ")";
	}
	
	public void setHueco(Hueco hueco) {
		this.hueco = hueco;		
	}

	public Hueco getHueco() {
		return hueco;
	}
	
	public void setGestorZona(GestorZona gestor) {
		this.gestorZona = gestor;		
	}
	
	public GestorZona getGestorZona() {
		return this.gestorZona;
	}
	
	public int getIZona() {
		return iZona;
	}

	public int getJZona() {
		return jZona;
	}

	public LocalDateTime getTInicial() {
		return tInicial;
	}

	public LocalDateTime getTFinal() {
		return tFinal;
	}

	public Vehiculo getVehiculo() {
		return vehiculo;
	}
	
	public boolean esValida(GestorLocalidad gestorLocalidad) {
        return gestorLocalidad.existeZona(iZona, jZona)
            && tInicial.isBefore(tFinal)
            && !vehiculo.getSancionado();
	}
	
	public void gestionarSolicitudReserva(GestorLocalidad gestor) {
        gestorZona = gestor.getGestorZona(iZona, jZona);
        hueco = gestorZona.reservarHueco(tInicial, tFinal);
	}
}
