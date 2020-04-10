package com.example.tesis1.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.tesis1.DataBase.Login_Request;
import com.example.tesis1.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    Button btn_registro, btn_login;
    EditText ed_txt_usuario, ed_txt_contraseña;

    static String usuario, contraseña, identificacionDispositivo;

    ProgressBar pg_login;

    private boolean ejecutar_hilo=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_registro        =(Button)findViewById(R.id.log_register);
        btn_login           =(Button)findViewById(R.id.log_login);

        ed_txt_usuario      =(EditText)findViewById(R.id.log_user);
        ed_txt_contraseña   =(EditText)findViewById(R.id.log_password);

        pg_login            =(ProgressBar)findViewById(R.id.log_progreso);


        identificacionDispositivo   = Settings.Secure.getString(getApplicationContext()
                .getContentResolver(), Settings.Secure.ANDROID_ID);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario     = ed_txt_usuario.getText().toString();
                contraseña  = ed_txt_contraseña.getText().toString();

                if (usuario.equals(""))
                {
                    Toast.makeText(getBaseContext(), "Usuario obligatorio", Toast.LENGTH_LONG).show();
                }
                else if(contraseña.equals(""))
                {
                    Toast.makeText(getBaseContext(), "Contraseña obligatoria", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Hilo_login hilo_login = new Hilo_login();
                    hilo_login.start();
                }
            }
        });


        btn_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Registro.class);
                startActivity(i);
            }
        });
    }

    private class Hilo_login_ProgressBar extends Thread
    {
        @Override
        public void run() {
            super.run();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(ejecutar_hilo)
                    {
                        pg_login.setVisibility(View.VISIBLE);
                        btn_login.setEnabled(false);
                        btn_registro.setEnabled(false);
                    }
                    else
                    {
                        pg_login.setVisibility(View.INVISIBLE);
                        btn_login.setEnabled(true);
                        btn_registro.setEnabled(true);
                    }
                }
            });
        }
    }

    private class Hilo_login extends Thread
    {
        @Override
        public void run() {
            super.run();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Hilo_login_ProgressBar hilo_progressBar = new Hilo_login_ProgressBar();
                    hilo_progressBar.start();
                    ejecutar_hilo=true;

                    Response.Listener<String> respuesta = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject json_respuesta = new JSONObject(response);
                                String respuestaRegistro = json_respuesta.getString("login");
                                Boolean respuestaDispositivo = json_respuesta.getBoolean("identificacionDispositivo");

                                switch (respuestaRegistro)
                                {
                                    case "login":
                                        ejecutar_hilo=false;
                                        Intent i = new Intent(Login.this, MenuPrincipal.class);
                                        startActivity(i);
                                        Login.this.finish();
                                        break;

                                    case "noValido":
                                        ejecutar_hilo=false;
                                        Toast.makeText(getBaseContext(), "Dispositivo ya registrado con otro usuario"
                                                , Toast.LENGTH_LONG).show();
                                        break;

                                    case "usuario":
                                        ejecutar_hilo=false;
                                        Toast.makeText(getBaseContext(), "Usuario no existe", Toast.LENGTH_LONG).show();
                                        ed_txt_usuario.setText("");
                                        ed_txt_contraseña.setText("");
                                        break;

                                    case "contraseña":
                                        ejecutar_hilo=false;
                                        Toast.makeText(getBaseContext(), "Contraseña erronea", Toast.LENGTH_LONG).show();
                                        ed_txt_contraseña.setText("");
                                        break;

                                    default:
                                        ejecutar_hilo=false;
                                        Toast.makeText(getBaseContext(), "Fallo al ingresar", Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (JSONException e){
                                e.getMessage();
                            }
                        }
                    };

                    Login_Request login = new Login_Request(identificacionDispositivo,usuario, contraseña, respuesta);

                    RequestQueue cola = Volley.newRequestQueue(Login.this);
                    cola.add(login);
                }
            });
        }
    }

    public String Datos()
    {
        String DatosPermiso = identificacionDispositivo+","+usuario+"?";
        return DatosPermiso;
    }
}
