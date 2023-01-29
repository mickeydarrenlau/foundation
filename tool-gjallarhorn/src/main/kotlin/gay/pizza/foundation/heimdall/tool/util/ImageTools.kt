package gay.pizza.foundation.heimdall.tool.util

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun BufferedImage.savePngFile(path: String) {
  if (!ImageIO.write(this, "png", File(path))) {
    throw RuntimeException("Unable to write PNG.")
  }
}

fun BufferedImage.saveJpegFile(path: String) {
  if (!ImageIO.write(this, "jpeg", File(path))) {
    throw RuntimeException("Unable to write JPEG.")
  }
}
