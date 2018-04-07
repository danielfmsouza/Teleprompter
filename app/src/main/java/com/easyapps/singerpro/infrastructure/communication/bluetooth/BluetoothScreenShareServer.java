package com.easyapps.singerpro.infrastructure.communication.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.ArraySet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by daniel on 08/07/2017.
 * Abstracts all methods to create and manipulate the bluetooth screen share server
 */

public class BluetoothScreenShareServer {

    private static final String NAME = "BluetoothPrompter";
    private static final UUID MY_UUID = UUID.fromString("5D58D44C-E4A1-4CA3-BE17-21CB0908AF51");
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Inject
    public BluetoothScreenShareServer() {
    }

    public boolean isScreenShareAvailable() {
        return mBluetoothAdapter != null;
    }


    public boolean isProtocolEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean enable() {
        return mBluetoothAdapter.enable();
    }

    public void disable() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.disable();
        }
    }

    public Set<BluetoothDevice> getPairedDevices() {
        if (!isScreenShareAvailable()) return new HashSet<>();
        return mBluetoothAdapter.getBondedDevices();
    }

    public List<String> getPairedDevicesNames() {
        List<String> result = new ArrayList<>();
        if (!isScreenShareAvailable()) return result;
        for (BluetoothDevice device :
                mBluetoothAdapter.getBondedDevices()) {
            result.add(device.getName());
        }

        return result;
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        // Cancel any thread attempting to make a connection
//        if (mState == STATE_CONNECTING) {
//            if (mConnectThread != null) {
//                mConnectThread.cancel();
//                mConnectThread = null;
//            }
//        }
//        // Cancel any thread currently running a connection
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel();
//            mConnectedThread = null;
//        }
//        // Start the thread to connect with the given device
//        mConnectThread = new ConnectThread(device);
//        mConnectThread.start();
//        setState(STATE_CONNECTING);
    }

}
