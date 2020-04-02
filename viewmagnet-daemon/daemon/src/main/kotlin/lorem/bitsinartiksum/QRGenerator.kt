package lorem.bitsinartiksum

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import java.awt.image.BufferedImage
import java.io.IOException

class QRGenerator {

    @Throws(WriterException::class, IOException::class)
    public fun generateQRCodeImage(
        text: String,
        width: Int,
        height: Int
    ) :  BufferedImage {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
        return MatrixToImageWriter.toBufferedImage(bitMatrix)
    }
}