package de.thu.recipebook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
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
    private FavoritesDbHelper dbHelper;

    public RecipeListAdapter(FavoritesDbHelper dbHelper) {
        this.dbHelper = dbHelper;
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

        ImageView imageView = view.findViewById(R.id.image_view_favorite);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(FavoritesDbHelper.FAVORITES_TABLE, new String[]{BaseColumns._ID},
                BaseColumns._ID + " = ?", new String[]{data.get(i).getId()}, null, null, null);
        if (c.getCount() > 0) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    public void setData(List<Recipe> data) {
        this.data = data;
    }
}
