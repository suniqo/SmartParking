package modelo.gestoresplazas;

import list.IList;
import modelo.gestoresplazas.huecos.Hueco;
import modelo.reservas.solicitudesreservas.SolicitudReservaAnticipada;

//TO-DO alumno obligatorio

public class GestorLocalidad {
    
    private GestorZona[][] gestorZonas;

	public GestorLocalidad(int[][] plazas, double[][] precios) {
        gestorZonas = new GestorZona[plazas.length][plazas[0].length];

        for (int i = 0; i < gestorZonas.length; i++) 
            for (int j = 0; j < gestorZonas[i].length; j++) 
                gestorZonas[i][j] = new GestorZona(i, j, plazas[i][j], precios[i][j]);
	}
	
	public int getRadioMaxI() {
		return gestorZonas.length - 1;
	}
	
    // En caso de que la matriz fuese rectangular el método no haría falta,
    // comprobar todas las filas, bastaria con coger la longitud de una cualquiera.
    // TODO comprobar si son rectangulares
	public int getRadioMaxJ() {
        return gestorZonas[0].length;
        //int maximo = gestorZonas[0].length;
        //for (int i = 1; i < gestorZonas.length; i++) {
        //    int columnas = gestorZonas[i].length; 
        //    maximo = columnas > maximo ? columnas : maximo;
        //}

		//return maximo;
	}
	
	public boolean existeZona(int i, int j) {
		return i >= 0 && i <= getRadioMaxI()
            && j >= 0 && j <= getRadioMaxJ();
	}

	public boolean existeHuecoReservado(Hueco hueco, int i, int j) {
        if (!existeZona(i, j)) {
            // TODO excepcion?
        }
        return gestorZonas[i][j].existeHuecoReservado(hueco);
	}

	public GestorZona getGestorZona(int i, int j)  {
        if(!existeZona(i, j)) {
            // TODO excepcion?
            return null;
        }
		return gestorZonas[i][j];
	}
	
	//TO-DO alumno opcional
	
	public IList<SolicitudReservaAnticipada> getSolicitudesAtendidasListaEspera(int i, int j) {
		//TO-DO
		return null;
	}
	
}
