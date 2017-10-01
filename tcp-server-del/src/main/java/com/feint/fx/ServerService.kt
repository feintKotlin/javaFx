package com.feint.fx

import com.fasterxml.jackson.databind.ObjectMapper
import com.feint.fx.pojo.Data
import com.feint.fx.pojo.Message
import com.feint.fx.pojo.Request
import com.feint.fx.pojo.RequestType
import kotlinx.coroutines.experimental.async
import java.io.BufferedReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

class ServerService {


    lateinit var streamPair: Pair<BufferedReader, OutputStream>

    lateinit var connectLis: (socket: Socket) -> Unit

    var clientId = 0;

    val data = Data()

    fun start(): Boolean {

        val sServer = ServerSocket(8888)
        async {
            waitClient(sServer)
        }

        return true;
    }

    /**
     * 等待客户端进行连接
     */
    private suspend fun waitClient(sServer: ServerSocket) {
        while (true) {
            val socket = sServer.accept()
            connectLis(socket)

            update(socket)

        }
    }

    /**
     *  根据客户端的请求进行相应的操作。
     */
    fun update(socket: Socket) {
        val pair = StreamUtil.getStreamPair(socket)
        val text = pair.first.readText()

        val req = ObjectMapper().readValue<Request>(text, Request::class.java)
        /**
         * 依据请求的类型进行相应的操作
         * LOGIN：返回clientId给客户端，并让clientId＋1
         * TALK：存储客户端传来的对话信息
         * REFRESH：根据客户端传来的时间，返回相应的消息列表（再这里可以使用列表的filter函数，十分方便）
         */
        when (req.req) {
            RequestType.LOGIN -> {
                pair.second.write(ObjectMapper().writeValueAsBytes(
                        Request(RequestType.LOGIN, "${clientId++}")
                ))

                socket.shutdownOutput()
            }
            RequestType.TALK -> {
                try {
                    val msg = req.data as LinkedHashMap<String,Any>
                    val message = Message(msg["msg"]!! as String, msg["id"]!! as Int)
                    data.msgList.add(message)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            RequestType.REFRESH -> {
                val list=data.msgList.filter { it.time.time>req.data as Long }
                pair.second.write(ObjectMapper().writeValueAsBytes(list))
                socket.shutdownOutput()
            }
        }


    }
}

