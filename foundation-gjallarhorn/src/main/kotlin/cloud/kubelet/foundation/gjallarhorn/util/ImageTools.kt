package cloud.kubelet.foundation.gjallarhorn.util

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun BufferedImage.savePngFile(path: String) = ImageIO.write(this, "png", File(path))
