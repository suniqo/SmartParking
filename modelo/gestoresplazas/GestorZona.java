package modelo.gestoresplazas;

import java.time.LocalDateTime;
import java.util.Arrays;

import list.ArrayList;
import list.IList;

import modelo.gestoresplazas.huecos.GestorHuecos;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.gestoresplazas.huecos.Plaza;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;

public class GestorZona {
	private int iZona;
	private int jZona;
	private Plaza[] plazas;
	private double precio;
	private IList<SolicitudReservaAnticipada> listaEspera;
	private GestorHuecos gestorHuecos;
	private IList<Hueco> huecosReservados;
	
	public int getI() {
		return iZona;
	}
	
	public int getJ() {
		return jZona;
	}
	
	public double getPrecio() {
		return precio;
	}
	
	public String getId() {
		return "z" + iZona + ":" + jZona;
	}
	
	public String getEstadoHuecosLibres() {
		return this.gestorHuecos.toString();
	}
	
	public String getEstadoHuecosReservados() {
		return this.huecosReservados.toString();
	}
	
	public String getListaEspera() {
		return this.listaEspera.toString();
	}
	
	public String getPlazas() {
		return Arrays.toString(this.plazas);
	}
	
	public String toString() {
		return getId() + ": " + getEstadoHuecosReservados();
	}
	
	public GestorZona(int i, int j, int noPlazas, double precio) {
        iZona = i;
        jZona = j;

        plazas  = new Plaza[noPlazas];
        gestorHuecos = new GestorHuecos(plazas);

        listaEspera =  new ArrayList<SolicitudReservaAnticipada>();
        huecosReservados = new ArrayList<Hueco>();

        this.precio = precio;
	}

	public Hueco reservarHueco(LocalDateTime tI, LocalDateTime tF) {
        if (existeHueco(tI, tF)) {
            Hueco huecoReservado = gestorHuecos.reservarHueco(tI, tF);
            huecosReservados.add(huecosReservados.size(), huecoReservado);
            return huecoReservado;
        }
		return null;
	}
	
	public void meterEnListaEspera(SolicitudReservaAnticipada solicitud) {
        listaEspera.add(listaEspera.size(), solicitud);
	}

	
	public boolean existeHuecoReservado(Hueco hueco) {
        return huecosReservados.indexOf(hueco) != -1;
	}
	
	public void liberarHueco(Hueco hueco) {
        huecosReservados.remove(hueco);
        gestorHuecos.liberarHueco(hueco);
	}

	//PRE (no es necesario comprobar): las solicitudes de la lista de espera son válidas
	public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera() {
        IList<SolicitudReservaAnticipada> res = new ArrayList<SolicitudReservaAnticipada>();

        for(int i = 0; i<listaEspera.size(); i++) {
            SolicitudReservaAnticipada solicitud = listaEspera.get(i);

            if(existeHueco(solicitud.getTInicial(), solicitud.getTFinal())) {
                res.add(res.size(), listaEspera.get(i));
                listaEspera.removeElementAt(i);
            }
        }
        return res;
	}

    public boolean existeHueco(LocalDateTime tI, LocalDateTime tF) {
        return gestorHuecos.existeHueco(tI, tF);
    }
}
