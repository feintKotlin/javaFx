package com.feint.fx

import com.sun.security.ntlm.Server
import javafx.fxml.FXML
import javafx.scene.control.TextArea

class MainController{
    @FXML
    lateinit var area:TextArea

    constructor(){
        val sService=ServerService()

        sService.connectLis={
            area.text=area.text+it.remoteSocketAddress+"\n"
        }

        if(sService.start()){
            System.out.println("Start Server")
        }
    }
}