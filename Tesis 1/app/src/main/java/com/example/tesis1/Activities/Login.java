package com.example.tesis1.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.tesis1.DataBase.Login_Request;
import com.example.tesis1.R;

import org.json.JSONException;
import org.json.JSONObject;
//se realiza herencia de la clase AppCompatActivity para correr el código mejor
public class Login extends AppCompatActivity {

    private Button btn_registro, btn_login;
    private EditText ed_txt_usuario, ed_txt_contraseña;
    private ProgressBar pg_login;
    private ImageButton im_verContraseña;
    static String usuario, contraseña, identificacionDispositivo;
    private boolean ejecutar_hilo=true, verContraseña=true;

    @Override //sobreescribir metodo onCreate de AppComparActivity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); //Llamar el layout

        unidadGrafica(); //Metodo para implementar findview

        solicitudPermisos(); //metodo para solicitar al dispositivo permisos

        identificacionDispositivo   = Settings.Secure.getString(getApplicationContext()
                .getContentResolver(), Settings.Secure.ANDROID_ID);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario     = ed_txt_usuario.getText().toString();
                contraseña  = ed_txt_contraseña.getText().toString();

                ingreso();
            }
        });

        btn_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Registro.class);
                startActivity(i);
            }
        });

        im_verContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verContraseña();
            }
        });

    }

    private void unidadGrafica()
    {
        btn_registro        =(Button)findViewById(R.id.log_register);
        btn_login           =(Button)findViewById(R.id.log_login);

        ed_txt_usuario      =(EditText)findViewById(R.id.log_user);
        ed_txt_contraseña   =(EditText)findViewById(R.id.log_password);

        pg_login            =(ProgressBar)findViewById(R.id.log_progreso);

        im_verContraseña    =(ImageButton)findViewById(R.id.log_verPass);

    }

    private void solicitudPermisos()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        }
    }

    private boolean verificacionUsuario()
    {
        if (usuario.equals(""))
        {
            Toast.makeText(getBaseContext(), "Usuario obligatorio", Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean verificacionContraseña()
    {
        if(contraseña.equals(""))
        {
            Toast.makeText(getBaseContext(), "Contraseña obligatoria", Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    private void ingreso()
    {
        if(verificacionUsuario() && verificacionContraseña())
        {
            Hilo_login hilo_login = new Hilo_login();
            hilo_login.start();
        }
    }

    private void verContraseña()
    {
        if (verContraseña)
        {
            ed_txt_contraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            im_verContraseña.setImageResource(R.drawable.ic_visibility_off);
            verContraseña=false;
        }
        else
        {
            ed_txt_contraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            im_verContraseña.setImageResource(R.drawable.ic_visibility);
            verContraseña=true;
        }
    }

    public String datos()
    {
        String DatosPermiso = identificacionDispositivo+","+usuario+"?";
        return DatosPermiso;
    }

    public String datoUsuario()
    {
        String datoUsuario = usuario;
        return datoUsuario;
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
                    ejecutar_hilo=true;
                    final Hilo_login_ProgressBar hilo_progressBar = new Hilo_login_ProgressBar();
                    hilo_progressBar.start();
                    hilo_progressBar.run();

                    Response.Listener<String> respuesta = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject json_respuesta = new JSONObject(response);
                                String respuestaRegistro = json_respuesta.getString("login");
                                Boolean respuestaDispositivo;
                                if (respuestaRegistro.equals("login")) {
                                    respuestaDispositivo = json_respuesta.getBoolean("identificacionDispositivo");
                                }
                                else {
                                    respuestaDispositivo = false;
                                }
                                switch (respuestaRegistro)
                                {
                                    case "contraseña":
                                        Toast.makeText(getBaseContext(), "Contraseña erronea", Toast.LENGTH_LONG).show();
                                        ed_txt_contraseña.setText("");
                                        break;
                                    case "noValido":
                                        Toast.makeText(getBaseContext(), "Dispositivo ya registrado con otro usuario"
                                                , Toast.LENGTH_LONG).show();
                                        break;

                                    case "usuario":
                                        Toast.makeText(getBaseContext(), "Usuario no existe", Toast.LENGTH_LONG).show();
                                        ed_txt_usuario.setText("");
                                        ed_txt_contraseña.setText("");
                                        break;
                                    case "login":
                                        Intent i = new Intent(Login.this, MenuPrincipal.class);
                                        startActivity(i);
                                        Login.this.finish();
                                        break;

                                    default:
                                        Toast.makeText(getBaseContext(), "Fallo al ingresar", Toast.LENGTH_LONG).show();
                                }
                                ejecutar_hilo=false;
                                hilo_progressBar.run();
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


}
