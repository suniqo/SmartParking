package modelo.gestoresplazas;

import java.time.LocalDateTime;
import java.util.Arrays;

import anotacion.Programacion2;
import list.ArrayList;
import list.IList;

import modelo.gestoresplazas.huecos.GestorHuecos;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.gestoresplazas.huecos.Plaza;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;

@Programacion2 (
		nombreAutor1 = "Andrés",
		apellidoAutor1 = "Súnico Sánchez",
		emailUPMAutor1 = "andres.sunico@alumnos.upm.es",
		nombreAutor2 = "Samuel",
		apellidoAutor2 = "Álvarez Salido",
		emailUPMAutor2 = "samuel.alvarez@alumnos.upm.es"
		)

public class GestorZona {
    private int iZona;
    private int jZona;
    private Plaza[] plazas;
    private double precio;
    private IList<SolicitudReservaAnticipada> listaEspera;
    private GestorHuecos gestorHuecos;
    private IList<Hueco> huecosReservados;
    
    /**
     * Método getter del atributo iZona
     * @return iZona atributo
     */
    public int getI() {
        return iZona;
    }

    /**
     * Método getter del atributo jZona
     * @return jZona atributo
     */
    public int getJ() {
        return jZona;
    }

    /**
     * Método getter del atributo precio
     * @return precio atributo
     */
    public double getPrecio() {
        return precio;
    }

    /** Método toString del Id asociado al gestor de zona
     * @return String representativo de la zona en la que se encuentra el gestor de zona
     */
    public String getId() {
        return "z" + iZona + ":" + jZona;
    }

    /** Método toString del estado del gestor de huecos
     * @return String representativo del estado de cada plaza del gestor de zona 
     */
    public String getEstadoHuecosLibres() {
        return this.gestorHuecos.toString();
    }

    /** Método toString del ArrayList asociado a los huecos reservados 
     * @return String representativo con los huecos reservados hasta el momento
     */
    public String getEstadoHuecosReservados() {
        return this.huecosReservados.toString();
    }

    /** Método toString del ArrayList asociado a la lista de espera 
     * @return String representativo con las solicitudes de reserva anticipada que se encuentran en espera
     */
    public String getListaEspera() {
        return this.listaEspera.toString();
    }

    /** Método toString del atributo plazas
     * @return String representativo con el estado de cada plaza del gestor de zona
     */
    public String getPlazas() {
        return Arrays.toString(this.plazas);
    }

    /** Método toString que proporciona información sobre la zona del gestor de zona y los huecos reservados
     * @return String representativo de una solicitud de reserva
     */
    public String toString() {
        return getId() + ": " + getEstadoHuecosReservados();
    }
    
    /**
     * Constructor de GestorZona
     * @param i Coordenada i de la zona en la localidad
     * @param j Coordenada j de la zona en la localidad
     * @param noPlazas Número de plazas disponibles en la zona
     * @param precio Precio de una plaza en la zona
     */
    public GestorZona(int i, int j, int noPlazas, double precio) {
        iZona = i;
        jZona = j;

        plazas  = new Plaza[noPlazas];
        for (int k = 0; k < plazas.length; k++) {
            plazas[k] = new Plaza(k);
        }

        gestorHuecos = new GestorHuecos(plazas);

        listaEspera =  new ArrayList<SolicitudReservaAnticipada>();
        huecosReservados = new ArrayList<Hueco>();

        this.precio = precio;
    }

    /**
     * Se intenta reservar un hueco en la franja de tiempo dada
     * @param tI Comienzo de la reserva
     * @param tF Fin de la reserva
     * @return Hueco reservado exitosamente o null en caso de que no haya disponibles
     */
    public Hueco reservarHueco(LocalDateTime tI, LocalDateTime tF) {
        Hueco huecoReservado = gestorHuecos.reservarHueco(tI, tF);
        if(huecoReservado != null)
            huecosReservados.add(huecosReservados.size(), huecoReservado);
        return huecoReservado;
    }

    /**
     * Se añade la solicitud dada a la lista de espera
     * @param solicitud Solicitud que se desea añadir
     */
    public void meterEnListaEspera(SolicitudReservaAnticipada solicitud) {
        listaEspera.add(listaEspera.size(), solicitud);
    }

    /**
     * Indica si el hueco dado está reservado en la zona
     * @param hueco Hueco que se desea comprobar
     * @return True si está reservado e.o.c false
     */
    public boolean existeHuecoReservado(Hueco hueco) {
        return huecosReservados.indexOf(hueco) != -1;
    }

    /**
     * Si el hueco está en la lista de huecos reservados, se libera
     * @param hueco Hueco que se desea liberar
     */
    public void liberarHueco(Hueco hueco) {
        if (existeHuecoReservado(hueco)) {
            huecosReservados.remove(hueco);
            gestorHuecos.liberarHueco(hueco);
        }
    }

    //PRE (no es necesario comprobar): las solicitudes de la lista de espera son válidas
    /**
     * Extrae las solicitudes que pueden ser atendidas, y se eliminan de la lista de espera
     * @return ArrayList de las solicitudes que se pueden atender
     */
    public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera() {
        IList<SolicitudReservaAnticipada> res = new ArrayList<SolicitudReservaAnticipada>();
        IList<SolicitudReservaAnticipada> newListaEspera = new ArrayList<SolicitudReservaAnticipada>();

        for(int i = 0; i<listaEspera.size(); i++) {

            SolicitudReservaAnticipada solicitud = listaEspera.get(i);
            Hueco huecoSolicitud = reservarHueco(solicitud.getTInicial(), solicitud.getTFinal());

            if(huecoSolicitud != null) {
                res.add(res.size(), solicitud);
                solicitud.setHueco(huecoSolicitud);
            }else
                newListaEspera.add(newListaEspera.size(), solicitud);
        }
        listaEspera = copiaArrayList(newListaEspera);
        return res;
    }

    /**
     * Método auxiliar para hacer una deepCopy de un ArrayList (se podría haber utilizado el método predefinido clon)
     * @param newListaEspera ArrayList que se desea copiar
     * @return ArrayList deep copy
     */
    private ArrayList<SolicitudReservaAnticipada> copiaArrayList (IList<SolicitudReservaAnticipada> newListaEspera){
        ArrayList<SolicitudReservaAnticipada> res = new ArrayList<SolicitudReservaAnticipada>();
        for(int i = 0; i < newListaEspera.size(); i++)
            res.add(i, newListaEspera.get(i));
        return res;
    }
    
    /**
     * Indica si existe un hueco en la franja de tiempo provista
     * @param tI Comienzo de la reserva
     * @param tF Fin de la reserva
     * @return True si existe e.o.c. false
     */
    public boolean existeHueco(LocalDateTime tI, LocalDateTime tF) {
        return gestorHuecos.existeHueco(tI, tF);
    }
}

