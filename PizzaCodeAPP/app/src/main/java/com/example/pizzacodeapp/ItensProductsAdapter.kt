package com.example.pizzacodeapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class ItensProductsAdapter(
        private val produtos: List<String>,
        private val reference: DatabaseReference?,
        private val productsSize: Int
): RecyclerView.Adapter<ItensProductsAdapter.VH>(){

    class VH(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtName: TextView? = itemView.findViewById(R.id.txtTitle)
        val txtDescription: TextView = itemView.findViewById(R.id.txtText)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val txtTipo: TextView = itemView.findViewById(R.id.tilCod)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_message,parent,false)
        val vh = VH(v)

        vh.itemView.setOnClickListener{
            Log.v("I", "${vh.adapterPosition}")
            Log.v("I", "${produtos}")

            //val produto = produtos[vh.adapterPosition]
            //callback(produto)
        }
        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.v("I","INIT GET DATABASE")
        reference!!.child("products")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var i = 0
                        for (node1 in dataSnapshot.children) {
                            Log.v("I", node1.child("nameItem").getValue().toString())
                            if (position == i) {
                                Log.v("I","dentro if ${i}")
                                holder.txtName?.setText(node1.child("nameItem").getValue().toString())
                                holder.txtDescription?.setText(node1.child("description").getValue().toString())
                                holder.txtPrice?.setText(node1.child("price").getValue().toString())
                                holder.txtTipo?.setText(node1.child("tipoProducts").getValue().toString())
                            }
                            i += 1
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
    }

    override fun getItemCount(): Int {
        Log.v("I", "Produto tamanho data = ${productsSize}")
        return productsSize
    }
}