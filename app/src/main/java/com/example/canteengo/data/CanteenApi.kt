package com.example.canteengo.data

import com.example.canteengo.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class ApiException(val status: Int, message: String) : IOException(message)

data class ServerSession(val token: String, val username: String, val role: String)

/** Runs requests off the UI thread. Tokens stay in memory and are never logged. */
class CanteenApi(address: String) {
    private val base: String

    init {
        val uri = try { URI(address.trim()) } catch (_: Exception) {
            throw IllegalArgumentException("Enter a valid server address")
        }
        require(uri.scheme == "https" || (BuildConfig.DEBUG && uri.scheme == "http")) {
            "Use an HTTPS server address"
        }
        require(!uri.host.isNullOrBlank() && uri.userInfo == null && uri.query == null &&
            uri.fragment == null && (uri.path.isNullOrEmpty() || uri.path == "/")) {
            "Use only the server address and port, for example http://10.0.2.2:8000"
        }
        base = uri.toString().trimEnd('/')
    }

    private suspend fun request(method: String, path: String, token: String? = null, body: JSONObject? = null): String =
        withContext(Dispatchers.IO) {
            val connection = URL(base + path).openConnection() as HttpURLConnection
            try {
                connection.requestMethod = method
                connection.connectTimeout = 8000
                connection.readTimeout = 10000
                connection.instanceFollowRedirects = false
                connection.setRequestProperty("Accept", "application/json")
                token?.let { connection.setRequestProperty("Authorization", "Bearer $it") }
                if (body != null) {
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
                }
                val code = connection.responseCode
                val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                val result = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                if (code !in 200..299) {
                    val detail = runCatching { JSONObject(result).opt("detail") }.getOrNull()
                    // Display backend validation messages without copying input values
                    // (which may include passwords) from the validation response.
                    val validation = if (detail is JSONArray) {
                        List(detail.length()) { index ->
                            detail.optJSONObject(index)?.optString("msg").orEmpty()
                        }.filter { it.isNotBlank() }.distinct().joinToString("\n")
                    } else ""
                    val message = when {
                        detail is String -> detail
                        validation.isNotBlank() -> validation
                        code == 422 -> "Check your details and try again."
                        code == 503 -> "The server is temporarily unavailable."
                        else -> "Server request failed ($code)"
                    }
                    throw ApiException(code, message)
                }
                result
            } finally {
                connection.disconnect()
            }
        }

    suspend fun register(username: String, password: String) {
        request("POST", "/api/auth/register", body = JSONObject().put("username", username).put("password", password))
    }

    suspend fun login(username: String, password: String): ServerSession {
        val json = JSONObject(request("POST", "/api/auth/login",
            body = JSONObject().put("username", username).put("password", password)))
        return ServerSession(json.getString("access_token"), json.getString("username"), json.getString("role"))
    }

    suspend fun logout(token: String) { request("POST", "/api/auth/logout", token) }

    suspend fun menu(token: String): List<FoodItem> {
        val array = JSONArray(request("GET", "/api/menu", token))
        return List(array.length()) { food(array.getJSONObject(it)) }
    }

    suspend fun orders(token: String): List<Order> {
        val array = JSONArray(request("GET", "/api/orders", token))
        return List(array.length()) { order(array.getJSONObject(it)) }
    }

    suspend fun place(token: String, customer: String, items: List<CartItem>, requestId: String): Order {
        val lines = JSONArray()
        items.forEach { lines.put(JSONObject().put("food_id", it.food.id).put("quantity", it.quantity)) }
        val body = JSONObject().put("customer", customer).put("items", lines).put("request_id", requestId)
        return order(JSONObject(request("POST", "/api/orders", token, body)))
    }

    suspend fun advance(token: String, number: String, next: OrderStatus): Order =
        order(JSONObject(request("PATCH", "/api/orders/$number/status", token,
            JSONObject().put("status", next.name))))

    suspend fun report(token: String, format: String): String =
        request("GET", "/api/reports/orders?format=$format", token)

    suspend fun updateStock(token: String, foodId: String, stock: Int) {
        request("PATCH", "/api/menu/$foodId/stock", token, JSONObject().put("stock", stock))
    }

    private fun food(json: JSONObject) = FoodItem(
        json.getString("id"), json.getString("name"), json.getString("description"),
        json.getString("category"), json.getInt("price_centavos"), json.getString("emoji")
    )

    private fun order(json: JSONObject): Order {
        val lines = json.getJSONArray("items")
        return Order(json.getString("number"), json.getString("customer"),
            List(lines.length()) {
                val line = lines.getJSONObject(it)
                CartItem(food(line.getJSONObject("food")), line.getInt("quantity"))
            }, OrderStatus.valueOf(json.getString("status")))
    }
}
