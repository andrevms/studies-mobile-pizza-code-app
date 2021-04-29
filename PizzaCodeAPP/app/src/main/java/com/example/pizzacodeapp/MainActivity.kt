package com.example.pizzacodeapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.Serializable

class   MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var ref: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var recyclerVH : RecyclerView? = null
    private var productsSize : Int = 0
    private var adapter : ItensProductsClientAdapter? = null

    private var sacolaList = mutableListOf<String>()
    private var products = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerVH = findViewById(R.id.recyclerViewMainActivity)
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

    private fun initRecyclerView() {
        adapter = ItensProductsClientAdapter(products, ref, productsSize, this::addPedido,
                this::message)
        recyclerVH?.adapter = adapter
        val layoutManager = LinearLayoutManager(this)

        recyclerVH?.layoutManager = layoutManager
    }

    private fun addPedido(nameItem: String) {
        sacolaList.add(nameItem)
        Toast.makeText(this,"Item ${nameItem} adiciona a sacola", Toast.LENGTH_LONG).show()
    }

    private fun message(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == R.id.logoutButton){
            mAuth?.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else if(id == R.id.sacola){
            val intent = Intent(applicationContext, SacolaComprarActivity::class.java)
            intent.putExtra("LIST", sacolaList as Serializable);

            startActivityForResult(intent, 10)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10 && resultCode == Activity.RESULT_CANCELED){
            val listaux = data?.getSerializableExtra("LIST") as MutableList<String>
            val position = data?.getIntExtra("INT", -1)
            if(position != -1){
                listaux.removeAt(position)
                Toast.makeText(this, "Item Removido da Sacola", Toast.LENGTH_LONG)
                        .show()
            }
            sacolaList = mutableListOf<String>()
            sacolaList.addAll(listaux)
        }

        if(requestCode == 10 && resultCode == 1){
            Toast.makeText(this, "Por Favor Selecione produto antes de enviar pedido", Toast.LENGTH_LONG)
                    .show()
        }


        if (requestCode == 10 && resultCode == Activity.RESULT_OK){
            sacolaList = mutableListOf<String>()
            Toast.makeText(this, "Pedido Enviado", Toast.LENGTH_LONG)
                    .show()
        }

    }
}