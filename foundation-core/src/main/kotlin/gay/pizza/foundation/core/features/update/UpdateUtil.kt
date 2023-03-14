package gay.pizza.foundation.core.features.update

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path

object UpdateUtil {
  private val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()

  fun getUrl(path: String): String =
    UpdateResolver.latestManifestUrl
      .toURI()
      .resolve(path)
      .toString()

  private inline fun <reified T> fetchFile(url: String, strategy: DeserializationStrategy<T>): T {
    val request = HttpRequest
      .newBuilder()
      .GET()
      .uri(URI.create(url))
      .build()

    val response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    )

    return Json.decodeFromString(
      strategy,
      response.body()
    )
  }

  fun downloadArtifact(path: String, outPath: Path) {
    val uri = URI.create(getUrl(path))
    val request = HttpRequest
      .newBuilder()
      .GET()
      .uri(uri)
      .build()

    val response = client.send(
      request,
      HttpResponse.BodyHandlers.ofFile(outPath)
    )
    if (response.statusCode() != 200) {
      throw RuntimeException("Failed to download URL $uri (Status Code: ${response.statusCode()})")
    }
    response.body()
  }
}
