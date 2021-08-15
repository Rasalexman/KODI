package com.rasalexman.kodi.core

sealed class KodiEvent {

    abstract class BaseEvent : KodiEvent()

    object BIND : BaseEvent()
    object UNBIND : BaseEvent()
    object INSTANCE : BaseEvent()
}