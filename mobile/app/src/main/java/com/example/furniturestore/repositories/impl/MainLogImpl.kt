package com.example.furniturestore.repositories.impl

import android.util.Log
import com.example.furniturestore.repositories.MainLog
import javax.inject.Inject


class MainLogImpl @Inject constructor(): MainLog {
    override fun i(tag: String, msg: String) {
        Log.i(tag,msg)
    }

    override fun d(tag: String, msg: String) {
        Log.d(tag,msg)
    }

    override fun e(tag: String, msg: String) {
        Log.e(tag,msg)
    }
}