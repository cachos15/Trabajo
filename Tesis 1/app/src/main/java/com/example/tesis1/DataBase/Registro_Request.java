package com.example.tesis1.DataBase;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Registro_Request extends StringRequest {
    private static final String ruta = "https://diegochila.000webhostapp.com/PHP_Android/Registro.php";
    private Map<String,String> parametros;
    public Registro_Request(String primerNombre, String segundoNombre, String primerApellido, String segundoApellido,
                            String identificacionDispositivo, String usuario, String contraseña, Response.Listener<String> listener){
        super(Request.Method.POST, ruta, listener, null);
        parametros = new HashMap<>();
        parametros.put("primerNombre",primerNombre+"");
        parametros.put("segundoNombre",segundoNombre+"");
        parametros.put("primerApellido",primerApellido+"");
        parametros.put("segundoApellido",segundoApellido+"");
        parametros.put("identificacionDispositivo",identificacionDispositivo+"");
        parametros.put("usuario",usuario+"");
        parametros.put("contraseña",contraseña+"");
    }

    @Override
    protected Map<String, String> getParams(){
        return parametros;
    }
}
