package com.example.pizzacodeapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.Serializable

class SacolaComprarActivity : AppCompatActivity() {

    private var sacolaList : MutableList<String>? = null
    private var ref: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var recyclerVH : RecyclerView? = null
    private var productsSize : Int = 0
    private var adapter : SacolaAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sacola_comprar)

        sacolaList = intent.getSerializableExtra("LIST") as MutableList<String>

        findViewById<Button>(R.id.finishPurchaseB).setOnClickListener {

            if(sacolaList?.size!! > 0){
                setResult(Activity.RESULT_OK)
                finish()
            }
            setResult(1)
            finish()
        }

        recyclerVH = findViewById(R.id.recyclerViewSacola)
        productsSize = sacolaList?.size!!
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = SacolaAdapter(sacolaList!!, ref, productsSize)
        recyclerVH?.adapter = adapter
        val layoutManager = LinearLayoutManager(this)

        recyclerVH?.layoutManager = layoutManager

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val i = intent
        i.putExtra("LIST", sacolaList as Serializable);
        setResult(Activity.RESULT_CANCELED, i)
        finish()
    }


}