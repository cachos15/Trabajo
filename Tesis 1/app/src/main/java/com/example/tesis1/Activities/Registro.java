package com.example.tesis1.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.tesis1.DataBase.Registro_Request;
import com.example.tesis1.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Registro extends AppCompatActivity {

    Button btn_cancel, btn_register;
    EditText ed_txt_primerNombre, ed_txt_segundoNombre, ed_txt_primerApellido, ed_txt_segundoApeliido,
             ed_txt_usuario, ed_txt_contraseña, ed_txt_confirmeContraseña;

    private String primerNombre;
    private String segundoNombre="";
    private String primerApellido;
    private String segundoApellido="";
    private String usuario;
    private String contraseña;
    private String confirmeContraseña;
    private String identificacionDispositivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        btn_cancel      =(Button) findViewById(R.id.reg_cancel);
        btn_register    =(Button)findViewById(R.id.reg_register);

        ed_txt_primerNombre         =(EditText)findViewById(R.id.reg_primernombre);
        ed_txt_segundoNombre        =(EditText)findViewById(R.id.reg_segundonombre);
        ed_txt_primerApellido       =(EditText)findViewById(R.id.reg_primerapellido);
        ed_txt_segundoApeliido      =(EditText)findViewById(R.id.reg_segundoapellido);
        ed_txt_usuario              =(EditText)findViewById(R.id.reg_Usuario);
        ed_txt_contraseña           =(EditText)findViewById(R.id.reg_contraseña);
        ed_txt_confirmeContraseña   =(EditText)findViewById(R.id.reg_confirmecontraseña);

        identificacionDispositivo   = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                primerNombre        = ed_txt_primerNombre.getText().toString();
                segundoNombre       = ed_txt_segundoNombre.getText().toString();
                primerApellido      = ed_txt_primerApellido.getText().toString();
                segundoApellido     = ed_txt_segundoApeliido.getText().toString();
                usuario             = ed_txt_usuario.getText().toString();
                contraseña          = ed_txt_contraseña.getText().toString();
                confirmeContraseña  = ed_txt_confirmeContraseña.getText().toString();

                if (primerNombre.equals(""))
                {
                    Toast.makeText(getBaseContext(), "Primer nombre obligatorio", Toast.LENGTH_LONG).show();
                }
                else if (primerApellido.equals(""))
                {
                    Toast.makeText(getBaseContext(), "Primer Apellido obligatorio", Toast.LENGTH_LONG).show();
                }
                else if (usuario.equals(""))
                {
                    Toast.makeText(getBaseContext(), "Usuario obligatorio", Toast.LENGTH_LONG).show();
                }
                else if (contraseña.equals(""))
                {
                    Toast.makeText(getBaseContext(), "Contraseña obligatoria", Toast.LENGTH_LONG).show();
                }
                else if (confirmeContraseña.equals(""))
                {
                    Toast.makeText(getBaseContext(), "Confirmar contraseña obligatorio", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if (confirmeContraseña.equals(contraseña))
                    {
                        Response.Listener<String> respuesta = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject json_respuesta = new JSONObject(response);
                                    String respuestaRegistro = json_respuesta.getString("resultado");
                                    if (respuestaRegistro.equals("usuario"))
                                    {
                                        Toast.makeText(getBaseContext(), "Usuario ya existente", Toast.LENGTH_LONG).show();
                                    }
                                    else if(respuestaRegistro.equals("identificacionDispositivo"))
                                    {
                                        Toast.makeText(getBaseContext(), "Identificación del telefono ya registrado", Toast.LENGTH_LONG).show();
                                    }
                                    else if(respuestaRegistro.equals("registro"))
                                    {
                                        Toast.makeText(getBaseContext(), "Registro exitoso", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(Registro.this, Login.class);
                                        startActivity(i);
                                        Registro.this.finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(getBaseContext(), "Registro fallido", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(Registro.this, Login.class);
                                        startActivity(i);
                                        Registro.this.finish();
                                    }
                                }
                                catch (JSONException e){
                                    e.getMessage();
                                }
                            }
                        };

                        Registro_Request registro = new Registro_Request(primerNombre,segundoNombre,primerApellido,segundoApellido,
                                identificacionDispositivo,usuario,contraseña,respuesta);

                        RequestQueue cola = Volley.newRequestQueue(Registro.this);
                        cola.add(registro);
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                    }
                }




            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Registro.this, Login.class);
                startActivity(i);
            }
        });
    }
}
