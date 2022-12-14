package cc.fastcv.bluetoothdemo.ble.client

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cc.fastcv.bluetoothdemo.APP
import cc.fastcv.bluetoothdemo.R
import cc.fastcv.bluetoothdemo.ble.server.BLEServerActivity.Companion.UUID_CHAR_READ_NOTIFY
import cc.fastcv.bluetoothdemo.ble.server.BLEServerActivity.Companion.UUID_CHAR_WRITE
import cc.fastcv.bluetoothdemo.ble.server.BLEServerActivity.Companion.UUID_DESC_NOTITY
import cc.fastcv.bluetoothdemo.ble.server.BLEServerActivity.Companion.UUID_SERVICE
import java.util.*

@SuppressLint("MissingPermission")
class BLEDeviceActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "BLEDeviceActivity"

        fun startActivity(context: Context, address: String) {
            context.startActivity(Intent(context, BLEDeviceActivity::class.java).apply {
                putExtra("address", address)
            })
        }
    }


    private var mWriteET: EditText? = null
    private var mTips: TextView? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var isConnected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble_device)

        mTips = findViewById(R.id.tv_tips)

        mWriteET = findViewById(R.id.et_write)

        val manager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        val btConnect = findViewById<Button>(R.id.bt_connect)
        btConnect.setOnClickListener {
            intent?.getStringExtra("address")?.let {
                val remoteDevice = manager.adapter.getRemoteDevice(it)
                closeConn()
                mBluetoothGatt =
                    remoteDevice.connectGatt(this, false, mBluetoothGattCallback) // ??????????????????

                logTv(String.format("???[%s]????????????............", remoteDevice.address))
            }
        }

        val btDisConnect = findViewById<Button>(R.id.bt_disconnect)
        btDisConnect.setOnClickListener {
            closeConn()
        }

    }

    // ????????????????????????????????????????????????????????????????????????200ms?????????????????????????????????????????????????????????????????????
    // ???????????????????????????->onCharacteristicChanged()
    fun read(view: View?) {
        val service = getGattService(UUID_SERVICE)
        if (service != null) {
            val characteristic =
                service.getCharacteristic(UUID_CHAR_READ_NOTIFY) //??????UUID???????????????Characteristic
            mBluetoothGatt!!.readCharacteristic(characteristic)
        }
    }

    // ????????????????????????????????????????????????????????????????????????200ms?????????????????????????????????????????????????????????????????????
    // ???????????????????????????->onCharacteristicWrite()
    fun write(view: View?) {
        val service = getGattService(UUID_SERVICE)
        if (service != null) {
            val text = mWriteET!!.text.toString()
            val characteristic =
                service.getCharacteristic(UUID_CHAR_WRITE) //??????UUID???????????????Characteristic
            characteristic.value = text.toByteArray() //????????????20?????????
            mBluetoothGatt!!.writeCharacteristic(characteristic)
        }
    }

    // ????????????Characteristic???????????????->onCharacteristicChanged()
    fun setNotify(view: View?) {
        val service = getGattService(UUID_SERVICE)
        if (service != null) {
            // ??????Characteristic??????
            val characteristic =
                service.getCharacteristic(UUID_CHAR_READ_NOTIFY) //??????UUID??????????????????Characteristic
            mBluetoothGatt!!.setCharacteristicNotification(characteristic, true)

            // ???Characteristic???Descriptor?????????????????????????????????????????????????????????????????????
            val descriptor = characteristic.getDescriptor(UUID_DESC_NOTITY)
            // descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);//???????????????,??????????????????????????????,??????????????????????????????
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            mBluetoothGatt!!.writeDescriptor(descriptor)
        }
    }

    // ??????Gatt??????
    private fun getGattService(uuid: UUID): BluetoothGattService? {
        if (!isConnected) {
            APP.toast("????????????", 0)
            return null
        }
        val service = mBluetoothGatt!!.getService(uuid)
        if (service == null) APP.toast("??????????????????UUID=$uuid", 0)
        return service
    }

    // ????????????
    private fun logTv(msg: String) {
        if (isDestroyed) return
        runOnUiThread {
            APP.toast(msg, 0)
            mTips!!.append(msg.trimIndent())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeConn()
    }

    // BLE?????????????????????????????????????????????(??????2~7???)???????????????????????????????????????????????????????????????????????????????????????133
    private fun closeConn() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt!!.disconnect()
            mBluetoothGatt!!.close()
        }
    }

    // ?????????????????????Callback
    private var mBluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val dev = gatt.device
            Log.i(
                TAG,
                String.format(
                    "onConnectionStateChange:%s,%s,%s,%s",
                    dev.name,
                    dev.address,
                    status,
                    newState
                )
            )
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                isConnected = true
                gatt.discoverServices() //??????????????????
            } else {
                isConnected = false
                closeConn()
            }
            logTv(
                String.format(
                    if (status == 0) if (newState == 2) "???[%s]????????????" else "???[%s]????????????" else "???[%s]????????????,?????????:$status",
                    dev
                )
            )
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.i(
                TAG,
                String.format(
                    "onServicesDiscovered:%s,%s,%s",
                    gatt.device.name,
                    gatt.device.address,
                    status
                )
            )
            if (status == BluetoothGatt.GATT_SUCCESS) { //BLE??????????????????
                // ????????????BLE??????Services/Characteristics/Descriptors?????????UUID
                for (service in gatt.services) {
                    val allUUIDs = StringBuilder(
                        """
                          UUIDs={
                          S=${service.uuid}
                          """.trimIndent()
                    )
                    for (characteristic in service.characteristics) {
                        allUUIDs.append(",\nC=").append(characteristic.uuid)
                        for (descriptor in characteristic.descriptors) allUUIDs.append(",\nD=")
                            .append(descriptor.uuid)
                    }
                    allUUIDs.append("}")
                    Log.i(
                        TAG,
                        "onServicesDiscovered:$allUUIDs"
                    )
                    logTv("????????????$allUUIDs")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            val uuid = characteristic.uuid
            val valueStr = String(characteristic.value)
            Log.i(
                TAG,
                String.format(
                    "onCharacteristicRead:%s,%s,%s,%s,%s",
                    gatt.device.name,
                    gatt.device.address,
                    uuid,
                    valueStr,
                    status
                )
            )
            logTv("??????Characteristic[$uuid]:\n$valueStr")
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            BluetoothGatt.GATT_FAILURE
            val uuid = characteristic.uuid
            val valueStr = String(characteristic.value)
            Log.i(
                TAG,
                String.format(
                    "onCharacteristicWrite:%s,%s,%s,%s,%s",
                    gatt.device.name,
                    gatt.device.address,
                    uuid,
                    valueStr,
                    status
                )
            )
            logTv("??????Characteristic[$uuid]:\n$valueStr")
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val uuid = characteristic.uuid
            val valueStr = String(characteristic.value)
            Log.i(
                TAG,
                String.format(
                    "onCharacteristicChanged:%s,%s,%s,%s",
                    gatt.device.name,
                    gatt.device.address,
                    uuid,
                    valueStr
                )
            )
            logTv("??????Characteristic[$uuid]:\n$valueStr")
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            val uuid = descriptor.uuid
            val valueStr = Arrays.toString(descriptor.value)
            Log.i(
                TAG,
                String.format(
                    "onDescriptorRead:%s,%s,%s,%s,%s",
                    gatt.device.name,
                    gatt.device.address,
                    uuid,
                    valueStr,
                    status
                )
            )
            logTv("??????Descriptor[$uuid]:\n$valueStr")
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            val uuid = descriptor.uuid
            val valueStr = Arrays.toString(descriptor.value)
            Log.i(
                TAG,
                String.format(
                    "onDescriptorWrite:%s,%s,%s,%s,%s",
                    gatt.device.name,
                    gatt.device.address,
                    uuid,
                    valueStr,
                    status
                )
            )
            logTv("??????Descriptor[$uuid]:\n$valueStr")
        }

    }
}