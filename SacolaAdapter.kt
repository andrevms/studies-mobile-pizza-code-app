package com.example.pizzacodeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class SacolaAdapter(
        private val produtos: MutableList<String>,
        private val reference: DatabaseReference?,
        private val productsSize: Int
): RecyclerView.Adapter<ItensProductsAdapter.VH>() {

    class VH(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtName: TextView? = itemView.findViewById(R.id.txtTitle)
        val txtDescription: TextView = itemView.findViewById(R.id.txtText)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val txtTipo: TextView = itemView.findViewById(R.id.tilCod)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItensProductsAdapter.VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_message,parent,false)
        val vh = ItensProductsAdapter.VH(v)

        vh.itemView.setOnClickListener{
        /*    val message = messages[vh.adapterPosition]
            callback(message) */
        }
        return vh
    }


    override fun onBindViewHolder(holder: ItensProductsAdapter.VH, position: Int) {
        val produto = produtos[position]
        reference!!.child("products/${produto}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    holder.txtName?.setText(dataSnapshot.child("nameItem").getValue().toString())
                    holder.txtDescription.setText(dataSnapshot.child("description").getValue().toString())
                    holder.txtPrice.setText(dataSnapshot.child("price").getValue().toString())
                    holder.txtTipo.setText(dataSnapshot.child("tipoProducts").getValue().toString())
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

    }

    override fun getItemCount(): Int {
        return productsSize
    }
}