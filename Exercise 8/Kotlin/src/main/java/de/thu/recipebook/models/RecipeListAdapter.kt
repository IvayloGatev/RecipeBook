package de.thu.recipebook.models

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import de.thu.recipebook.R
import de.thu.recipebook.databases.FavoritesDbHelper
import de.thu.recipebook.models.CountryCollection.getImageResourceName

class RecipeListAdapter(private val favoritesDbHelper: FavoritesDbHelper) : BaseAdapter() {
    private var data: List<Recipe>

    init {
        data = ArrayList()
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(i: Int): Any {
        return data[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        val context = viewGroup.context
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_view_item_recipe, null, false)
        }

        val country = data[i].country
        val imageResourceName = getImageResourceName(country)
        val imageId = context.resources.getIdentifier(
            imageResourceName,
            "drawable", context.packageName
        )
        view?.findViewById<ImageView>(R.id.image_view_country)?.setImageResource(imageId)

        val textView = view?.findViewById<TextView>(R.id.text_view_recipe_list)
        val name = data[i].name
        textView?.text = name

        val favoriteImageView = view!!.findViewById<ImageView>(R.id.image_view_favorite)
        val db: SQLiteDatabase = favoritesDbHelper.getReadableDatabase()
        val c = db.query(
            FavoritesDbHelper.FAVORITES_TABLE, arrayOf(BaseColumns._ID),
            BaseColumns._ID + " = ?", arrayOf(data[i].id), null, null, null
        )
        if (c.count > 0) {
            favoriteImageView.visibility = View.VISIBLE
        } else {
            favoriteImageView.visibility = View.INVISIBLE
        }


        return view!!
    }

    fun setData(data: List<Recipe>) {
        this.data = data
    }
}