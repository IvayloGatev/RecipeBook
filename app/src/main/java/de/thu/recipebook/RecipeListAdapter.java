package de.thu.recipebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecipeListAdapter extends BaseAdapter {
    private List<Recipe> data;

    public RecipeListAdapter() {
        data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        String recipeName = data.get(i).getName();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_item_recipe, null, false);
        }

        TextView textView = view.findViewById(R.id.text_view_recipe_list);
        textView.setText(recipeName);
        return view;
    }

    public void setData(List<Recipe> data) {
        this.data = data;
    }
}
