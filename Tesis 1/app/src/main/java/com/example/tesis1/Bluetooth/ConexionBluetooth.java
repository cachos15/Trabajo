package com.example.tesis1.Bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;

public class ConexionBluetooth extends Thread {
    private final BluetoothSocket ConexionSocket;
    private final OutputStream ConexionOutputStream;

    public ConexionBluetooth(BluetoothSocket socket) {
        ConexionSocket = socket;
        OutputStream SocketOutStream = null;

        try {
            SocketOutStream = ConexionSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        ConexionOutputStream = SocketOutStream;
    }


    public void Envio(String Datos) {
        byte[] DatosSalida = Datos.getBytes();
        try {
            ConexionOutputStream.write(DatosSalida);
            ConexionOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}