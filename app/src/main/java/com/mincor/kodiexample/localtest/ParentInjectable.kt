package com.mincor.kodiexample.localtest

import com.rasalexman.kodi.annotations.BindProvider

@BindProvider(
    toClass = IParentInjectable::class
)
class ParentInjectable : IParentInjectable {
}

interface IParentInjectable