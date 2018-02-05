package com.example.angelus.firebaseandroidangel;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

import java.util.ArrayList;



public class Adapter extends BaseAdapter{
    private Context contexto;
    private ArrayList<Producto> misProductos;

    public Adapter(Context contexto, ArrayList<Producto> misProductos) {
        this.contexto = contexto;
        this.misProductos = misProductos;
    }

    @Override
    public int getCount() {//el metododo getCount va a repetir el metodo de abajo del tot getView tantas veces sea el tamaño
        return misProductos.size();//El tamaño va a ser el que nos de el ArrayList que recibe
    }

    @Override
    public Object getItem(int i) {
        return misProductos.get(i);//nos devuelve la posicion en ese momento
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Producto miProducto = (Producto)getItem(i);


        view = LayoutInflater.from(contexto).inflate(R.layout.items, null);

        TextView nombre = (TextView) view.findViewById(R.id.textViewNombreProductoListView);
        TextView descripcion = (TextView) view.findViewById(R.id.textViewDescripcionProductoListView);
        TextView categoria = (TextView) view.findViewById(R.id.textViewCategoriaProductoListView);
        TextView precio = (TextView) view.findViewById(R.id.textViewPrecioProductoListView);
        ImageView miImagen = (ImageView)view.findViewById(R.id.miImagenAdapter);

        /*//Y ALGUNOS ELEMENTOS QUE TENGO QUE INSTANCIAR PARA SETCLICKABLE y SETFOCUSABLE FALSE, SI NO NO ME DEJA CLICKAR
        LinearLayout lin1 = (LinearLayout)view.findViewById(R.id.layou1);
        LinearLayout lin2 = (LinearLayout)view.findViewById(R.id.layou2);
        LinearLayout lin3 = (LinearLayout)view.findViewById(R.id.layou3);
        LinearLayout lin4 = (LinearLayout)view.findViewById(R.id.layou4);
        LinearLayout lin5 = (LinearLayout)view.findViewById(R.id.layou5);
        ScrollView scr1 = (ScrollView)view.findViewById(R.id.scroll1) ;
        ScrollView scr2 = (ScrollView)view.findViewById(R.id.scroll2);

        nombre.setClickable(false);
        nombre.setFocusable(false);
        descripcion.setClickable(false);
        descripcion.setFocusable(false);
        categoria.setClickable(false);
        categoria.setFocusable(false);
        precio.setClickable(false);
        precio.setFocusable(false);
        lin1.setClickable(false);
        lin1.setFocusable(false);
        lin2.setClickable(false);
        lin2.setFocusable(false);
        lin3.setClickable(false);
        lin3.setFocusable(false);
        lin4.setClickable(false);
        lin4.setFocusable(false);
        lin5.setClickable(false);
        lin5.setFocusable(false);
        scr1.setClickable(false);
        scr2.setFocusable(false);*/



        //Ponemos colores bonicos
        nombre.setTextColor(Color.BLACK);
        descripcion.setTextColor(Color.BLACK);
        categoria.setTextColor(Color.BLACK);
        precio.setTextColor(Color.BLACK);






        nombre.setText("Nombre: "+miProducto.getNombre());
        descripcion.setText("Descripción: "+miProducto.getDescripcion());
        categoria.setText("Categoría: "+miProducto.getCategoria());
        precio.setText("Precio: "+miProducto.getPrecio());

        if(miProducto.getMiImagen() != null) {
            //miImagen = miProducto.getMiImagen();

            miImagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
            miImagen.setImageDrawable(miProducto.getMiImagen().getDrawable());


        }

        return view;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
