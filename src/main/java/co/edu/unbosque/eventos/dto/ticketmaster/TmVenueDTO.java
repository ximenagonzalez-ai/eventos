package co.edu.unbosque.eventos.dto.ticketmaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
	/**
	 * DTO que mapea un "venue" (lugar) de la respuesta de Ticketmaster.
	 *
	 * En el JSON de Ticketmaster viene dentro de:
	 *   _embedded -> venues -> [0]
	 *
	 * Ejemplo del JSON:
	 * {
	 *   "name": "Teatro Colon",
	 *   "address": { "line1": "Calle 10 # 5-32" },
	 *   "city":    { "name": "Bogota" },
	 *   "location": {
	 *     "longitude": "-74.0821",
	 *     "latitude":  "4.6097"
	 *   }
	 * }
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class TmVenueDTO {

	    /** Nombre del lugar (ej. "Teatro Colon"). */
	    private String name;

	    /**
	     * Objeto que contiene la direccion del venue.
	     * Ticketmaster lo devuelve como { "line1": "Calle 10 # 5-32" }.
	     */
	    private TmAddressDTO address;

	    /**
	     * Objeto que contiene la ciudad del venue.
	     * Ticketmaster lo devuelve como { "name": "Bogota" }.
	     */
	    private TmCityDTO city;

	    /**
	     * Coordenadas geograficas del venue.
	     * Estas son las que usamos para mostrar la ruta en el mapa del frontend.
	     * Ticketmaster las devuelve como Strings, las convertimos a Double al mapear.
	     */
	    private TmLocationDTO location;

	    public String getName() { return name; }
	    public void setName(String name) { this.name = name; }

	    public TmAddressDTO getAddress() { return address; }
	    public void setAddress(TmAddressDTO address) { this.address = address; }

	    public TmCityDTO getCity() { return city; }
	    public void setCity(TmCityDTO city) { this.city = city; }

	    public TmLocationDTO getLocation() { return location; }
	    public void setLocation(TmLocationDTO location) { this.location = location; }
	}
