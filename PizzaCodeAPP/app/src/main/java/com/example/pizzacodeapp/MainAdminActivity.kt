package com.example.pizzacodeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzacodeapp.ItensProductsAdapter.*
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.awaitAll


class MainAdminActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var ref: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var recyclerVH : RecyclerView? = null
    private var productsSize : Int = 0
    private var adapter : ItensProductsAdapter? = null

    private var products = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)


        recyclerVH = findViewById(R.id.recyclerViewAdmin)
        ref.child("products")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        productsSize = dataSnapshot.childrenCount.toInt()
                        var i = 0
                        for (node1 in dataSnapshot.children) {
                            products.add(i, node1.child("nameItem").value.toString())
                            i++
                        }
                        initRecyclerView()
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
    }

    fun onMessageItemClick( nameItem: String,
                            description: String,
                            price: String,
                            nStock: String,
                            tipoProducts: String){

        val s = "Nome do Item ${nameItem}\n" +
                "Descrição ${description}\n" +
                "Preço ${price}\n" +
                "Número em stock ${nStock}\n" +
                "Tipo Do Produto ${tipoProducts}\n"
        Toast.makeText(this,s,Toast.LENGTH_LONG).show()
    }



    private fun initRecyclerView() {
        adapter = ItensProductsAdapter(products, ref, productsSize, this::onMessageItemClick)
        recyclerVH?.adapter = adapter
        val layoutManager = LinearLayoutManager(this)

        recyclerVH?.layoutManager = layoutManager

        initSwipeDelete()

    }

    fun initSwipeDelete(){

        val swipe = object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                //Remove Data
                ref.child("products").child(products[position]).removeValue()
                products.removeAt(position)
                adapter?.notifyItemRemoved(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipe)
        itemTouchHelper.attachToRecyclerView(recyclerVH)
    }


    //Funções do Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_admin, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == R.id.logoutButton){
            Log.v("I", "signOut")
            mAuth?.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else if(id == R.id.add_new_product){
            val intent = Intent(applicationContext, AddProductsAdmin::class.java)
            startActivity(intent)
            finish()
        }else if(id == R.id.see_stock){
            //TODO
        }
        return super.onOptionsItemSelected(item)
    }


}