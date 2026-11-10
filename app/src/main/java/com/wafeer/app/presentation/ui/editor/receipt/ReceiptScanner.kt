package com.wafeer.app.presentation.ui.editor.receipt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.googlecode.tesseract.android.TessBaseAPI
import com.wafeer.app.domain.receipt.ReceiptParser
import com.wafeer.app.domain.receipt.ReceiptQrParser
import com.wafeer.app.domain.receipt.ReceiptScan
import com.wafeer.app.domain.receipt.ScanMode
import com.wafeer.app.domain.receipt.receiptOcrLanguages
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

private const val TAG = "WAFEER:ReceiptScanner"

class ReceiptScannerState(
    val isOpen: Boolean,
    val isScanning: Boolean,
    val camera: ReceiptCameraUiState,
    val launch: () -> Unit,
    val close: () -> Unit,
    val capture: () -> Unit,
    val pickImage: () -> Unit,
    val switchCamera: () -> Unit,
    val toggleTorch: () -> Unit,
    val toggleScanMode: () -> Unit,
    val switchToQrMode: () -> Unit,
    val switchToReceiptMode: () -> Unit,
    val dismissQrPrompt: () -> Unit,
    val preview: @Composable (Modifier) -> Unit,
)

@Composable
fun rememberReceiptScanner(
    onScanned: (ReceiptScan) -> Unit,
    onFailed: () -> Unit,
): ReceiptScannerState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnScanned by rememberUpdatedState(onScanned)
    val currentOnFailed by rememberUpdatedState(onFailed)
    var isOpen by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf(ReceiptCameraUiState()) }
    var session by remember { mutableStateOf<ReceiptCameraSession?>(null) }
    val generation = remember { ReceiptScanGeneration() }

    fun closeSheet() {
        generation.close()
        isOpen = false
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val fromSheet = isOpen
        val token = generation.current
        if (fromSheet) camera = camera.capturing() else isScanning = true
        scope.launch {
            val scan = runCatching { ReceiptOcr.read(context, uri) }.getOrNull()
            if (!generation.isCurrent(token)) return@launch
            if (fromSheet) {
                val (updated, result) = camera.pickedImage(scan)
                camera = updated
                if (result != null) {
                    closeSheet()
                    currentOnScanned(result)
                }
            } else {
                isScanning = false
                if (scan != null) currentOnScanned(scan) else currentOnFailed()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            generation.open()
            camera = ReceiptCameraUiState()
            isOpen = true
        } else {
            currentOnFailed()
        }
    }

    fun launch() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            generation.open()
            camera = ReceiptCameraUiState()
            isOpen = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun capture() {
        val s = session ?: return
        if (camera.isCapturing) return
        val token = generation.current
        val mode = camera.scanMode
        camera = camera.capturing()
        scope.launch {
            val scan = s.capture(mode)
            if (!generation.isCurrent(token)) return@launch
            val (updated, result) = camera.captured(scan)
            camera = updated
            if (result != null) {
                closeSheet()
                currentOnScanned(result)
            }
        }
    }

    val preview: @Composable (Modifier) -> Unit = { modifier ->
        ReceiptCameraPreview(
            usingFrontCamera = camera.usingFrontCamera,
            torchOn = camera.torchOn,
            onReady = { newSession ->
                session = newSession
                camera = camera.cameraReady(
                    hasFlash = newSession.hasFlash,
                    canSwitchCamera = newSession.canSwitchCamera,
                )
            },
            onQrDetected = { qrText ->
                if (!camera.isCapturing) {
                    camera = camera.withQrDetected(qrText)
                }
            },
            modifier = modifier,
        )
    }

    return ReceiptScannerState(
        isOpen = isOpen,
        isScanning = isScanning,
        camera = camera,
        launch = ::launch,
        close = ::closeSheet,
        capture = ::capture,
        pickImage = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        switchCamera = { camera = camera.switchCamera() },
        toggleTorch = { camera = camera.toggleTorch() },
        toggleScanMode = { camera = camera.toggleScanMode() },
        switchToQrMode = { camera = camera.switchToQrMode() },
        switchToReceiptMode = { camera = camera.switchToReceiptMode() },
        dismissQrPrompt = { camera = camera.dismissQrPrompt() },
        preview = preview,
    )
}

