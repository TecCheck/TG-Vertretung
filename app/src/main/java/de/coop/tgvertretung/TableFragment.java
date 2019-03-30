package de.coop.tgvertretung;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
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
import java.util.Date;

import de.sematre.tg.Table;
import de.sematre.tg.TableEntry;

public class TableFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    static Table table = null;
    private TextView label = null;
    private LinearLayout mainLayout = null;
    private ConstraintLayout mainScroll = null;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TableFragment newInstance(int sectionNumber) {
        Client.printMethod("newInstance");
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public static ArrayList<TableEntry> checkTableEntries(ArrayList<TableEntry> entries) {
        ArrayList<TableEntry> filtered = new ArrayList<>();
        for (TableEntry entry : entries) {
            if (!(entry.getSchoolClass() == "" || entry.getSchoolClass() == null)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    public static ArrayList<TableEntry> filterTable(ArrayList<TableEntry> entries, String filter) {
        ArrayList<TableEntry> filtered = new ArrayList<>();
        for (TableEntry entry : entries) {
            if (entry.getSchoolClass().toLowerCase().contains(filter.toLowerCase())) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Client.printMethod("onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        mainLayout = rootView.findViewById(R.id.MainLayout);
        //Client.print("MainLayoutID: " + R.id.MainLayout);
        //Client.print("mainLayout: " + mainLayout);
        label = rootView.findViewById(R.id.label);
        mainScroll = rootView.findViewById(R.id.MainScroll);
        mainScroll.setVerticalScrollBarEnabled(false);
        mainScroll.setHorizontalScrollBarEnabled(false);

        int asn = getArguments().getInt(ARG_SECTION_NUMBER);
        table = Settings.settings.timeTable.getTables().get(asn);
        String week = MainActivity.instance.getString(R.string.week) + " ";
        if(Settings.settings.showAB){
            if(table.getWeek().getLetter().toLowerCase().equals("a") || table.getWeek().getLetter().toLowerCase().equals("c"))
                week = week + "A";
            else
                week = week + "B";
        }else{
            week = week + table.getWeek().getLetter();
        }
        String s = Client.getFormattedDate(table.getDate(), true, false) + " " + week;
        label.setText(s);
        //Client.print("=> Neuer Plan! (" + table.getDate() + ")");
        //Client.print(table.toString());
        setColor(table.getDate());

        viewUI();
        return rootView;
    }

    public void viewUI() {
        Client.printMethod("viewUI");

        ArrayList<TableEntry> tableEntries = table.getTableEntries();

        if (Settings.settings.useFilter) {
            tableEntries = filterTable(tableEntries, Settings.settings.filter);
        }
        //tableEntries = checkTableEntries(tableEntries);
        if (tableEntries.isEmpty()) {
            Client.print("Leer!");
            addListItem(MainActivity.instance.getString(R.string.nothing), Client.instance.NothingSize, false);
        } else {
            Client.print("");
            Client.print("tableEntries for " + label.getText() +": ");
            Client.print("");
            for (TableEntry entry : tableEntries) {
                Client.print(entry.getSchoolClass() + ", " + entry.getTime());
                addTableItem(entry, Settings.settings.extended, Settings.settings.showText);
            }
        }
        Client.print("");
        Client.print("");
    }

    @SuppressLint("NewApi")
    private void setColor(Date date) {
        Client.printMethod("setColor");
        Drawable base = MainActivity.instance.getDrawable(R.drawable.colorgrade_base);
        Drawable stroke = MainActivity.instance.getDrawable(R.drawable.colorgrade_stroke);

        if (date.getDay() == 1) {
            base.setTint(getResources().getColor(R.color.yellow));
            stroke.setTint(getResources().getColor(R.color.yellow));
        } else if (date.getDay() == 2) {
            base.setTint(getResources().getColor(R.color.blue));
            stroke.setTint(getResources().getColor(R.color.blue));
        } else if (date.getDay() == 3) {
            base.setTint(getResources().getColor(R.color.green));
            stroke.setTint(getResources().getColor(R.color.green));
        } else if (date.getDay() == 4) {
            base.setTint(getResources().getColor(R.color.orange));
            stroke.setTint(getResources().getColor(R.color.orange));
        } else if (date.getDay() == 5) {
            base.setTint(getResources().getColor(R.color.pink));
            stroke.setTint(getResources().getColor(R.color.pink));
        } else {
            base.setTint(getResources().getColor(R.color.purple));
            stroke.setTint(getResources().getColor(R.color.purple));
        }
        label.setBackground(base);
        mainScroll.setBackground(stroke);
    }

    public void addTableItem(TableEntry entry, boolean extended, boolean showText) {
        Client.printMethod("addTableItem");

        String infoRow = "";
        String timeRow = entry.getTime() + ": ";
        String classRow = entry.getSchoolClass() + ": ";

        if (entry.getType().equals("Entfall") || entry.getReplacementRoom().equals("---") || entry.getReplacementSubject().equals("---")) {
            infoRow = infoRow + entry.getSubject() + " " + MainActivity.instance.getString(R.string.noClass);
        } else {
            if (extended) {
                infoRow = infoRow + entry.getReplacementSubject() + " in " + entry.getReplacementRoom() + " statt " + entry.getSubject() + " in " + entry.getRoom();
            } else {
                infoRow = infoRow + entry.getReplacementSubject() + " in " + entry.getReplacementRoom();
            }
        }
        if ((!entry.getText().equals("")) && showText) {
            infoRow += " (" + entry.getText() + ")";
        }
        addListItem(infoRow, timeRow, classRow, Client.instance.VertretungSize, Client.instance.VertretungRGB);
    }

    public void addListItem(String text, Integer textSize, boolean multiText) {
        Client.printMethod("addListItem");

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

        Client.printMethod("addListItem");

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
