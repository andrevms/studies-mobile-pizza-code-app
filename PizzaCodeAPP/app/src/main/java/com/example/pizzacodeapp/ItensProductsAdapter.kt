package com.example.pizzacodeapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class ItensProductsAdapter (
    private var ctx: Context,
    private var produtos: List<ItensProducts>
        ): BaseAdapter() {

    override fun getCount(): Int = produtos.size

    override fun getItem(position: Int): Any = produtos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val produto = produtos[position]
        val row = LayoutInflater.from(ctx).inflate(R.layout.item_produto,
            parent,false)


        //row.findViewById(R.id.item_photo)
        //row.findViewById(R.id.item_name)
        //row.findViewById(R.id.item_description)
        //row.findViewById(R.id.item_price)

        return row
    }
}