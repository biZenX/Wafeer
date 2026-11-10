package com.wafeer.app.presentation.ui.editor.receipt

import com.wafeer.app.domain.receipt.ReceiptQrParser
import com.wafeer.app.domain.receipt.ReceiptScan
import com.wafeer.app.domain.receipt.ScanMode

data class ReceiptCameraUiState(
    val detected: ReceiptScan? = null,
    val isCapturing: Boolean = false,
    val captureFailed: Boolean = false,
    val usingFrontCamera: Boolean = false,
    val torchOn: Boolean = false,
    val hasFlash: Boolean = false,
    val canSwitchCamera: Boolean = false,
    val scanMode: ScanMode = ScanMode.RECEIPT,
    val detectedQr: String? = null,
    val suggestQrPrompt: Boolean = false,
) {
    fun cameraReady(hasFlash: Boolean, canSwitchCamera: Boolean): ReceiptCameraUiState =
        copy(hasFlash = hasFlash, canSwitchCamera = canSwitchCamera, torchOn = torchOn && hasFlash)

    fun toggleTorch(): ReceiptCameraUiState = if (hasFlash) copy(torchOn = !torchOn) else this

    fun switchCamera(): ReceiptCameraUiState =
        if (canSwitchCamera) copy(usingFrontCamera = !usingFrontCamera, torchOn = false, hasFlash = false) else this

    fun pickedImage(scan: ReceiptScan?): Pair<ReceiptCameraUiState, ReceiptScan?> =
        copy(isCapturing = false, captureFailed = scan == null) to scan

    fun withFrame(scan: ReceiptScan?): ReceiptCameraUiState =
        if (scan == null || isCapturing) this else copy(detected = scan, captureFailed = false)

    fun withQrDetected(rawQr: String): ReceiptCameraUiState {
        if (rawQr.isBlank() || isCapturing) return this
        val qrScan = ReceiptQrParser.parse(rawQr)
        return if (scanMode == ScanMode.QR_CODE) {
            copy(detected = qrScan, detectedQr = rawQr, suggestQrPrompt = false)
        } else {
            // In RECEIPT OCR mode: only suggest QR prompt if the QR is actually a valid receipt
            if (qrScan != null) {
                copy(detectedQr = rawQr, suggestQrPrompt = true)
            } else {
                this
            }
        }
    }

    fun switchToQrMode(): ReceiptCameraUiState {
        val qrScan = detectedQr?.let { ReceiptQrParser.parse(it) }
        return copy(
            scanMode = ScanMode.QR_CODE,
            suggestQrPrompt = false,
            detected = qrScan ?: detected,
        )
    }

    fun switchToReceiptMode(): ReceiptCameraUiState =
        copy(scanMode = ScanMode.RECEIPT, suggestQrPrompt = false, detected = null)

    fun dismissQrPrompt(): ReceiptCameraUiState =
        copy(suggestQrPrompt = false)

    fun toggleScanMode(): ReceiptCameraUiState =
        if (scanMode == ScanMode.RECEIPT) switchToQrMode() else switchToReceiptMode()

    fun capturing(): ReceiptCameraUiState = copy(isCapturing = true, captureFailed = false)

    fun captured(scan: ReceiptScan?): Pair<ReceiptCameraUiState, ReceiptScan?> {
        val result = scan ?: if (scanMode == ScanMode.QR_CODE) detected else null
        return copy(isCapturing = false, captureFailed = result == null) to result
    }
}
