package com.example.uturn.data

import com.google.gson.Gson
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/** The ONLY place that knows where the API lives. 10.0.2.2 = the PC, as seen from the Android emulator. */
object ApiConfig {

    const val HOST = "https://ucoffee-cwcfbtarddcmcxfz.southafricanorth-01.azurewebsites.net"
    const val BASE_URL = "$HOST/api/"
}

// ---------- Request / response shapes (field names match the API's camelCase JSON) ----------

data class LoginBody(val email: String, val password: String)

data class RegisterBody(
    val name: String,
    val email: String,
    val password: String,
    val phone: String?
)

data class ShopUser(val id: Int, val name: String, val email: String, val phone: String?)

data class AuthResponse(val token: String, val expiresAtUtc: String?, val user: ShopUser)

data class CatalogSize(val productId: Int, val volumeMl: Int, val price: Double, val inStock: Boolean)

data class CatalogProduct(
    val id: Int,
    val name: String,
    val category: String,
    val description: String?,
    val imageUrl: String?,
    val sizes: List<CatalogSize>
)

data class CreateOrderLineBody(val productId: Int, val quantity: Int)

data class CreateOrderBody(
    val fullName: String,
    val phone: String,
    val address: String,
    val email: String?,
    val paymentMethod: String,
    val fulfilmentType: String,
    val items: List<CreateOrderLineBody>
)


data class ServerOrderLine(
    val productId: Int,
    val name: String,
    val volumeMl: Int,
    val unitPriceCents: Int,
    val quantity: Int
)

data class ServerOrder(
    val number: String,
    val lines: List<ServerOrderLine>,
    val subtotalCents: Int,
    val deliveryCents: Int,
    val totalCents: Int,
    val status: String,
    val paymentMethod: String,
    val fullName: String,
    val phone: String,
    val address: String,
    val createdAtMillis: Long,
    val fulfilmentType: String? = null,
    val paymentStatus: String? = null,
    val paymentUrl: String? = null
)

private data class ErrorBody(val message: String?)

// ---------- Retrofit interface ----------

interface UturnApi {
    @GET("shop/catalog")
    suspend fun catalog(): List<CatalogProduct>

    @POST("shop/login")
    suspend fun login(@Body body: LoginBody): AuthResponse

    @POST("shop/register")
    suspend fun register(@Body body: RegisterBody): AuthResponse

    @POST("shop/orders")
    suspend fun createOrder(@Body body: CreateOrderBody): ServerOrder

    @GET("shop/orders/mine")
    suspend fun myOrders(): List<ServerOrder>
}

// ---------- Client ----------

object ApiClient {
    val api: UturnApi by lazy {
        // BASIC logs only "METHOD url -> status", never headers or bodies, so tokens and passwords stay out of Logcat
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

        val auth = Interceptor { chain ->
            val token = TokenStore.token
            val request = if (token.isNullOrEmpty()) {
                chain.request()
            } else {
                chain.request().newBuilder().header("Authorization", "Bearer $token").build()
            }
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UturnApi::class.java)
    }
}

// ---------- Errors ----------

class ApiException(message: String, val code: Int? = null) : Exception(message)

/** Runs an API call and turns every failure into an ApiException with a message that is safe to show. */
internal suspend fun <T> apiCall(block: suspend () -> T): T =
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiException) {
        throw e
    } catch (e: HttpException) {
        throw ApiException(parseError(e), e.code())
    } catch (e: IOException) {
        throw ApiException("Can't reach the server. Check your connection and that the API is running.")
    } catch (e: Exception) {
        throw ApiException("Something went wrong. Please try again.")
    }

private fun parseError(e: HttpException): String {
    val raw = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
    val fromBody = runCatching { Gson().fromJson(raw, ErrorBody::class.java)?.message }.getOrNull()
    if (!fromBody.isNullOrBlank()) return fromBody
    return when (e.code()) {
        401 -> "Please log in again."
        429 -> "Too many requests. Please wait a minute and try again."
        in 500..599 -> "The server is having trouble. Please try again shortly."
        else -> "Something went wrong. Please try again."
    }
}

// ---------- Image URLs ----------

private val localhostRegex = Regex("^https?://(localhost|127\\.0\\.0\\.1)(:\\d+)?")

/** Product photos uploaded in the admin app come back as https://localhost:5001/...; the emulator must use 10.0.2.2 instead. */
fun fixImageUrl(url: String?): String? =
    url?.takeIf { it.isNotBlank() }?.replace(localhostRegex, ApiConfig.HOST)