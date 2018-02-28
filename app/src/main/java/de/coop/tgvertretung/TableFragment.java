package de.coop.tgvertretung;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.Space;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.sematre.api.tg.Vertretung;
import de.sematre.api.tg.VertretungsTabelle;

public class TableFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final boolean SYSOUT = false;
    static VertretungsTabelle table = null;
    private TextView label = null;
    private LinearLayout mainLayout = null;
    private ConstraintLayout mainScroll = null;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TableFragment newInstance(int sectionNumber) {
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        if (SYSOUT)
            System.out.println("sectionNumber: " + sectionNumber);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        mainLayout = (LinearLayout) rootView.findViewById(R.id.MainLayout);
        System.out.println("MainLayoutID: " + R.id.MainLayout);
        System.out.println("mainLayout: " + mainLayout);
        label = (TextView) rootView.findViewById(R.id.section_label);
        mainScroll = (ConstraintLayout) rootView.findViewById(R.id.MainScroll);
        mainScroll.setVerticalScrollBarEnabled(false);
        mainScroll.setHorizontalScrollBarEnabled(false);

        int asn = getArguments().getInt(ARG_SECTION_NUMBER);
        table = Client.tables.get(asn);

        if (SYSOUT)
            System.out.println("=> Neuer Plan! (" + table.getDate() + ")");

        label.setText(table.getDate());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setColor(table);
        }

        int i = 0;
        if (asn == 1)
            i = 2;
        else if (asn == 2)
            i = 1;

        viewUI(Client.tables.get(i));

        return rootView;
    }

    public void viewUI(VertretungsTabelle Vtable) {

        ArrayList<Vertretung> list = Vtable.getVertretungen();
        ArrayList<Vertretung> filteredList = new ArrayList<>();

        if (list.isEmpty()) {
            if (SYSOUT)
                System.out.println("Leer!");

            addListItem(MainActivity.instance.getString(R.string.nothing), Client.instance.NothingSize, Client.instance.NothingRGB, false);
        } else {
            if (Client.useFilter) {

                for (Vertretung vertretung : list) {
                    if (vertretung.getKlasse().toLowerCase().contains(Client.filter.toLowerCase())) {
                        filteredList.add(vertretung);
                    }
                }
                if (filteredList.isEmpty()) {
                    if (SYSOUT)
                        System.out.println("Leer!");

                    addListItem(MainActivity.instance.getString(R.string.nothing), Client.instance.NothingSize, Client.instance.NothingRGB, false);
                } else {
                    for (Vertretung fvertretung : filteredList) {

                        if (SYSOUT)
                            System.out.println(fvertretung.toString());
                        addTableItem(fvertretung.getStunde(), fvertretung.getKlasse(),
                                fvertretung.getVertretungstext(), fvertretung.getArt(),
                                fvertretung.getFach(), fvertretung.getRaum(),
                                fvertretung.getStattFach(), fvertretung.getStattRaum(), Client.extendet);
                    }
                }
            } else {
                for (Vertretung vertretung : list) {

                    if (SYSOUT)
                        System.out.println(vertretung.toString());
                    addTableItem(vertretung.getStunde(), vertretung.getKlasse(),
                            vertretung.getVertretungstext(), vertretung.getArt(),
                            vertretung.getFach(), vertretung.getRaum(),
                            vertretung.getStattFach(), vertretung.getStattRaum(), Client.extendet);
                }
            }
        }

        System.out.println();
        System.out.println();
    }

    @SuppressLint("NewApi")
    private void setColor(VertretungsTabelle Vtable) {

        if (Vtable.getDate().contains("Montag")) {
            label.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_green_base));
            mainScroll.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_green_stroke));
        } else if (Vtable.getDate().contains("Dienstag")) {
            label.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_blue_base));
            mainScroll.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_blue_stroke));
        } else if (Vtable.getDate().contains("Mittwoch")) {
            label.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_pink_base));
            mainScroll.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_pink_stroke));
        } else if (Vtable.getDate().contains("Donnerstag")) {
            label.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_orange_base));
            mainScroll.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_orange_stroke));
        } else if (Vtable.getDate().contains("Freitag")) {
            label.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_yellow_base));
            mainScroll.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_yellow_stroke));
        } else {
            label.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_purple_base));
            mainScroll.setBackground(MainActivity.instance.getDrawable(R.drawable.colorgrade_purple_stroke));
        }
    }

    public void addTableItem(String Stunde, String Klasse, String Text, String Art, String Fach, String Raum, String StdFach, String StdRaum, boolean extendet) {

        String tmp = "";
        String tmp1 = Stunde + ": ";
        String tmp2 = Klasse + ": ";

        if (extendet) {

            if (Art.equals("Entfall")) {
                tmp = tmp + MainActivity.instance.getString(R.string.noClass);
            } else if (Fach.equals("---") && Raum.equals("---")) {
                tmp = tmp + MainActivity.instance.getString(R.string.noClass);
            } else {
                tmp = tmp + Fach + " in " + Raum + " statt " + StdFach + " in " + StdRaum;
                if (Text.equals("")) {

                } else {
                    tmp = tmp + " (" + Text + ")";
                }
            }

        } else {

            if (Art.equals("Entfall")) {
                tmp = tmp + MainActivity.instance.getString(R.string.noClass);
            } else if (Fach.equals("---") && Raum.equals("---")) {
                tmp = tmp + MainActivity.instance.getString(R.string.noClass);
            } else {
                tmp = tmp + Fach + " in " + Raum;
            }

        }
        addListItem(tmp, tmp1, tmp2, Client.instance.VertretungSize, Client.instance.VertretungRGB);
    }

    public void addListItem(String text, Integer textSize, int ARGB, boolean multiText) {

        //adds items to the list
        int in = 2;

        TextView nameText = new TextView(MainActivity.instance);
        Space sp = new Space(MainActivity.instance);
        LinearLayout ln = new LinearLayout(MainActivity.instance);

        LinearLayout.LayoutParams nameTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameTextParams.gravity = Gravity.START;
        nameText.setLayoutParams(nameTextParams);
        nameText.setId(View.generateViewId());
        nameText.setTextSize(textSize);
        //nameText.setTextColor(ARGB);
        nameText.setSingleLine(!multiText);
        System.out.println("text: " + text);
        nameText.setText(text);

        LinearLayout.LayoutParams spParams = new LinearLayout.LayoutParams(11 * in, 15 * in);
        spParams.gravity = Gravity.END;
        sp.setLayoutParams(spParams);

        ln.addView(sp);
        ln.addView(nameText);
        mainLayout.addView(ln);

    }

    public void addListItem(String text, String text1, String text2, Integer textSize, int ARGB) {

        //adds items to the list
        int in = 2;

        TextView nameText = new TextView(MainActivity.instance);
        TextView nameText1 = new TextView(MainActivity.instance);
        TextView nameText2 = new TextView(MainActivity.instance);
        Space sp = new Space(MainActivity.instance);
        LinearLayout ln = new LinearLayout(MainActivity.instance);

        LinearLayout.LayoutParams nameTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameTextParams.gravity = Gravity.START;
        nameText.setLayoutParams(nameTextParams);
        nameText.setId(View.generateViewId());
        nameText.setTextSize(textSize);
        nameText.setTextColor(ARGB);
        nameText.setSingleLine(false);
        nameText.setText(text);

        LinearLayout.LayoutParams nameText1Params = new LinearLayout.LayoutParams(50 * in, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameTextParams.gravity = Gravity.START;
        nameText1.setLayoutParams(nameText1Params);
        nameText1.setId(View.generateViewId());
        nameText1.setTextSize(textSize);
        nameText1.setTextColor(ARGB);
        nameText1.setSingleLine(false);
        nameText1.setText(text1);

        LinearLayout.LayoutParams nameText2Params = new LinearLayout.LayoutParams(100 * in, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameTextParams.gravity = Gravity.START;
        nameText2.setLayoutParams(nameText2Params);
        nameText2.setId(View.generateViewId());
        nameText2.setTextSize(textSize);
        nameText2.setTextColor(ARGB);
        nameText2.setSingleLine(false);
        nameText2.setText(text2);

        LinearLayout.LayoutParams spParams = new LinearLayout.LayoutParams(11 * in, 15 * in);
        spParams.gravity = Gravity.END;
        sp.setLayoutParams(spParams);

        ln.addView(sp);
        ln.addView(nameText1);
        ln.addView(nameText2);
        ln.addView(nameText);
        mainLayout.addView(ln);
    }
}
