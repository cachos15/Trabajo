package com.example.tesis1.Fragments.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.tesis1.DataBase.Prueba_Request;
import com.example.tesis1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private ListView lv_lista;

    private ProgressBar pg_prueba;

    private ArrayAdapter<String> arrayAdapter;

    private ArrayList<String> lista = new ArrayList<String>();

    private Boolean ejecutar_hilo = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        lv_lista = root.findViewById(R.id.lista);

        pg_prueba = root.findViewById(R.id.prueba_progreso);

        Hilo_cargar hilo = new Hilo_cargar();
        hilo.start();

        return root;
    }

    private class Hilo_cargar extends Thread
    {
        @Override
        public void run() {
            super.run();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final Hilo_progress hilo_progress = new Hilo_progress();
                    hilo_progress.start();
                    ejecutar_hilo=true;

                    Response.Listener<String> respuesta = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray json_respuesta = new JSONArray(response);
                                int tamaño = json_respuesta.getInt(0);

                                for (int i=1; i<=tamaño; i++)
                                {
                                    JSONObject json = new JSONObject(json_respuesta.getString(i));
                                    String res = json.getString("id");
                                    String res_2 = json.getString("id_usuario");
                                    String res_3 = json.getString("identificacionDispositivo");
                                    lista.add(res+" "+res_2+" "+res_3+".");
                                    arrayAdapter = new ArrayAdapter<String>(getActivity(),
                                            android.R.layout.simple_list_item_1, lista);
                                    lv_lista.setAdapter(arrayAdapter);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ejecutar_hilo=false;
                            hilo_progress.run();
                        }
                    };

                    Prueba_Request prueba = new Prueba_Request(respuesta);
                    RequestQueue cola = Volley.newRequestQueue(getActivity());
                    cola.add(prueba);
                }
            });

        }
    }

    private class Hilo_progress extends Thread
    {
        @Override
        public void run() {
            super.run();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ejecutar_hilo)
                    {
                        pg_prueba.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        pg_prueba.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }
}

