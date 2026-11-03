package com.wafeer.app.presentation.ui.editor.receipt

class ReceiptScanGeneration {
    var current: Int = 0
        private set

    fun open(): Int = ++current

    fun close() {
        current++
    }

    fun isCurrent(token: Int): Boolean = token == current
}
