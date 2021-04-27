package com.example.pizzacodeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzacodeapp.ItensProductsAdapter.*
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainAdminActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var ref: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var recyclerVH : RecyclerView? = null
    private var productsSize : Int = 0

    private var products = mutableListOf<ItensProducts>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)


        recyclerVH = findViewById(R.id.recyclerViewAdmin)
        ref.child("products")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.v("I", "Entrando OnDataChange")
                        productsSize = dataSnapshot.childrenCount.toInt()
                        Log.v("I", "Pegando o ChidrenCount ${productsSize}")
                        initRecyclerView()
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
    }



    private fun initRecyclerView() {
       recyclerVH?.adapter = ItensProductsAdapter(products, ref, productsSize)
       val layoutManager = LinearLayoutManager(this)

        recyclerVH?.layoutManager = layoutManager

    }

    fun loadSizeProducts() {


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