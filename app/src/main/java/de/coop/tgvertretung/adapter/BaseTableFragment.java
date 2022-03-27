package de.coop.tgvertretung.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Utils;

public class BaseTableFragment extends Fragment {

    public static final String ARG_INDEX = "index";
    protected int index = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        index = getArguments().getInt(ARG_INDEX);
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    protected void setupUi(View rootView, int colorIndex, boolean rainbow, String labelText, String labelText2, boolean showNothingLabel) {
        TextView label = rootView.findViewById(R.id.label);
        TextView label2 = rootView.findViewById(R.id.label2);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);

        if (rainbow) {
            Utils.addRainbow(label);
            Utils.addRainbow(label2);
            Utils.addRainbow(nothing);
        } else {
            int color = Utils.getDayColor(getContext(), colorIndex);
            label.setTextColor(color);
            label2.setTextColor(color);
            nothing.setTextColor(color);
        }

        label.setText(labelText);
        if (labelText2 != null) {
            label2.setVisibility(View.VISIBLE);
            label2.setText(labelText2);
        } else {
            label2.setVisibility(View.GONE);
        }

        nothing.setVisibility(showNothingLabel ? View.VISIBLE : View.GONE);
    }
}
