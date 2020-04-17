package com.example.tesis1.Fragments.bluetoothFragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tesis1.Activities.Login;
import com.example.tesis1.Bluetooth.ConexionBluetooth;
import com.example.tesis1.Estilos_listas.EstiloLista1;
import com.example.tesis1.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;

public class BluetoothFragment extends Fragment {

    private BluetoothAdapter bth_adapter;

    private ArrayList<String> listaBluetooth = new ArrayList<String>();
    private ArrayList<String> listaBluetooth_mac = new ArrayList<String>();

    private Button btn_buscarDispositivos;
    private ListView l_vw_bluetooth;
    private ProgressBar pg_progreso;

    private boolean ejecutar_hilo=true, buscandoDispositivos;

    private Login DatosLogin = new Login();
    String DatosUsuario = DatosLogin.datos();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        btn_buscarDispositivos  =root.findViewById(R.id.bluetooth_buscar);
        l_vw_bluetooth          =root.findViewById(R.id.bluetooth_lista);
        pg_progreso             =root.findViewById(R.id.bluetooth_progreso);

        bth_adapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        getActivity().registerReceiver(receiver, filter);

        if (!bth_adapter.isEnabled()){
            encenderbluetooth();
        }
        else
        {
            buscarDispositivos();
        }

        btn_buscarDispositivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarDispositivos();
            }
        });

        l_vw_bluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                conectarDispositivo(position);
            }
        });

        return root;
    }

    private void conectarDispositivo(int position)
    {
        if (!buscandoDispositivos)
        {
            BluetoothDevice Dispositivo = bth_adapter.getRemoteDevice(listaBluetooth_mac.get(position));
            BluetoothSocket bth_Socket  = null;

            try {
                bth_Socket = Dispositivo.createRfcommSocketToServiceRecord(UUID.
                        fromString("0001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                Toast.makeText(getActivity(), "error 1", Toast.LENGTH_LONG ).show();
            }

            try {
                bth_Socket.connect();
            }catch (IOException connectException){
                Toast.makeText(getActivity(), "error 2", Toast.LENGTH_LONG ).show();
            }

            if (!bth_Socket.isConnected())
            {
                try {
                    bth_Socket.connect();
                } catch (IOException connectException) {
                    Toast.makeText(getActivity(), "error 3", Toast.LENGTH_LONG ).show();
                }
            }
            else
            {
                ConexionBluetooth conexionBluetooth = new ConexionBluetooth(bth_Socket);
                conexionBluetooth.start();
                conexionBluetooth.Envio(DatosUsuario);
                try {
                    bth_Socket.close();
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "error 4", Toast.LENGTH_LONG ).show();
                }
            }
        }
        else
        {
            Toast.makeText(getActivity(), "Buscando dispositivos, por favor espere", Toast.LENGTH_LONG ).show();
        }
    }

    private void encenderbluetooth()
    {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 100);
    }

    private void buscarDispositivos()
    {
        if (!bth_adapter.isEnabled()){
            encenderbluetooth();
        }
        else
        {
            if(bth_adapter.isDiscovering())
            {
                Toast.makeText(getActivity(), "Ya se esta detectando dispositivos", Toast.LENGTH_LONG ).show();
            }
            else
            {
                bth_adapter.startDiscovery();

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100)
        {
            if (resultCode==RESULT_CANCELED)
            {
                Toast.makeText(getActivity(), "Por favor encienda el Bluetooth", Toast.LENGTH_LONG ).show();
            }
            else
            {
                buscarDispositivos();
            }
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Hilo_progreso hilo_progreso = new Hilo_progreso();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                limpiarLista();
                ejecutar_hilo=true;
                hilo_progreso.start();
                hilo_progreso.run();
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listaBluetooth.add(device.getName());
                listaBluetooth_mac.add(device.getAddress());
                EstiloLista1 estilo = new EstiloLista1(getActivity(),listaBluetooth);
                l_vw_bluetooth.setAdapter(estilo);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                ejecutar_hilo=false;
                hilo_progreso.run();
            }
        }
    };

    @Override
    public void onDestroy()
    {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void limpiarLista()
    {
        listaBluetooth.clear();
        listaBluetooth_mac.clear();
    }

    private class Hilo_progreso extends Thread
    {
        @Override
        public void run() {
            super.run();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ejecutar_hilo)
                    {
                        pg_progreso.setVisibility(View.VISIBLE);
                        buscandoDispositivos=true;
                    }
                    else
                    {
                        pg_progreso.setVisibility(View.INVISIBLE);
                        buscandoDispositivos=false;
                    }
                }
            });
        }
    }

}