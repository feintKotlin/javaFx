package com.feint.fx

import com.fasterxml.jackson.databind.ObjectMapper
import com.feint.fx.pojo.Message
import com.feint.fx.pojo.Request
import com.feint.fx.pojo.RequestType
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import java.text.SimpleDateFormat
import java.util.*

class MainController {


    @FXML
    lateinit var inputText: TextField

    @FXML
    lateinit var talkArea: TextArea

    @FXML
    lateinit var hint: Label

    @FXML
    lateinit var sendBtn: Button

    val port=8888

    val cService = ClientService()

    var clientId: String = "-1"

    var lastUpdate = Date().time

    @FXML
    fun send() {
        val msg = Message(inputText.text, clientId.toInt())
        cService.connect(port) {
            val streamPair = StreamUtil.getStreamPair(it)
            streamPair.second.write(ObjectMapper().
                    writeValueAsString(Request(RequestType.TALK, msg)).
                    toByteArray())
            it.shutdownOutput()
        }
        inputText.text=""
    }

    /**
     * 登录，获取clientID
     */
    @FXML
    fun login() {
            if (cService.connect(port) {

                val streamPair = StreamUtil.getStreamPair(it)
                streamPair.second.write(
                        ObjectMapper().
                                writeValueAsString(Request(RequestType.LOGIN, "")).
                                toByteArray()
                )
                it.shutdownOutput()
                //获取clientID
                clientId = ObjectMapper().
                        readValue(streamPair.first.readText(), Request::class.java).data.toString()

            }) {
                hint.text = "登录成功$clientId"

                async {
                    refresh()
                }
            } else
                hint.text = "登录失败，请关闭重试"
    }

    /**
     * 用于刷新界面上的聊天信息
     */
    suspend fun refresh() {
        while (true) {
            cService.connect(port) {
                val pair = StreamUtil.getStreamPair(it)
                /**
                 * 发起刷新请求
                 * RequestType.REFRESH：表示此次的请求类型为刷新
                 * lastUpdate：最近一次更新消息记录的时间
                 */
                pair.second.write(
                        ObjectMapper().
                                writeValueAsString(Request(RequestType.REFRESH, lastUpdate)).
                                toByteArray()
                )
                it.shutdownOutput()
                /**
                 * 获取服务端返回的消息列表，解析后显示到talkArea上。
                 */
                val text = pair.first.readText()
                try {
                    ObjectMapper().readValue<List<Message>>(text,
                            ObjectMapper().typeFactory.
                                    constructParametricType(List::class.java, Message::class.java)
                    ).forEach {
                        talkArea.text+="ID:${it.id}--${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                                format(it.time)}\n${it.msg}\n\n"
                    }
                    //更新最近刷新时间，将lastUpdate的值设置为当前时刻
                    lastUpdate=Date().time

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //延迟1秒，即每隔一秒进行一次记录刷新。
            delay(1000)
        }

    }
}

