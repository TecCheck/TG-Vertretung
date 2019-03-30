package de.coop.tgvertretung;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.sematre.tg.Table;

public class TableFragment2 extends Fragment {

    private static final String INDEX = "index";
    public static ArgbEvaluator evaluator = null;
    private Table table = null;

    public static TableFragment2 newInstance(int sectionNumber) {
        Client.printMethod("newInstance");
        TableFragment2 fragment = new TableFragment2();
        Bundle args = new Bundle();
        args.putInt(INDEX, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Client.printMethod("onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        //Get the Views
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main2, container, false);
        TextView label = rootView.findViewById(R.id.label);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);
        int index = getArguments().getInt(INDEX);

        //Filer the table if needed
        if(Settings.settings.useFilter){
            table = Utils.filterTable(Settings.settings.timeTable.getTables().get(index), Settings.settings.filter);
        }else {
            table = Settings.settings.timeTable.getTables().get(index);
        }

        if(evaluator == null){
            evaluator = new ArgbEvaluator();
        }

        //Show nothing if table is empty
        if(table.getTableEntries().isEmpty()){

            if(Settings.settings.rainbow){
                Utils.addRainbow(nothing);
            }else {
                nothing.setTextColor(Utils.getColor(table.getDate()));
            }
            nothing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            nothing.setVisibility(View.GONE);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.instance.getApplicationContext());
            RecyclerView.Adapter adapter = new TableEntryAdapter(table);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

        //Add a rainbow effect to the label
        if(Settings.settings.rainbow){
            Utils.addRainbow(label);
        }else {
            label.setTextColor(Utils.getColor(table.getDate()));
        }

        //Show the label
        label.setText(getLabelText());
        //label.getBackground().setTint(Utils.getColor(table.getDate()));

        return rootView;
    }

    private String getLabelText(){
        String week = MainActivity.instance.getString(R.string.week) + " ";
        if(Settings.settings.showAB){
            if(table.getWeek().getLetter().toLowerCase().equals("a") || table.getWeek().getLetter().toLowerCase().equals("c"))
                week = week + "A";
            else
                week = week + "B";
        }else{
            week = week + table.getWeek().getLetter();
        }
        return Client.getFormattedDate(table.getDate(), true, false) + " " + week;
    }
}
