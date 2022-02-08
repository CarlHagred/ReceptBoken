package com.example.receptboken

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receptboken.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var recipeList: ArrayList<Recipe>
    lateinit var adapter: RecyclerAdapter
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recipeList = arrayListOf()
        adapter = RecyclerAdapter(recipeList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        loadData()
    }

    fun setList() {
        val gson = Gson()
        val saveList: ArrayList<RecipeSave> = arrayListOf()
        for (item in recipeList){
            saveList.add(RecipeSave(item.title, item.instructions, encodeTobase64(item.image)))
        }
        val json: String = gson.toJson(saveList)
        set("recipe", json)
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show()
    }

    operator fun set(key: String?, value: String?) {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString(key, value)
        }.apply()
    }

    fun loadData(){
        val gson = Gson()
        var productFromShared: List<RecipeSave?>? = ArrayList()
        val sharedPref = applicationContext.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val jsonPreferences = sharedPref.getString("recipe", "")
        val type = object : TypeToken<List<RecipeSave?>?>() {}.type
        productFromShared = gson.fromJson<List<RecipeSave?>>(jsonPreferences, type)

        if (productFromShared.isEmpty()) return

        for(item in productFromShared){
            if (item != null) {
                recipeList.add(Recipe(item.title, item.instructions, decodeBase64(item.image)))
            }
        }
        adapter.notifyItemInserted(recipeList.size - 1)
    }

    fun encodeTobase64(image: Bitmap): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        val imageEncoded: String = Base64.encodeToString(b, Base64.DEFAULT)
        return imageEncoded
    }

    fun decodeBase64(input: String): Bitmap {
        val decodedByte = Base64.decode(input, 0)
        return BitmapFactory
            .decodeByteArray(decodedByte, 0, decodedByte.size)
    }

    fun onAdd(view: View) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE && data != null){
            val title = binding.txtTitle.text.toString()
            val description = binding.txtDescription.text.toString()
            val image = data.extras!!.get("data") as Bitmap
            recipeList.add(Recipe(title, description, image))
            adapter.notifyItemInserted(recipeList.size - 1)
            setList()
        }
    }
}