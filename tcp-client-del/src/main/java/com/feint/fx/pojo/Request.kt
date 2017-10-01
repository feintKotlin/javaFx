package com.feint.fx.pojo

data class Request(val req:String= RequestType.REFRESH, val data:Any?=null)


object RequestType{
    val LOGIN="#login#"
    val TALK="#talk#"
    val REFRESH="#refresh"
}