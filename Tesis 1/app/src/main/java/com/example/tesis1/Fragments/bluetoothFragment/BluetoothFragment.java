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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.tesis1.Activities.Login;
import com.example.tesis1.Bluetooth.ConexionBluetooth;
import com.example.tesis1.Estilos_listas.EstiloLista1;
import com.example.tesis1.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static java.lang.Thread.sleep;

public class BluetoothFragment extends Fragment {

    private BluetoothFragmentViewModel homeViewModel;

    private BluetoothAdapter bth_adapter;

    private ArrayList<String> listaBluetooth = new ArrayList<String>();
    private ArrayList<String> listaBluetooth_mac = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapterBluetooth;

    Button btn_buscarDispositivos, btn_dispositivosEmparejados;
    ListView l_vw_bluetooth;

    private Login DatosLogin = new Login();

    String DatosUsuario = DatosLogin.Datos();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(BluetoothFragmentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        btn_buscarDispositivos      =root.findViewById(R.id.bluetooth_buscar);
        btn_dispositivosEmparejados =root.findViewById(R.id.bluetooth_dispositivos);
        l_vw_bluetooth              =root.findViewById(R.id.bluetooth_lista);

        bth_adapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        getActivity().registerReceiver(receiver, filter);

        //Solicitar bluetooth
        if (!bth_adapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 100);
        }
        else //Llenar la lista con dispositivos emparejados
        {
            DispositivosEmparejados();
        }

        //Buscar Dispositivos
        btn_buscarDispositivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bth_adapter.isEnabled()){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 100);
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
        });

        //Dispositivos emparejados
        btn_dispositivosEmparejados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DispositivosEmparejados();
            }
        });

        l_vw_bluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!bth_adapter.isDiscovering())
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
        });

        return root;
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
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action)
            {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    listaBluetooth.add(device.getName());
                    listaBluetooth_mac.add(device.getAddress());
//                    arrayAdapterBluetooth = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listaBluetooth);
//                    l_vw_bluetooth.setAdapter(arrayAdapterBluetooth);
                    EstiloLista1 estilo = new EstiloLista1(getActivity(),listaBluetooth);
                    l_vw_bluetooth.setAdapter(estilo);
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Toast.makeText(getActivity(), "Buscando Dispositivos", Toast.LENGTH_LONG ).show();
                    DispositivosEmparejados();
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(getActivity(), "Busqueda finalizada", Toast.LENGTH_LONG ).show();
                    break;
            }
        }
    };

    @Override
    public void onDestroy()
    {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void LimpiarLista()
    {
        listaBluetooth.clear();
        listaBluetooth_mac.clear();
        arrayAdapterBluetooth = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listaBluetooth);
        l_vw_bluetooth.setAdapter(arrayAdapterBluetooth);
    }

    public void DispositivosEmparejados()
    {
        LimpiarLista();
        Set<BluetoothDevice> pairedDevices = bth_adapter.getBondedDevices();
        if(pairedDevices.size()>0)
        {
            for (BluetoothDevice device : pairedDevices) {
                listaBluetooth.add(device.getName());
                listaBluetooth_mac.add(device.getAddress());
            }
//            arrayAdapterBluetooth = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
//                    listaBluetooth);
//            l_vw_bluetooth.setAdapter(arrayAdapterBluetooth);

            EstiloLista1 estilo = new EstiloLista1(getActivity(),listaBluetooth);
            l_vw_bluetooth.setAdapter(estilo);
        }
    }

}