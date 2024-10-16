package com.nmh.skynet.callback

interface ICallBackDimensional {
    fun callBackItem(objects: Any, callBackItem: ICallBackItem)

    fun callBackCheck(objects: Any, check: ICallBackCheck)
}