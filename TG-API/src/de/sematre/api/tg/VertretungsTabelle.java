package de.sematre.api.tg;

import java.io.Serializable;
import java.util.ArrayList;

public class VertretungsTabelle implements Serializable {

	private static final long serialVersionUID = 2088874862043425751L;
	private ArrayList<Vertretung> vertretungen = null;
	private String date = null;

	public VertretungsTabelle(ArrayList<Vertretung> vertretungen, String date) {
		this.vertretungen = vertretungen;
		this.date = date;
	}

	public ArrayList<Vertretung> getVertretungen() {
		return vertretungen;
	}

	public String getDate() {
		return date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((vertretungen == null) ? 0 : vertretungen.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		VertretungsTabelle other = (VertretungsTabelle) obj;
		if (date == null) {
			if (other.date != null) return false;
		} else if (!date.equals(other.date)) return false;
		if (vertretungen == null) {
			if (other.vertretungen != null) return false;
		} else if (!vertretungen.equals(other.vertretungen)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "{vertretungen=" + vertretungen + ", date=" + date + "}";
	}
}