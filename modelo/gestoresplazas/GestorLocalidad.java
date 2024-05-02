package modelo.gestoresplazas;

import list.IList;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;

//TO-DO alumno obligatorio

public class GestorLocalidad {
    
    private GestorZona[][] gestoresZonas;

	public GestorLocalidad(int[][] plazas, double[][] precios) {
        gestoresZonas = new GestorZona[plazas.length][plazas[0].length];

        for (int i = 0; i < gestoresZonas.length; i++) 
            for (int j = 0; j < gestoresZonas[i].length; j++) 
                gestoresZonas[i][j] = new GestorZona(i, j, plazas[i][j], precios[i][j]);
	}
	
	public int getRadioMaxI() {
		return gestoresZonas.length - 1;
	}
	
    // En caso de que la matriz fuese rectangular el método no haría falta,
    // comprobar todas las filas, bastaria con coger la longitud de una cualquiera.
	public int getRadioMaxJ() {
        return gestoresZonas[0].length - 1;
	}
	
	public boolean existeZona(int i, int j) {
		return i >= 0 && i <= getRadioMaxI()
            && j >= 0 && j <= getRadioMaxJ();
	}

	public boolean existeHuecoReservado(Hueco hueco, int i, int j) {
        return existeZona(i,j) ? gestoresZonas[i][j].existeHuecoReservado(hueco) : false;
	}

	public GestorZona getGestorZona(int i, int j)  {
		return existeZona(i, j) ? gestoresZonas[i][j] : null;
	}
	
	public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera(int i, int j) {
		return existeZona(i, j) ? gestoresZonas[i][j].getSolicitudesAtendidasListaEspera() : null;
	}
}
