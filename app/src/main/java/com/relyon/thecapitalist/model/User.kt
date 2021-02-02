package com.relyon.thecapitalist.model

data class User(

    var uid: String = "",
    var name: String = "",
    var cash: Float = 0f,
    var token: String = "",
    var premium: Boolean = false,
    var nickname: String = "",
    var photoPath: String = ""
)