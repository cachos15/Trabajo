package com.example.tesis1.Estilos_listas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tesis1.R;

import java.util.ArrayList;

public class EstiloLista1 extends BaseAdapter {
    private Context context;
    private ArrayList<String> lista;

    public EstiloLista1(Context context,  ArrayList<String> lista)
    {
        this.context    = context;
        this.lista      = lista;
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }

    @Override
    public Object getItem(int position) {
        return this.lista.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vista = convertView;

        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        vista = layoutInflater.inflate(R.layout.estilo_lista_bluetooth,null);

        String texto = lista.get(position);

        TextView tv_texto = (TextView)vista.findViewById(R.id.estilo1_texto);
        tv_texto.setText(texto);

        return vista;
    }
}
