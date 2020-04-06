package com.example.tesis1.DataBase;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegistroDispositivo_Request extends StringRequest {
    private static final String ruta = "https://diegochila.000webhostapp.com/PHP_Android/RegistroDispositivo.php";
    private Map<String,String> parametros;
    public RegistroDispositivo_Request(String identificacionDispositivo, String usuario,  Response.Listener<String> listener){
        super(Request.Method.POST, ruta, listener, null);
        parametros = new HashMap<>();
        parametros.put("identificacionDispositivo",identificacionDispositivo+"");
        parametros.put("usuario",usuario+"");
    }

    @Override
    protected Map<String, String> getParams(){
        return parametros;
    }
}
