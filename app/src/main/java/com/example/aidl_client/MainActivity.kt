package com.example.aidl_client

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.content.Intent
import android.os.RemoteException
import android.util.Log
import android.widget.Toast

import com.example.aidl_service.IRemoteAidlInterface


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var iRemoteAidlInterface: IRemoteAidlInterface? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            iRemoteAidlInterface = IRemoteAidlInterface.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            iRemoteAidlInterface = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val connect = findViewById<Button>(R.id.connect)
        connect.setOnClickListener {
            bindService()
        }
        val fetch = findViewById<Button>(R.id.fetch)
        fetch.setOnClickListener {
            iRemoteAidlInterface?.let {
                try {
                    val name = it.personUserName
                    val age = it.personUserAge
                    Toast.makeText(
                        applicationContext,
                        "name = $name, age = $age",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun bindService() {
        /**
         * Android 5.0 以上会报错：IllegalArgumentException:
         * Service Intent must be explicit，可通过下面代码实现
         *
         * Intent intent = new Intent("com.fqxyi.aidlservice.remote");
         * bindService(intent, conn, Context.BIND_AUTO_CREATE);
         */
        val intent = Intent()
        intent.action = "com.example.aidl_server.remote"
        intent.setPackage("com.example.aidl_server")
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        Log.d(TAG, "bindService")
    }

    private fun unbindService() {
        unbindService(connection)
        Log.d(TAG, "unbindService")
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService()
    }
}