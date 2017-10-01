package com.feint.fx.pojo

import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class Data{
    val msgList= ArrayList<Message>()

    val clientList=ArrayList<Int>()
}
data class Message(val msg:String="",
                   val id:Int=0,
                   val time: Timestamp = Timestamp(Date().time))