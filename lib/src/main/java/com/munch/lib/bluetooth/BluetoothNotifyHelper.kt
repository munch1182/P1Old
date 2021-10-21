package com.munch.lib.bluetooth

import android.bluetooth.BluetoothDevice
import com.munch.lib.base.Destroyable
import com.munch.lib.base.OnChangeListener
import com.munch.lib.helper.SimpleARSHelper

class BluetoothNotifyHelper : Destroyable {

    val scanListeners = SimpleARSHelper<OnScannerListener>()
    val stateListeners = SimpleARSHelper<OnStateChangeListener>()
    val connectListeners = SimpleARSHelper<OnConnectListener>()
    val bluetoothStateListeners = SimpleARSHelper<(bondState: Int, dev: BluetoothDevice?) -> Unit>()

    internal val scanCallback = object : OnScannerListener {
        override fun onStart() {
            BluetoothHelper.instance.apply {
                newState(BluetoothState.SCANNING)
                workHandler.post { scanListeners.notifyListener { it.onStart() } }
            }
        }

        override fun onScan(device: BluetoothDev) {
            BluetoothHelper.instance.workHandler.post {
                scanListeners.notifyListener {
                    it.onScan(device)
                }
            }
        }

        override fun onBatchScan(devices: MutableList<BluetoothDev>) {
            BluetoothHelper.instance.workHandler.post {
                scanListeners.notifyListener {
                    it.onBatchScan(devices)
                }
            }
        }

        override fun onComplete() {
            BluetoothHelper.instance.apply {
                newState(BluetoothState.IDLE)
                workHandler.post { scanListeners.notifyListener { it.onComplete() } }
            }
        }

        override fun onFail() {
            BluetoothHelper.instance.apply {
                newState(BluetoothState.IDLE)
                workHandler.post { scanListeners.notifyListener { it.onFail() } }
            }
        }
    }

    internal val stateChangeCallback = object : OnChangeListener {
        override fun onChange() {
            BluetoothHelper.instance.workHandler.post {
                stateListeners.notifyListener {
                    it.onChange(BluetoothHelper.instance.state.currentStateVal)
                }
            }
        }
    }

    internal val connectCallback = object : OnConnectListener {
        override fun onStart() {
            BluetoothHelper.instance.apply {
                newState(BluetoothState.CONNECTING)
                workHandler.post { connectListeners.notifyListener { it.onStart() } }
            }
        }

        override fun onConnectSuccess() {
            BluetoothHelper.instance.apply {
                newState(BluetoothState.CONNECTED)
                workHandler.post {
                    connectListeners.notifyListener { it.onConnectSuccess() }
                    clearConnectListener()
                }
            }
        }

        override fun onConnectFail(status: Int) {
            BluetoothHelper.instance.apply {
                newState(BluetoothState.IDLE)
                workHandler.post {
                    connectListeners.notifyListener { it.onConnectFail(status) }
                    clearConnectListener()
                }
            }
        }
    }

    internal val bluetoothStateCallback = { state: Int, dev: BluetoothDevice? ->
        BluetoothHelper.instance.workHandler.post {
            bluetoothStateListeners.notifyListener { it.invoke(state, dev) }
        }
    }

    /**
     * 当一次连接结束后(成功/失败)，则移除监听，因为连接是个一次性的动作
     * 连接的状态不在这个回调中，这个只表示连接的动作
     */
    private fun clearConnectListener() {
        connectListeners.clear()
    }

    override fun destroy() {
        scanListeners.clear()
        stateListeners.clear()
        connectListeners.clear()
    }
}