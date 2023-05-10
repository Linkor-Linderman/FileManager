package com.example.filemanager.domain.util

sealed class FileOrder(val orderType: OrderType) {
    class Name(orderType: OrderType): FileOrder(orderType)
    class Size(orderType: OrderType): FileOrder(orderType)
    class DateOfCreation(orderType: OrderType): FileOrder(orderType)
    class FileExtension(orderType: OrderType): FileOrder(orderType)

    fun copy(orderType: OrderType): FileOrder{
        return when(this){
            is Name -> Name(orderType)
            is Size -> Size(orderType)
            is DateOfCreation -> DateOfCreation(orderType)
            is FileExtension -> FileExtension(orderType)
        }
    }
}