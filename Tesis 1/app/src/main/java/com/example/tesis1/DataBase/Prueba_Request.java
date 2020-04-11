package com.example.tesis1.DataBase;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class Prueba_Request extends StringRequest {
    private static final String ruta = "https://diegochila.000webhostapp.com/PHP_Android/prueba.php";

    public Prueba_Request(Response.Listener<String> listener)
    {
        super(Request.Method.POST, ruta, listener, null);

    }
}
