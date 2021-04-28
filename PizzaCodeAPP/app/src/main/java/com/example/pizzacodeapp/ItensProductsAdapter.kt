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
        private val productsSize: Int,
        private val callback: (nameItem: String,
                               description: String,
                               price: String,
                               nStock: String,
                               tipoProducts: String)-> Unit,
        private val longClickFun: (nameItem: String,
                                   description: String,
                                   price: String,
                                   nStock: String,
                                   tipoProducts: String) -> Unit

): RecyclerView.Adapter<ItensProductsAdapter.VH>(){

    private var nameItem : String = ""
    private var description : String = ""
    private var price : String = ""
    private var nStock : String = ""
    private var tipoProducts : String = ""

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

            val produto = produtos[vh.adapterPosition]

            reference!!.child("products/${produto}")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            nameItem = dataSnapshot.child("nameItem").value.toString()
                            description = dataSnapshot.child("description").value.toString()
                            price = dataSnapshot.child("price").value.toString()
                            nStock = dataSnapshot.child("nstock").value.toString()
                            tipoProducts = dataSnapshot.child("tipoProducts").value.toString()
                            callback( nameItem, description, price, nStock, tipoProducts )
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
        }

        vh.itemView.setOnLongClickListener {
            Log.v("I", "LONGPRESSSING")

            val produto = produtos[vh.adapterPosition]

            reference!!.child("products/${produto}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val nameItem = dataSnapshot.child("nameItem").value.toString()
                        val description = dataSnapshot.child("description").value.toString()
                        val price = dataSnapshot.child("price").value.toString()
                        val nStock = dataSnapshot.child("nstock").value.toString()
                        val tipoProducts = dataSnapshot.child("tipoProducts").value.toString()
                        longClickFun( nameItem, description, price, nStock, tipoProducts)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            true
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