package com.example.tesis1.Fragments.slideshow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tesis1.R;

import java.util.ArrayList;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    private ListView lista;
    private ProgressBar pg_wifi;
    private WifiManager wifiManager;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> Arraylista = new ArrayList<String>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        lista = root.findViewById(R.id.wifi);
        pg_wifi = root.findViewById(R.id.wifi_progreso);

        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(getContext().WIFI_SERVICE);

        scanWifi();

        return root;
    }

    private void scanWifi()
    {
        if(!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().registerReceiver(wifiScanReceiver, intentFilter);
    }

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (success)
            {
                List<ScanResult> results = wifiManager.getScanResults();

                for (ScanResult scanResult : results)
                {
                    Arraylista.add(scanResult.SSID);
                    arrayAdapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, Arraylista);
                    lista.setAdapter(arrayAdapter);
                }
            }
        }
    };}