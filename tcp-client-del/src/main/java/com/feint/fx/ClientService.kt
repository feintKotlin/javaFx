package com.feint.fx

import java.io.BufferedReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket

class ClientService{

    fun connect(port:Int,
                pair:(socket:Socket)->Unit):Boolean{
        return try {
            val socket=Socket("localhost",port)

            pair(socket)
            socket.close()
            return true
        }catch (e:Exception) {
            e.printStackTrace()
            return false;
        }
    }
}