package com.munch.lib.bluetooth.scan

import com.munch.lib.bluetooth.dev.BluetoothDev
import com.munch.lib.bluetooth.helper.BluetoothHelperConfig

/**
 * Create by munch1182 on 2022/10/26 16:28.
 */

interface IBluetoothScanner {
    /**
     * 表示当前扫描器的扫描状态
     */
    val isScanning: Boolean
    fun startScan(timeout: Long = BluetoothHelperConfig.config.defaultTimeout)
    fun stopScan()
}

interface IBluetoothScanListenerManager {
    fun addScanListener(l: OnBluetoothDevScannedListener)
    fun removeScanListener(l: OnBluetoothDevScannedListener)
}

/**
 * 该类的扫描回调只存在一次扫描周期内, 当扫描结束后, 扫描回调即被移除
 */
fun interface IBluetoothOnceScanListenerManager {
    fun setScanListener(l: OnBluetoothOwnerDevScannedListener): IBluetoothScanner
}

interface IBluetoothDevScanner : IBluetoothScanner, IBluetoothScanListenerManager

interface IBluetoothOnceScanner : IBluetoothScanner, IBluetoothOnceScanListenerManager

//<editor-fold desc="ScannedListener">
/**
 * 设备扫描到的回调
 */
fun interface OnBluetoothDevScannedListener {
    fun onBluetoothDevScanned(dev: BluetoothDev)
}

fun interface OnBluetoothOwnerDevScannedListener {
    fun onBluetoothDevScanned(scanner: IBluetoothScanner, dev: BluetoothDev)
}

interface OnBluetoothDevScanLifecycleListener {
    fun onScanStart() {}
    fun onScanStop() {}
}

/**
 * 拓展[OnBluetoothDevScannedListener], 附带开始和结束的回调
 */
interface OnBluetoothDevScanListener : OnBluetoothDevScannedListener,
    OnBluetoothDevScanLifecycleListener

interface OnBluetoothOwnerDevScanListener : OnBluetoothOwnerDevScannedListener,
    OnBluetoothDevScanLifecycleListener
//</editor-fold>