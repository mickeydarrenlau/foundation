package cloud.kubelet.foundation.core.features.update

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path

object UpdateUtil {
  private val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()

  // TODO: Add environment variable override. Document it.
  private const val basePath =
    "https://git.gorence.io/lgorence/foundation/-/jobs/artifacts/main/raw"
  private const val basePathQueryParams = "job=build"
  private const val manifestPath = "build/manifests/update.json"

  fun fetchManifest() = fetchFile(
    getUrl(manifestPath), MapSerializer(String.serializer(), ModuleManifest.serializer()),
  )

  fun getUrl(path: String) = "$basePath/$path?$basePathQueryParams"

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
    val request = HttpRequest
      .newBuilder()
      .GET()
      .uri(URI.create(getUrl(path)))
      .build()

    val response = client.send(
      request,
      HttpResponse.BodyHandlers.ofFile(outPath)
    )
    response.body()
  }
}
