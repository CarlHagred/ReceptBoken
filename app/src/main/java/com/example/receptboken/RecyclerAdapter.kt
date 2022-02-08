package com.example.receptboken

import android.R.attr.bitmap
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream


class RecyclerAdapter(var recipe: List<Recipe>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.itemTitle.text = recipe[position].title
        holder.itemImage.setImageBitmap(recipe[position].image)
    }

    override fun getItemCount(): Int {
        return recipe.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var itemImage: ImageView
        var itemTitle: TextView

        init {
            itemImage = itemView.findViewById(R.id.itemImage)
            itemTitle = itemView.findViewById(R.id.item_title)

            itemView.setOnClickListener {
                val title = recipe[position].title
                val message = recipe[position].instructions
                val image = recipe[position].image
                showRecipe(itemView.context, title, message, image)
            }
        }
    }

    fun showRecipe(context: Context, title: String, message: String, image: Bitmap) {
        val bStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, bStream)
        val byteArray: ByteArray = bStream.toByteArray()

        val intent = Intent(context, RecipeActivity::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("image", byteArray)
        }
        context.startActivity(intent)
    }
}