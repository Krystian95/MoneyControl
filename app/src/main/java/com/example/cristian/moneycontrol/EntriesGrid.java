package com.example.cristian.moneycontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;
import com.example.cristian.moneycontrol.database.Entry;

import java.util.List;

public class EntriesGrid extends BaseAdapter {

    private Context mContext;
    List<String> entries_id;
    private AppDatabase db;

    public EntriesGrid(Context c, List<String> entries_id) {
        mContext = c;
        this.entries_id = entries_id;
        this.db = AppDatabase.getAppDatabase(c);
    }

    @Override
    public int getCount() {
        return entries_id.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Entry entry = AppDatabase.getEntryById(db, entries_id.get(position));
        Category category = AppDatabase.getCategoryById(db, String.valueOf(entry.getIdCategory()));

        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        grid = new View(mContext);
        grid = inflater.inflate(R.layout.grid_element_entry, null);
        TextView eur = (TextView) grid.findViewById(R.id.total_expense_eur);
        TextView category_name = (TextView) grid.findViewById(R.id.category_name);
        category_name.setText(category.getName());
        TextView entry_amount = (TextView) grid.findViewById(R.id.total_expense);
        entry_amount.setText(Utils.formatNumber(entry.getAmount()));
        ImageView imageView = (ImageView) grid.findViewById(R.id.category_image);
        imageView.setImageResource(category.getIcon());

        if (category.getType().equals("expense")) {
            entry_amount.setTextColor(mContext.getResources().getColor(R.color.red));
            eur.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            entry_amount.setTextColor(mContext.getResources().getColor(R.color.green));
            eur.setTextColor(mContext.getResources().getColor(R.color.green));
        }

        return grid;
    }

}
