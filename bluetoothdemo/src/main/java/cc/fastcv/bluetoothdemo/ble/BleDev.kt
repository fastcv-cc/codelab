package cc.fastcv.bluetoothdemo.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import java.util.*

@SuppressLint("MissingPermission")
class BleDev(var dev: BluetoothDevice, var scanResult: ScanResult) {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val bleDev = o as BleDev
        return dev == bleDev.dev
    }

    override fun hashCode(): Int {
        return Objects.hash(dev)
    }

    fun getAddress(): String {
        return dev.address
    }

    fun getName(): String? {
        return dev.name
    }
}