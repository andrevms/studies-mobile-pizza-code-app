package com.example.pizzacodeapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ItensProductsClientAdapter (
        private val produtos: List<String>,
        private val reference: DatabaseReference?,
        private val productsSize: Int,
        private val addPedido: (nameItem: String)-> Unit,
        private val message: (s: String)-> Unit
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

            val produto = produtos[vh.adapterPosition]

            reference!!.child("products/${produto}")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            val numStock = dataSnapshot.child("nstock").value.toString().toInt()
                            if (numStock >= 1){
                                val nameItem = dataSnapshot.child("nameItem").value.toString()
                                addPedido(nameItem)
                            }else{
                                message("Esse produto não se encontra em stock")
                            }

                        }
                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
        }

        return vh
    }

    override fun onBindViewHolder(holder: ItensProductsAdapter.VH, position: Int) {
        reference!!.child("products")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var i = 0
                        for (node1 in dataSnapshot.children) {
                            Log.v("I", node1.child("nameItem").getValue().toString())
                            if (position == i) {
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

    override fun getItemCount(): Int = productsSize
}