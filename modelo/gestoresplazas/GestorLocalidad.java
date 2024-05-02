package modelo.gestoresplazas;

import anotacion.Programacion2;
import list.IList;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;

@Programacion2 (
		nombreAutor1 = "Andrés",
		apellidoAutor1 = "Súnico Sánchez",
		emailUPMAutor1 = "andres.sunico",
		nombreAutor2 = "Samuel",
		apellidoAutor2 = "Álvarez Salido",
		emailUPMAutor2 = "samuel.alvarez"
		)

public class GestorLocalidad {
    
    private GestorZona[][] gestoresZonas;

    /**
     * Constructor de GestorLocalidad
     * @param plazas Matriz que contiene el número de plazas disponible en cada zona
     * @param precios Matriz que contiene los precios asociados a las plazas de cada zona
     */
	public GestorLocalidad(int[][] plazas, double[][] precios) {
        gestoresZonas = new GestorZona[plazas.length][plazas[0].length];

        for (int i = 0; i < gestoresZonas.length; i++) 
            for (int j = 0; j < gestoresZonas[i].length; j++) 
                gestoresZonas[i][j] = new GestorZona(i, j, plazas[i][j], precios[i][j]);
	}
	
    /**
     * Devuelve el ínidce máximo en las filas de la matriz de gestores de zona
     * @return Índice máximo de filas 
     */
	public int getRadioMaxI() {
		return gestoresZonas.length - 1;
	}
	
    /**
     * Devuelve el ínidce máximo en las columnas de la matriz de gestores de zona
     * @return Índice máximo de columnas
     */
	public int getRadioMaxJ() {
        return gestoresZonas[0].length - 1;
	}
	
    /**
     * Indica si la coordenada provista existe en la localidad
     * @param i Componente i de la coordenada
     * @param j Componente j de la coordenada
     * @return True si existe en la localidad e.o.c false
     */
	public boolean existeZona(int i, int j) {
		return i >= 0 && i <= getRadioMaxI()
            && j >= 0 && j <= getRadioMaxJ();
	}

    /**
     * Indica si el hueco provisto está reservado en la zona con la coordenada dada
     * @param hueco El Hueco cuyo estado de reserva se desea comprobar
     * @param i Componente i de la coordenada
     * @param j Componente j de la coordenada
     * @return True si el hueco existe y está reservado, e.o.c false
     */
	public boolean existeHuecoReservado(Hueco hueco, int i, int j) {
        return existeZona(i,j) ? gestoresZonas[i][j].existeHuecoReservado(hueco) : false;
	}

    /**
     * Devuelve, si existe, el gestor correpondiente a la coordenada dada
     * @param i Componente i de la coordenada 
     * @param j Componente j de la coordenada
     * @return El GestorZona si existe, e.o.c null
     */
	public GestorZona getGestorZona(int i, int j)  {
		return existeZona(i, j) ? gestoresZonas[i][j] : null;
	}
	
    /**
     * Devuelve, si existe el gestor de zona, las solicitudes de reserva que pueden ser atendidas en este
     * @param i Componente i de la coordenada 
     * @param j Componente i de la coordenada 
     * @return Las solicitudes que pueden ser atendidasa en el gestor de zona si existe, e.o.c null
     */
	public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera(int i, int j) {
		return existeZona(i, j) ? gestoresZonas[i][j].getSolicitudesAtendidasListaEspera() : null;
	}
}
