package de.thu.recipebook.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import de.thu.recipebook.R
import de.thu.recipebook.models.CountryCollection.countries
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.runnables.SaveRecipeRunnable
import java.io.ByteArrayOutputStream
import java.lang.Exception

class SaveRecipeActivity : AppCompatActivity() {
    private var adapter: ArrayAdapter<String>? = null
    private var recipe: Recipe? = null
    private var image: ByteArray? = null
    private var chooseImage: ActivityResultLauncher<String>? = null

    private var runnable: SaveRecipeRunnable? = null

    private var nameEditText: EditText? = null
    private var countrySpinner: Spinner? = null
    private var ingredientsEditText: EditText? = null
    private var instructionsEditText: EditText? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_recipe)

        adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            countries
        )
        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        runnable = SaveRecipeRunnable(this)

        countrySpinner = findViewById(R.id.spinner_country)
        countrySpinner?.adapter = adapter

        nameEditText = findViewById(R.id.edit_text_name)
        countrySpinner = findViewById(R.id.spinner_country)
        ingredientsEditText = findViewById(R.id.edit_text_ingredients)
        instructionsEditText = findViewById(R.id.edit_text_instructions)

        imageView = findViewById(R.id.image_view_upload)
        imageView!!.setOnClickListener(View.OnClickListener { view: View? ->
            if (image != null) {
                image = null
                imageView!!.setImageBitmap(null)
                imageView!!.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        })

        chooseImage = registerForActivityResult(
            GetContent()
        ) { uri: Uri? ->
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                setImage(bitmap)

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.WEBP, 0, stream)
                val byteArray = stream.toByteArray()
                image = byteArray
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (intent.getSerializableExtra("recipe") != null) {
            recipe = intent.getSerializableExtra("recipe") as Recipe?
            nameEditText!!.setText(recipe!!.name)
            countrySpinner!!.setSelection(countries.indexOf(recipe!!.country))
            ingredientsEditText!!.setText(recipe!!.ingredients)
            instructionsEditText!!.setText(recipe!!.instructions)
            if (recipe!!.image != null) {
                image = recipe!!.image
                val bitmap = BitmapFactory.decodeByteArray(recipe!!.image, 0, recipe!!.image!!.size)
                setImage(bitmap)
            }
        }
    }

    fun saveButtonOnClick(view: View?) {
        var isValid = true
        if (!validate(nameEditText)) {
            isValid = false
        }
        if (!validate(ingredientsEditText)) {
            isValid = false
        }
        if (!validate(instructionsEditText)) {
            isValid = false
        }

        if (isValid) {
            var id: String? = null
            if (recipe != null) {
                id = recipe!!.id
            }

            recipe = Recipe(
                id, nameEditText!!.text.toString(), countrySpinner!!.selectedItem.toString(),
                ingredientsEditText!!.text.toString(), instructionsEditText!!.text.toString(), true
            )
            if (image != null) {
                recipe!!.image = image
            }

            Thread(runnable).start()
        }
    }

    fun selectImage(view: View?) {
        chooseImage!!.launch("image/*")
    }


    private fun validate(editText: EditText?): Boolean {
        if (editText!!.text.toString().isEmpty()) {
            editText.error = "This field can't be empty!"
            return false
        }
        return true
    }

    private fun setImage(bitmap: Bitmap) {
        imageView!!.setImageBitmap(bitmap)
        val scale = applicationContext.resources.displayMetrics.density
        imageView!!.layoutParams.height = (270 * scale + 0.5f).toInt()
    }

    fun getRecipe(): Recipe? {
        return recipe
    }
}