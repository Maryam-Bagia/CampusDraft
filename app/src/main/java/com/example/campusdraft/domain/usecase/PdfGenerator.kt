package com.example.campusdraft.domain.usecase

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.core.content.FileProvider
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Handles all PDF generation and printing using Android's built-in PdfDocument API.
 * No third-party PDF library required – uses platform APIs only.
 */
object PdfGenerator {

    private const val PAGE_WIDTH  = 595   // A4 width in points (72 dpi)
    private const val PAGE_HEIGHT = 842   // A4 height in points
    private const val MARGIN      = 56f   // ~20mm margin
    private const val CONTENT_WIDTH = PAGE_WIDTH - MARGIN * 2

    /**
     * Generates a professional PDF file from the given rendered text.
     * Returns the [File] pointing to the saved PDF, or null on error.
     */
    fun generatePdf(
        context: Context,
        title: String,
        renderedText: String
    ): File? {
        return try {
            val document = PdfDocument()
            val lines = prepareLines(renderedText)

            // Paints
            val titlePaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                textSize = 14f
                color = Color.parseColor("#1a1a2e")
                isAntiAlias = true
            }
            val bodyPaint = Paint().apply {
                typeface = Typeface.DEFAULT
                textSize = 11f
                color = Color.parseColor("#2d2d2d")
                isAntiAlias = true
            }
            val accentPaint = Paint().apply {
                color = Color.parseColor("#6c63ff")
                strokeWidth = 2f
                style = Paint.Style.FILL
            }
            val lightLinePaint = Paint().apply {
                color = Color.parseColor("#e8e8f0")
                strokeWidth = 1f
            }

            val lineHeight  = 16f
            val titleHeight = 20f
            val headerH     = 80f

            // Calculate how many lines fit per page
            val usableHeight = PAGE_HEIGHT - MARGIN * 2 - headerH - 40f
            val linesPerPage = (usableHeight / lineHeight).toInt()
            val pageCount    = Math.ceil(lines.size.toDouble() / linesPerPage).toInt().coerceAtLeast(1)

            for (pageIndex in 0 until pageCount) {
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex + 1).create()
                val page     = document.startPage(pageInfo)
                val canvas   = page.canvas

                drawPageBackground(canvas, pageIndex, pageCount)
                drawHeader(canvas, title, titlePaint, accentPaint, lightLinePaint, headerH)

                val startLine = pageIndex * linesPerPage
                val endLine   = minOf(startLine + linesPerPage, lines.size)
                var y = MARGIN + headerH + lineHeight

                for (i in startLine until endLine) {
                    canvas.drawText(lines[i], MARGIN, y, bodyPaint)
                    y += lineHeight
                }

                drawFooter(canvas, pageIndex + 1, pageCount, bodyPaint)
                document.finishPage(page)
            }

            // Save to app's external files directory
            val dir = File(context.getExternalFilesDir(null), "Documents").also { it.mkdirs() }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val safeTitle = title.replace(Regex("[^a-zA-Z0-9]"), "_").take(30)
            val file = File(dir, "${safeTitle}_${timestamp}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawPageBackground(canvas: Canvas, pageIndex: Int, total: Int) {
        // White background
        canvas.drawColor(Color.WHITE)
        // Subtle top colour bar
        if (pageIndex == 0) {
            val barPaint = Paint().apply { color = Color.parseColor("#f0eeff") }
            canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 8f, barPaint)
        }
    }

    private fun drawHeader(
        canvas: Canvas, title: String,
        titlePaint: Paint, accentPaint: Paint, linePaint: Paint, headerH: Float
    ) {
        // Purple accent bar on left edge
        canvas.drawRect(0f, 0f, 4f, headerH, accentPaint)

        // App branding
        val smallPaint = Paint().apply {
            typeface = Typeface.DEFAULT
            textSize = 8f
            color = Color.parseColor("#888888")
            isAntiAlias = true
        }
        canvas.drawText("SMART APPLICATION WRITER", MARGIN, MARGIN - 8f, smallPaint)

        // Application title
        canvas.drawText(title.uppercase(), MARGIN, MARGIN + 14f, titlePaint)

        // Separator line
        canvas.drawLine(MARGIN, MARGIN + 24f, PAGE_WIDTH - MARGIN, MARGIN + 24f, linePaint)

        // Date line
        val datePaint = Paint().apply {
            typeface = Typeface.DEFAULT
            textSize = 9f
            color = Color.parseColor("#888888")
            isAntiAlias = true
        }
        val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Generated on $dateStr", MARGIN, MARGIN + 38f, datePaint)
    }

    private fun drawFooter(canvas: Canvas, current: Int, total: Int, bodyPaint: Paint) {
        val footerPaint = Paint().apply {
            typeface = Typeface.DEFAULT
            textSize = 9f
            color = Color.parseColor("#aaaaaa")
            isAntiAlias = true
        }
        val linePaint = Paint().apply {
            color = Color.parseColor("#e8e8f0")
            strokeWidth = 1f
        }
        val y = PAGE_HEIGHT - MARGIN + 10f
        canvas.drawLine(MARGIN, y - 10f, PAGE_WIDTH - MARGIN, y - 10f, linePaint)
        canvas.drawText("Page $current of $total  •  Smart Application Writer", MARGIN, y + 4f, footerPaint)
    }

    /**
     * Word-wraps the raw rendered text into individual lines that fit within CONTENT_WIDTH.
     */
    private fun prepareLines(text: String): List<String> {
        val paint = Paint().apply { textSize = 11f }
        val result = mutableListOf<String>()
        text.lines().forEach { paragraph ->
            if (paragraph.isBlank()) {
                result.add("")
                return@forEach
            }
            val words = paragraph.split(" ")
            var current = ""
            words.forEach { word ->
                val candidate = if (current.isEmpty()) word else "$current $word"
                if (paint.measureText(candidate) <= CONTENT_WIDTH) {
                    current = candidate
                } else {
                    if (current.isNotEmpty()) result.add(current)
                    current = word
                }
            }
            if (current.isNotEmpty()) result.add(current)
        }
        return result
    }

    /**
     * Returns a shareable [Uri] for the given PDF file using FileProvider.
     */
    fun getShareUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    /**
     * Launches Android's print dialog using WebView as the print adapter.
     */
    fun printDocument(context: Context, title: String, htmlContent: String) {
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val adapter = view.createPrintDocumentAdapter(title)
                val attrs = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print(title, adapter, attrs)
            }
        }
        webView.loadDataWithBaseURL(null, buildHtmlForPrint(title, htmlContent), "text/html", "UTF-8", null)
    }

    /** Converts raw text to HTML for the print adapter */
    private fun buildHtmlForPrint(title: String, text: String): String {
        val escaped = text
            .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\n", "<br/>")
        return """
            <!DOCTYPE html><html><head>
            <meta charset="UTF-8"/>
            <style>
                body { font-family: 'Times New Roman', serif; font-size: 12pt; margin: 2cm; line-height: 1.6; color: #1a1a1a; }
                h2   { font-size: 14pt; color: #4a3f9f; border-bottom: 1px solid #ccc; padding-bottom: 6pt; margin-bottom: 16pt; }
                p    { white-space: pre-wrap; }
            </style></head><body>
            <h2>$title</h2><p>$escaped</p>
            </body></html>
        """.trimIndent()
    }
}