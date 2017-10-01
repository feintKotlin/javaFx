package com.feint.fx.pojo

import java.sql.Timestamp
import java.util.*

data class Message(val msg:String="",
                   val id:Int=0,
                   val time:Timestamp= Timestamp(Date().time))