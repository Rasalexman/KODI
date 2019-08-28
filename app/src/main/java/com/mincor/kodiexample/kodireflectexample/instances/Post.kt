package com.mincor.kodiexample.kodireflectexample.instances

import com.mincor.kodiexample.kodireflectexample.single.UserData

data class Post(val id:String = "", val user:UserData? = null, val title:String = "", val desc:String = "")