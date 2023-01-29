package gay.pizza.foundation.heimdall.tool.commands

import gay.pizza.foundation.heimdall.tool.util.saveJpegFile
import gay.pizza.foundation.heimdall.tool.util.savePngFile
import java.awt.image.BufferedImage

enum class ImageFormatType(val id: String, val extension: String, val save: (BufferedImage, String) -> Unit) {
  Png("png", "png", { image, path -> image.savePngFile(path) }),
  Jpeg("jpeg", "jpg", { image, path -> image.saveJpegFile(path) })
}
