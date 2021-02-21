package jenm.yagoc.ui.swing

import jenm.yagoc.pieces.*
import org.apache.batik.transcoder.SVGAbstractTranscoder
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

enum class Themes(val themePath: String, val toImage: (String) -> Image) {
    Original("/themes/original", ::fromGif),
    Modern("/themes/svg", ::fromSvg);

    fun loadImages(): Map<Piece, Image> =
        HashMap<Piece, Image>().also {
            it[whitePawn] = toImage("$themePath/white_pawn")
            it[whiteKnight] = toImage("$themePath/white_knight")
            it[whiteBishop] = toImage("$themePath/white_bishop")
            it[whiteRook] = toImage("$themePath/white_rook")
            it[whiteQueen] = toImage("$themePath/white_queen")
            it[whiteKing] = toImage("$themePath/white_king")
            it[blackPawn] = toImage("$themePath/black_pawn")
            it[blackKnight] = toImage("$themePath/black_knight")
            it[blackBishop] = toImage("$themePath/black_bishop")
            it[blackRook] = toImage("$themePath/black_rook")
            it[blackQueen] = toImage("$themePath/black_queen")
            it[blackKing] = toImage("$themePath/black_king")
        }

}

private fun fromGif(path: String) = Toolkit.getDefaultToolkit().getImage(Themes::class.java.getResource("$path.gif"))

private fun fromSvg(path: String) = createImageFromSVG(Themes::class.java.getResource("$path.svg"))

private fun createImageFromSVG(url: URL): BufferedImage {
    val resultByteStream = ByteArrayOutputStream()
    val transcoderInput = TranscoderInput(url.toExternalForm())
    val transcoderOutput = TranscoderOutput(resultByteStream)
    val pngTranscoder = PNGTranscoder()
    pngTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, 240f)
    pngTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, 240f)
    pngTranscoder.transcode(transcoderInput, transcoderOutput)
    resultByteStream.flush()
    return ImageIO.read(ByteArrayInputStream(resultByteStream.toByteArray()))
}
