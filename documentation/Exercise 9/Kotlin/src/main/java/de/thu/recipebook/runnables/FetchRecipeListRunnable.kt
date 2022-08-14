package de.thu.recipebook.runnables

import android.provider.Settings
import android.util.Log
import de.thu.recipebook.activities.RecipeListActivity
import de.thu.recipebook.models.RecipeCollection
import de.thu.recipebook.services.UpdateRecipeListNotifier
import org.json.JSONObject
import android.widget.Toast
import de.thu.recipebook.models.Recipe
import okhttp3.Credentials.basic
import kotlin.Throws
import okhttp3.OkHttpClient
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class FetchRecipeListRunnable(activity: RecipeListActivity?) : Runnable {
    private val recipeCollection: RecipeCollection = RecipeCollection.instance!!
    private val activity: RecipeListActivity? = activity
    private val notifier: UpdateRecipeListNotifier = UpdateRecipeListNotifier(activity!!)

    override fun run() {
        synchronized(this@FetchRecipeListRunnable) {
            if (activity != null) {
                var toastText = "Connection to the server couldn't be established."
                try {
                    val response = executeQuery(activity.getFilterName())
                    if (response.isSuccessful) {
                        val body = response.body!!.string()
                        val recipes = convertResponseBodyToRecipeList(body)
                        recipeCollection.setRecipes(recipes)
                        toastText = "The recipes were fetched successfully."
                        notifier.showOrUpdateNotification("Recipe list updated.")
                    } else {
                        toastText = JSONObject(response.body!!.string()).getString("message")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val finalToastText = toastText
                activity.runOnUiThread(Runnable {
                    activity.notifyDataSetChanged()
                    val toast = Toast.makeText(activity, finalToastText, Toast.LENGTH_SHORT)
                    toast.show()
                })
            }
        }
    }

    @Throws(IOException::class)
    private fun executeQuery(nameFilter: String?): Response {
        val url = "http://10.0.2.2:3000/api/recipes"
        val credentials = basic(
            Settings.Secure.getString(
                activity!!.contentResolver,
                Settings.Secure.ANDROID_ID
            ), ""
        )
        val client = OkHttpClient()
        val httpBuilder: HttpUrl.Builder = url.toHttpUrlOrNull()!!.newBuilder()
        if (nameFilter != null && nameFilter.isNotEmpty()) {
            httpBuilder.addQueryParameter("name", nameFilter)
        }
		
        val request: Request =
            Request.Builder().url(httpBuilder.build()).header("Authorization", credentials).build()
        return client.newCall(request).execute()
    }

    @Throws(Exception::class)
    private fun convertResponseBodyToRecipeList(body: String): MutableList<Recipe> {
        val recipes: MutableList<Recipe> = ArrayList()

        val recipesJsonArray = JSONArray(body)
        for (i in 0 until recipesJsonArray.length()) {
            val properties = recipesJsonArray.getJSONObject(i)
            Log.i("JSON", properties.toString())
            recipes.add(
                Recipe(
                    properties.getString("id"),
                    properties.getString("name"),
                    properties.getString("country"),
                    null, null, false
                )
            )
        }

        return recipes
    }
}