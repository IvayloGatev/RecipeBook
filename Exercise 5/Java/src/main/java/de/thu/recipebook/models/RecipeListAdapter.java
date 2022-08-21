package de.thu.recipebook.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.thu.recipebook.R;

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
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_item_recipe, null, false);
        }

        ImageView countryImageView = view.findViewById(R.id.image_view_country);
        String country = data.get(i).getCountry();
        String imageResourceName = CountryCollection.getImageResourceName(country);
        int imageId = context.getResources().getIdentifier(imageResourceName,
                "drawable", context.getPackageName());
        countryImageView.setImageResource(imageId);

        TextView textView = view.findViewById(R.id.text_view_recipe_list);
        String name = data.get(i).getName();
        textView.setText(name);

        return view;
    }

    public void setData(List<Recipe> data) {
        this.data = data;
    }
}
