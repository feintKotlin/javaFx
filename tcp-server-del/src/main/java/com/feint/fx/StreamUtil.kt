package com.feint.fx

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket

object StreamUtil{
    fun getStreamPair(socket:Socket):Pair<BufferedReader,OutputStream>{
        val br=socket.getInputStream().bufferedReader()
        return br to socket.getOutputStream()
    }
}