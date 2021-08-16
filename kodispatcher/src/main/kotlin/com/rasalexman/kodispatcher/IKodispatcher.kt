package com.rasalexman.kodispatcher

import com.rasalexman.kodi.core.IKodi

/**
 *
 */
interface IKodiListener : IKodi

/**
 *
 */
object KodiListener : KodiEventStorage(), IKodiListener

/**
 * Initialize KODI dependencies
 *
 * @param block - main initialization block for binding instances
 */
inline fun <reified ReturnType : Any> kodiListener(block: IKodiListener.() -> ReturnType): ReturnType {
    return KodiListener.block()
}