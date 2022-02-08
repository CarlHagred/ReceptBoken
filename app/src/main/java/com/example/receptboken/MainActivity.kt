package com.example.receptboken

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receptboken.databinding.ActivityMainBinding
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var recipeList: MutableList<Recipe>
    lateinit var adapter: RecyclerAdapter
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Paper.init(this);
        recipeList = mutableListOf()
        loadData()
        adapter = RecyclerAdapter(recipeList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun loadData(){
        val l = Paper.book().read<List<Recipe>>("recipe", ArrayList())
        val convert: MutableList<Recipe> = l as MutableList<Recipe>
        if (convert.isEmpty()) return
        for(index in convert){
            Log.d("test", index.image.get)
            //recipeList.add(Recipe(index.title, index.instructions, index.image))
        }
        adapter.notifyItemInserted(recipeList.size - 1)
       // recipeList = convert
    }

    fun saveData() {
        Paper.book().write("recipe", recipeList);
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show()
    }

    fun onAdd(view: View) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
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
            saveData()
        }
    }
}