package cloud.kubelet.foundation.core.update

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object UpdateUtil {
  private val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()

  // TODO: Add environment variable override. Document it.
  private const val manifestUrl =
    "https://git.gorence.io/lgorence/foundation/-/jobs/artifacts/main/raw/build/manifests/update.json?job=build"

  fun fetchManifest() = fetchFile(
    manifestUrl, MapSerializer(String.serializer(), ModuleManifest.serializer()),
  )

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
}
