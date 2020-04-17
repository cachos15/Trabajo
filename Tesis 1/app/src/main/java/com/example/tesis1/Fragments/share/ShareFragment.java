package com.example.tesis1.Fragments.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.tesis1.Activities.Login;
import com.example.tesis1.Activities.MenuPrincipal;
import com.example.tesis1.DataBase.BusquedaDispositivo_Request;
import com.example.tesis1.DataBase.Login_Request;
import com.example.tesis1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShareFragment extends Fragment {

    private ProgressBar pg_progreso;
    private ListView lv_listaDispositivos;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<String>();

    private Login login = new Login();
    private String usuario = login.datoUsuario();

    private boolean ejecutarHilo=true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_share, container, false);

        pg_progreso             = root.findViewById(R.id.dispsitivoProgreso);
        lv_listaDispositivos    = root.findViewById(R.id.dispositivoLista);

        Hilo_busqueda hilo_busqueda = new Hilo_busqueda();
        hilo_busqueda.start();

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_agregar_dispositivo,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class Hilo_busqueda extends Thread
    {
        @Override
        public void run() {
            super.run();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ejecutarHilo=true;
                    final Hilo_progreso hilo_progreso = new Hilo_progreso();
                    hilo_progreso.start();
                    hilo_progreso.run();

                    Response.Listener<String> respuesta = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray json_respuesta = new JSONArray(response);
                                int tamaño = json_respuesta.getInt(0);

                                for (int i=1; i<=tamaño; i++)
                                {
                                    JSONObject json = new JSONObject(json_respuesta.getString(i));
                                    String res = json.getString("modeloDispositivo");
                                    String res_2 = json.getString("fechaMod");
                                    arrayList.add(res+" "+res_2+" "+".");
                                    arrayAdapter = new ArrayAdapter<String>(getActivity(),
                                            android.R.layout.simple_list_item_1, arrayList);
                                    lv_listaDispositivos.setAdapter(arrayAdapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ejecutarHilo=false;
                            hilo_progreso.run();
                        }
                    };

                    BusquedaDispositivo_Request busqueda =
                            new BusquedaDispositivo_Request(usuario, respuesta);

                    RequestQueue cola = Volley.newRequestQueue(getActivity());
                    cola.add(busqueda);
                }
            });
        }
    }

    private class Hilo_progreso extends Thread
    {
        @Override
        public void run() {
            super.run();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ejecutarHilo)
                    {
                        pg_progreso.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        pg_progreso.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }
}