package com.mincor.kodiexample.instances

import com.mincor.kodiexample.single.UserData

data class Post(val id:String = "", val user:UserData? = null, val title:String = "", val desc:String = "")