private class ReceiptCameraSession(
    private val captureUseCase: ImageCapture,
    private val ocr: ReceiptOcrEngine,
    val hasFlash: Boolean,
    val canSwitchCamera: Boolean,
    val camera: Camera,
) {
    suspend fun capture(scanMode: ScanMode): ReceiptScan? = withContext(Dispatchers.Default) {
        val proxy = suspendCancellableCoroutine<ImageProxy?> { cont ->
            captureUseCase.takePicture(
                Dispatchers.Default.asExecutor(),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        if (cont.isActive) cont.resume(image) else image.close()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        logcat(TAG) { "Capture failed: ${exception.asLog()}" }
                        if (cont.isActive) cont.resume(null)
                    }
                },
            )
        } ?: return@withContext null

        try {
            val bitmap = proxy.toBitmap().rotated(proxy.imageInfo.rotationDegrees)
            if (scanMode == ScanMode.QR_CODE) {
                val qrText = BarcodeReader.read(bitmap)
                if (qrText != null) {
                    val qrScan = ReceiptQrParser.parse(qrText)
                    if (qrScan != null) {
                        if (!bitmap.isRecycled) bitmap.recycle()
                        return@withContext qrScan
                    }
                }
                if (!bitmap.isRecycled) bitmap.recycle()
                return@withContext null
            }

            // In RECEIPT mode: execute OCR on the receipt text
            val downscaled = ReceiptOcr.downscale(bitmap)
            val text = ocr.read(downscaled)
            val parsedScan = ReceiptParser.parse(text)
            if (parsedScan != null) {
                if (!downscaled.isRecycled) downscaled.recycle()
                if (!bitmap.isRecycled && bitmap != downscaled) bitmap.recycle()
                return@withContext parsedScan
            }

            // Fallback: If OCR found nothing on the image, check if the image has a valid receipt QR
            val qrText = BarcodeReader.read(bitmap)
            if (qrText != null) {
                val qrScan = ReceiptQrParser.parse(qrText)
                if (qrScan != null) {
                    if (!downscaled.isRecycled) downscaled.recycle()
                    if (!bitmap.isRecycled && bitmap != downscaled) bitmap.recycle()
                    return@withContext qrScan
                }
            }

            if (!downscaled.isRecycled) downscaled.recycle()
            if (!bitmap.isRecycled && bitmap != downscaled) bitmap.recycle()
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logcat(TAG) { "OCR failed: ${e.asLog()}" }
            null
        } finally {
            proxy.close()
        }
    }
}

private fun kotlinx.coroutines.CoroutineDispatcher.asExecutor(): java.util.concurrent.Executor =
    java.util.concurrent.Executor { command ->
        kotlinx.coroutines.CoroutineScope(this).launch { command.run() }
    }

