package com.example.tesis1.Fragments.bluetoothFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BluetoothFragmentViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BluetoothFragmentViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}