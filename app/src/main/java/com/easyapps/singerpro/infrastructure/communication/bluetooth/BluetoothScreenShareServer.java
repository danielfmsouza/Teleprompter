package com.easyapps.singerpro.infrastructure.communication.bluetooth;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by daniel on 08/07/2017.
 * Abstracts all methods to create and manipulate the bluetooth screen share server
 */

public class BluetoothScreenShareServer {

    private final BluetoothAdapter bluetoothAdapter;

    public BluetoothScreenShareServer(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public boolean isScreenShareAvailable(){
        return bluetoothAdapter == null;
    }


    public boolean isProtocolEnabled(){
        return bluetoothAdapter.isEnabled();
    }

    public void disable() {
        if (bluetoothAdapter != null){
            bluetoothAdapter.disable();
        }
    }
}
