package de.sematre.api.tg;

import java.io.Serializable;

public class Vertretung implements Serializable {

	private static final long serialVersionUID = -1133323877874234703L;
	private String klasse = "";
	private String stunde = "";
	private String art = "";
	private String stattFach = "";
	private String fach = "";
	private String stattRaum = "";
	private String raum = "";
	private String vertretungstext = "";

	public Vertretung() {}

	public Vertretung(String klasse, String stunde, String art, String stattFach, String fach, String stattRaum, String raum, String vertretungstext) {
		this.klasse = klasse;
		this.stunde = stunde;
		this.art = art;
		this.stattFach = stattFach;
		this.fach = fach;
		this.stattRaum = stattRaum;
		this.raum = raum;
		this.vertretungstext = vertretungstext;
	}

	public String getKlasse() {
		return klasse;
	}

	public void setKlasse(String klasse) {
		this.klasse = klasse;
	}

	public String getStunde() {
		return stunde;
	}

	public void setStunde(String stunde) {
		this.stunde = stunde;
	}

	public String getArt() {
		return art;
	}

	public void setArt(String art) {
		this.art = art;
	}

	public String getStattFach() {
		return stattFach;
	}

	public void setStattFach(String stattFach) {
		this.stattFach = stattFach;
	}

	public String getFach() {
		return fach;
	}

	public void setFach(String fach) {
		this.fach = fach;
	}

	public String getStattRaum() {
		return stattRaum;
	}

	public void setStattRaum(String stattRaum) {
		this.stattRaum = stattRaum;
	}

	public String getRaum() {
		return raum;
	}

	public void setRaum(String raum) {
		this.raum = raum;
	}

	public String getVertretungstext() {
		return vertretungstext;
	}

	public void setVertretungstext(String vertretungstext) {
		this.vertretungstext = vertretungstext;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((art == null) ? 0 : art.hashCode());
		result = prime * result + ((fach == null) ? 0 : fach.hashCode());
		result = prime * result + ((klasse == null) ? 0 : klasse.hashCode());
		result = prime * result + ((raum == null) ? 0 : raum.hashCode());
		result = prime * result + ((stattFach == null) ? 0 : stattFach.hashCode());
		result = prime * result + ((stattRaum == null) ? 0 : stattRaum.hashCode());
		result = prime * result + ((stunde == null) ? 0 : stunde.hashCode());
		result = prime * result + ((vertretungstext == null) ? 0 : vertretungstext.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Vertretung other = (Vertretung) obj;
		if (art == null) {
			if (other.art != null) return false;
		} else if (!art.equals(other.art)) return false;
		if (fach == null) {
			if (other.fach != null) return false;
		} else if (!fach.equals(other.fach)) return false;
		if (klasse == null) {
			if (other.klasse != null) return false;
		} else if (!klasse.equals(other.klasse)) return false;
		if (raum == null) {
			if (other.raum != null) return false;
		} else if (!raum.equals(other.raum)) return false;
		if (stattFach == null) {
			if (other.stattFach != null) return false;
		} else if (!stattFach.equals(other.stattFach)) return false;
		if (stattRaum == null) {
			if (other.stattRaum != null) return false;
		} else if (!stattRaum.equals(other.stattRaum)) return false;
		if (stunde == null) {
			if (other.stunde != null) return false;
		} else if (!stunde.equals(other.stunde)) return false;
		if (vertretungstext == null) {
			if (other.vertretungstext != null) return false;
		} else if (!vertretungstext.equals(other.vertretungstext)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "{klasse=" + klasse + ", stunde=" + stunde + ", art=" + art + ", stattFach=" + stattFach + ", fach=" + fach + ", stattRaum=" + stattRaum + ", raum=" + raum + ", vertretungstext=" + vertretungstext + "}";
	}
}