@Composable
private fun ReceiptCameraPreview(
    usingFrontCamera: Boolean,
    torchOn: Boolean,
    onReady: (ReceiptCameraSession) -> Unit,
    onQrDetected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnQrDetected by rememberUpdatedState(onQrDetected)
    val currentOnReady by rememberUpdatedState(onReady)

    var cameraRef by remember { mutableStateOf<Camera?>(null) }
    var viewWidth by remember { mutableFloatStateOf(0f) }
    var viewHeight by remember { mutableFloatStateOf(0f) }

    val tapAnim = remember { Animatable(0f) }
    var tapPoint by remember { mutableStateOf<Offset?>(null) }

    LaunchedEffect(torchOn, cameraRef) {
        val cam = cameraRef ?: return@LaunchedEffect
        if (cam.cameraInfo.hasFlashUnit()) cam.cameraControl.enableTorch(torchOn)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged {
                viewWidth = it.width.toFloat()
                viewHeight = it.height.toFloat()
            }
            .pointerInput(cameraRef, viewWidth, viewHeight) {
                detectTapGestures { offset ->
                    val cam = cameraRef ?: return@detectTapGestures
                    if (viewWidth <= 0f || viewHeight <= 0f) return@detectTapGestures
                    val factory = PreviewView(context).meteringPointFactory
                    val point = factory.createPoint(offset.x, offset.y)
                    val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                        .setAutoCancelDuration(3, TimeUnit.SECONDS)
                        .build()
                    cam.cameraControl.startFocusAndMetering(action)
                    tapPoint = offset
                }
            }
            .pointerInput(cameraRef) {
                detectTransformGestures { _, _, zoomChange, _ ->
                    val cam = cameraRef ?: return@detectTransformGestures
                    val currentZoom = cam.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                    cam.cameraControl.setZoomRatio(currentZoom * zoomChange)
                }
            },
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
                val ocr = ReceiptOcrEngine(ctx)
                var lastScanned = 0L

                val providerFuture = ProcessCameraProvider.getInstance(ctx)
                providerFuture.addListener({
                    val provider = providerFuture.get()
                    val selector = if (usingFrontCamera) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }

                    val resolutionSelector = ResolutionSelector.Builder()
                        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                        .setResolutionStrategy(
                            ResolutionStrategy(Size(1920, 1080), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER),
                        )
                        .build()

                    val previewUseCase = Preview.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .build().also { it.surfaceProvider = previewView.surfaceProvider }

                    val captureUseCase = ImageCapture.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    val analysisUseCase = ImageAnalysis.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    // Fast, lightweight QR & Barcode detection in camera preview (10-15ms, zero heavy OCR lag)
                    analysisUseCase.setAnalyzer(cameraExecutor) { image ->
                        val now = System.currentTimeMillis()
                        if (now - lastScanned < 300) {
                            image.close()
                            return@setAnalyzer
                        }
                        lastScanned = now

                        try {
                            val qrText = BarcodeReader.read(image)
                            if (!qrText.isNullOrBlank()) {
                                currentOnQrDetected(qrText)
                            }
                        } catch (e: Exception) {
                            logcat(TAG) { "Barcode analysis failed: ${e.asLog()}" }
                        } finally {
                            image.close()
                        }
                    }

                    try {
                        provider.unbindAll()
                        val cam = provider.bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            previewUseCase,
                            captureUseCase,
                            analysisUseCase,
                        )
                        cameraRef = cam
                        val hasFlash = cam.cameraInfo.hasFlashUnit()
                        val canSwitch = provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) &&
                            provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
                        currentOnReady(
                            ReceiptCameraSession(
                                captureUseCase = captureUseCase,
                                ocr = ocr,
                                hasFlash = hasFlash,
                                canSwitchCamera = canSwitch,
                                camera = cam,
                            ),
                        )
                    } catch (exc: Exception) {
                        logcat(TAG) { "Camera binding failed: ${exc.asLog()}" }
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize(),
        )

        LaunchedEffect(tapPoint) {
            val point = tapPoint ?: return@LaunchedEffect
            tapAnim.snapTo(1f)
            tapAnim.animateTo(0f, animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing))
            tapPoint = null
        }

        tapPoint?.let { offset ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                val alpha = tapAnim.value
                val radius = 32.dp.toPx() * (1f + (1f - alpha) * 0.2f)
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = radius,
                    center = offset,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }
    }
}

private class ReceiptOcrEngine(
    private val context: Context,
    private val languages: List<String> = ReceiptOcr.languagesFor(context),
) {
    private var tess: TessBaseAPI? = null
    private var currentLang: String? = null
    private val mutex = Mutex()

    /**
     * Executes Tesseract OCR safely using single-language passes to completely eliminate
     * the native multi-model memory corruption bug in libtesseract.so (classify_word_and_language / ReplaceCurrentWord).
     * Automatically attempts fallback to English if the primary language yields no usable receipt data.
     */
    suspend fun read(bitmap: Bitmap): String = mutex.withLock {
        withContext(Dispatchers.Default) {
            val primaryLang = languages.firstOrNull() ?: ReceiptOcr.FALLBACK_LANGUAGE
            val text = runPass(bitmap, primaryLang)

            val parsedPrimary = if (text.isNotBlank()) ReceiptParser.parse(text) else null
            // If primary language yielded a complete receipt with items, return it
            if (parsedPrimary != null && parsedPrimary.items.isNotEmpty()) {
                return@withContext text
            }

            // Fallback pass with English if primary was non-English
            if (primaryLang != ReceiptOcr.FALLBACK_LANGUAGE) {
                val engText = runPass(bitmap, ReceiptOcr.FALLBACK_LANGUAGE)
                if (engText.isNotBlank()) {
                    val combined = if (text.isNotBlank()) "$text\n$engText" else engText
                    val parsedCombined = ReceiptParser.parse(combined)
                    val parsedEng = ReceiptParser.parse(engText)

                    return@withContext when {
                        parsedCombined != null && parsedCombined.items.isNotEmpty() -> combined
                        parsedPrimary != null -> text
                        parsedEng != null && parsedEng.items.isNotEmpty() -> engText
                        parsedCombined != null -> combined
                        text.isNotBlank() -> text
                        else -> engText
                    }
                }
            }

            text
        }
    }

    private fun runPass(bitmap: Bitmap, lang: String): String {
        return try {
            val api = getOrCreateApi(lang)
            api.setImage(bitmap)
            api.getUTF8Text().orEmpty().also { text ->
                logcat(TAG) { "OCR ($lang) ${bitmap.width}x${bitmap.height} read:\n$text\n-> ${ReceiptParser.parse(text)}" }
            }
        } catch (e: Exception) {
            logcat(TAG) { "Tesseract OCR error for $lang: ${e.asLog()}" }
            ""
        } finally {
            tess?.clear()
        }
    }

    private fun getOrCreateApi(lang: String): TessBaseAPI {
        if (tess != null && currentLang == lang) return tess!!
        tess?.recycle()
        tess = null

        val api = TessBaseAPI()
        try {
            val dataPath = ReceiptOcr.installTessdata(context, listOf(lang))
            logcat(TAG) { "Starting OCR with single-language: $lang" }
            check(api.init(dataPath, lang)) { "Tesseract init failed for $lang" }
            api.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO
            currentLang = lang
            tess = api
            return api
        } catch (failure: Throwable) {
            api.recycle()
            throw failure
        }
    }

    fun close() {
        tess?.recycle()
        tess = null
        currentLang = null
    }
}

