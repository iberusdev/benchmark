package com.example.benchmark.data.repository.ocr

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.example.benchmark.domain.ocr.repository.OCREngine
import com.example.benchmark.domain.ocr.repository.OCRRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

class OCRSpaceRepositoryImpl(
    private val context: Context,
) : OCRRepository {
    override val type: OCREngine = OCREngine.OCR_SPACE
    private val apiKey: String = "helloworld"
    private val url: String = "https://api.ocr.space/parse/image"

    override suspend fun extractTextFromImage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        val base64Image = convertUriToBase64(imageUri)
        val url = URL(url)
        val connection = url.openConnection() as HttpsURLConnection

        connection.setRequestMethod("POST")
        connection.setRequestProperty("User-Agent", "Mozilla/5.0")
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
        connection.doOutput = true

        val postDataParams = JSONObject().apply {
            put("apikey", apiKey)
            put("base64Image", "data:image/jpeg;base64,$base64Image")
            put("language", "spa")
        }

        DataOutputStream(connection.outputStream).use { writer ->
            writer.writeBytes(getPostDataString(postDataParams))
            writer.flush()
        }

        val responseStringBuilder = StringBuilder()
        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
            var inputLine: String?
            while (reader.readLine().also { inputLine = it } != null) {
                responseStringBuilder.append(inputLine)
            }
        }

        return@withContext parseOcrResponse(responseStringBuilder.toString())
    }

    private fun convertUriToBase64(imageUri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Error opening image from Uri.")

        val bytes = inputStream.use { it.readBytes() }
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun getPostDataString(params: JSONObject): String {
        val result = StringBuilder()
        var first = true

        val itr = params.keys()

        while (itr.hasNext()) {
            val key = itr.next()
            val value = params.get(key)

            if (first) first = false
            else result.append("&")

            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
        return result.toString()
    }

    private fun parseOcrResponse(jsonResponse: String): String {
        val jsonObject = JSONObject(jsonResponse)
        val exitCode = jsonObject.optInt("OCRExitCode", -1)

        if (exitCode == 1) {
            val parsedResults = jsonObject.optJSONArray("ParsedResults")
            if (parsedResults != null && parsedResults.length() > 0) {
                val firstResult = parsedResults.getJSONObject(0)
                return firstResult.optString("ParsedText", "")
            }
        }

        val errorMessage = jsonObject.optJSONArray("ErrorMessage")?.optString(0)
            ?: "Unknown error to process image with OCR.Space"
        throw IllegalStateException(errorMessage)
    }

    override suspend fun closeOCR() { }
}