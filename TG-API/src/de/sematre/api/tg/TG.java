package de.sematre.api.tg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.sematre.api.dsbmobile.DSBMobile;

public class TG {

	private DSBMobile dsbMobile = null;
	private String filterPrefix = null;

	public TG() {
		dsbMobile = new DSBMobile("226142", "tgrv");
	}

	public TG(String username, String password) {
		dsbMobile = new DSBMobile(username, password);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<VertretungsTabelle> get() {
		try {
			ArrayList<String> dates = new ArrayList<>();
			Integer currentIndex = 0;
			HashMap<Integer, ArrayList<Vertretung>> plan = new HashMap<>();
			Vertretung currentVertretung = new Vertretung();
			ArrayList<Vertretung> currentVertretungen = new ArrayList<>();

			Document document = Jsoup.connect(dsbMobile.getTimeTables().get(0).getUrl()).get();
			for (Element element : document.select("div")) {
				if (element.hasClass("mon_title")) {
					dates.add(element.html());
				}
			}

			Integer i = 0;
			for (Element element : document.select("tr")) {
				if (i++ == 0) {
					continue;
				}

				if (!currentVertretung.getKlasse().equals("")) {
					currentVertretungen.add(currentVertretung);
					currentVertretung = new Vertretung();
				}

				Integer e = 1;
				for (Element element2 : element.getAllElements()) {
					String line = element2.toString();
					if (line.startsWith("<tr class=\"list\">")) {
						plan.put(currentIndex, currentVertretungen);
						currentIndex++;
						currentVertretungen = new ArrayList<>();
						break;
					} else if (line.startsWith("<tr>")) {
						break;
					} else if (line.startsWith("<tr class=\"list odd\">") || line.startsWith("<tr class=\"list even\">")) {
						continue;
					} else if (line.startsWith("<td class=\"list\" align=\"center\">&nbsp;</td>")) {
						continue;
					}

					if (e == 1) {
						currentVertretung.setKlasse(element2.text());
					}

					if (e == 2) {
						currentVertretung.setStunde(element2.text());
					}

					if (e == 3) {
						currentVertretung.setArt(element2.text());
					}

					if (e == 4) {
						currentVertretung.setStattFach(element2.text());
					}

					if (e == 5) {
						currentVertretung.setFach(element2.text());
					}

					if (e == 6) {
						currentVertretung.setStattRaum(element2.text());
					}

					if (e == 7) {
						currentVertretung.setRaum(element2.text());
					}
					e++;
				}
			}

			currentVertretungen.add(currentVertretung);
			plan.put(currentIndex, currentVertretungen);
			plan.remove(0);

			ArrayList<ArrayList<Vertretung>> badList = new ArrayList<>();
			if (filterPrefix == null || filterPrefix.equals("")) {
				for (Object obj : plan.values().toArray())
					badList.add((ArrayList<Vertretung>) obj);
			} else {
				for (Object obj : plan.values().toArray()) {
					ArrayList<Vertretung> subList = new ArrayList<>();
					ArrayList<Vertretung> copyList = (ArrayList<Vertretung>) obj;
					for (Vertretung vertretung : copyList) {
						if (vertretung.getKlasse().startsWith(filterPrefix)) subList.add(vertretung);
					}

					badList.add(subList);
				}
			}

			ArrayList<VertretungsTabelle> tabellen = new ArrayList<>();
			for (Integer count = 0; count < badList.size(); count++) {
				String date = "";
				try {
					date = dates.get(count);
				} catch (ArrayIndexOutOfBoundsException e) {}

				tabellen.add(new VertretungsTabelle(badList.get(count), date));
			}

			return tabellen;
		} catch (IOException e) {
			try {
				throw e;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return null;
	}

	public void setFilterPrefix(String filterPrefix) {
		this.filterPrefix = filterPrefix;
	}
}