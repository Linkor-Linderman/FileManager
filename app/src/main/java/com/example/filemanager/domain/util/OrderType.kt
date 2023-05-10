package com.example.filemanager.domain.util

sealed class OrderType{
    object Ascending: OrderType()
    object Descending: OrderType()
}