object ReceiptOcr {
    const val FALLBACK_LANGUAGE = "eng"
    private const val MAX_SIDE_PX = 2400

    fun languagesFor(context: Context): List<String> {
        val app = context.resources.configuration.locales
        val system = Resources.getSystem().configuration.locales
        val locales = (0 until app.size()).map(app::get) + (0 until system.size()).map(system::get)
        return receiptOcrLanguages(locales)
    }

    suspend fun read(context: Context, uri: Uri): ReceiptScan? = withContext(Dispatchers.Default) {
        val bitmap = decodeUpright(context, uri) ?: return@withContext null

        // 1. Perform OCR on the receipt image first
        val scaled = downscale(bitmap)
        val engine = ReceiptOcrEngine(context)
        val ocrScan = try {
            val text = engine.read(scaled)
            ReceiptParser.parse(text)
        } finally {
            if (!scaled.isRecycled) scaled.recycle()
            engine.close()
        }

        if (ocrScan != null) {
            if (!bitmap.isRecycled && bitmap != scaled) bitmap.recycle()
            return@withContext ocrScan
        }

        // 2. Fallback: if OCR found no receipt, check if image is a QR code
        val qrText = BarcodeReader.read(bitmap)
        if (qrText != null) {
            val qrScan = ReceiptQrParser.parse(qrText)
            if (qrScan != null) {
                if (!bitmap.isRecycled && bitmap != scaled) bitmap.recycle()
                return@withContext qrScan
            }
        }

        if (!bitmap.isRecycled && bitmap != scaled) bitmap.recycle()
        null
    }

    fun installTessdata(context: Context, languages: List<String>): String {
        val root = File(context.filesDir, "tesseract")
        val tessdata = File(root, "tessdata").apply { mkdirs() }
        (languages + FALLBACK_LANGUAGE).distinct().map { "$it.traineddata" }.forEach { name ->
            val target = File(tessdata, name)
            if (target.exists()) return@forEach
            val partial = File(tessdata, "$name.part")
            context.assets.open("tessdata/$name").use { input ->
                partial.outputStream().use { input.copyTo(it) }
            }
            check(partial.renameTo(target)) { "Could not install $name" }
        }
        return root.absolutePath
    }

    fun downscale(bitmap: Bitmap): Bitmap {
        val longest = maxOf(bitmap.width, bitmap.height)
        if (longest <= MAX_SIDE_PX) return bitmap
        val scale = MAX_SIDE_PX.toFloat() / longest
        val scaled = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scale).toInt(),
            (bitmap.height * scale).toInt(),
            true,
        )
        if (scaled != bitmap) bitmap.recycle()
        return scaled
    }

    private fun decodeUpright(context: Context, uri: Uri): Bitmap? {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / sample > MAX_SIDE_PX) sample *= 2
        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: return null
        val degrees = resolver.openInputStream(uri)?.use { ExifInterface(it).rotationDegrees } ?: 0
        return bitmap.rotated(degrees)
    }

    private val ExifInterface.rotationDegrees: Int
        get() = when (getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
}

private fun Bitmap.rotated(degrees: Int): Bitmap {
    if (degrees == 0) return this
    val rotated = Bitmap.createBitmap(
        this, 0, 0, width, height,
        Matrix().apply { postRotate(degrees.toFloat()) }, true,
    )
    if (rotated != this) recycle()
    return rotated
}
