package de.coop.tgvertretung;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
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

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        mainLayout = (LinearLayout) rootView.findViewById(R.id.MainLayout);
        Client.print("MainLayoutID: " + R.id.MainLayout);
        Client.print("mainLayout: " + mainLayout);
        label = (TextView) rootView.findViewById(R.id.section_label);
        mainScroll = (ConstraintLayout) rootView.findViewById(R.id.MainScroll);
        mainScroll.setVerticalScrollBarEnabled(false);
        mainScroll.setHorizontalScrollBarEnabled(false);

        int asn = getArguments().getInt(ARG_SECTION_NUMBER);
        table = Client.tables.get(asn);
        Client.print("=> Neuer Plan! (" + table.getDate() + ")");
        label.setText(table.getDate());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setColor(table);
        }

        if (asn == 1)
            asn = 2;
        else if (asn == 2)
            asn = 1;
        viewUI(Client.tables.get(asn));
        return rootView;
    }

    public void viewUI(VertretungsTabelle Vtable) {

        ArrayList<Vertretung> list = Vtable.getVertretungen();
        ArrayList<Vertretung> filteredList = list;

        if (Client.useFilter) {
            filteredList = new ArrayList<>();
            Client.print("___________Using Filter!!!_______________");
            for (Vertretung vertretung : list) {
                if (vertretung.getKlasse().toLowerCase().contains(Client.filter.toLowerCase())) {
                    filteredList.add(vertretung);
                }
            }
        }
        if (filteredList.isEmpty()) {
            Client.print("Leer!");

            addListItem(MainActivity.instance.getString(R.string.nothing), Client.instance.NothingSize, false);
        } else {
            for (Vertretung fvertretung : filteredList) {
                Client.print(fvertretung.toString());
                addTableItem(fvertretung, Client.extendet);
            }
        }
        Client.print("");
        Client.print("");
    }

    @SuppressLint("NewApi")
    private void setColor(VertretungsTabelle Vtable) {
        Drawable base = MainActivity.instance.getDrawable(R.drawable.colorgrade_base);
        Drawable stroke = MainActivity.instance.getDrawable(R.drawable.colorgrade_stroke);

        if (Vtable.getDate().contains("Montag")) {
            base.setTint(getResources().getColor(R.color.yellow));
            stroke.setTint(getResources().getColor(R.color.yellow));
        } else if (Vtable.getDate().contains("Dienstag")) {
            base.setTint(getResources().getColor(R.color.blue));
            stroke.setTint(getResources().getColor(R.color.blue));
        } else if (Vtable.getDate().contains("Mittwoch")) {
            base.setTint(getResources().getColor(R.color.orange));
            stroke.setTint(getResources().getColor(R.color.orange));
        } else if (Vtable.getDate().contains("Donnerstag")) {
            base.setTint(getResources().getColor(R.color.green));
            stroke.setTint(getResources().getColor(R.color.green));
        } else if (Vtable.getDate().contains("Freitag")) {
            base.setTint(getResources().getColor(R.color.pink));
            stroke.setTint(getResources().getColor(R.color.pink));
        } else {
            base.setTint(getResources().getColor(R.color.purple));
            stroke.setTint(getResources().getColor(R.color.purple));
        }
        label.setBackground(base);
        mainScroll.setBackground(stroke);
    }

    public void addTableItem(Vertretung vt, boolean extendet) {

        String tmp = "";
        String tmp1 = vt.getStunde() + ": ";
        String tmp2 = vt.getKlasse() + ": ";

        if (vt.getFach().equals("---")) {
            tmp = tmp + vt.getStattFach() + " " + MainActivity.instance.getString(R.string.noClass);
        } else if (vt.getArt().equals("Entfall") || vt.getRaum().equals("---")) {
            tmp = tmp + vt.getStattFach() + " " + MainActivity.instance.getString(R.string.noClass);
        } else {
            if (extendet) {
                tmp = tmp + vt.getFach() + " in " + vt.getRaum() + " statt " + vt.getStattFach() + " in " + vt.getStattRaum();
            } else {
                tmp = tmp + vt.getFach() + " in " + vt.getRaum();
            }
        }
        if (!vt.getVertretungstext().equals("") && extendet) {
            tmp = tmp + " (" + vt.getVertretungstext() + ")";

        }
        addListItem(tmp, tmp1, tmp2, Client.instance.VertretungSize, Client.instance.VertretungRGB);
    }

    public void addListItem(String text, Integer textSize, boolean multiText) {

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
        Client.print("text: " + text);
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
