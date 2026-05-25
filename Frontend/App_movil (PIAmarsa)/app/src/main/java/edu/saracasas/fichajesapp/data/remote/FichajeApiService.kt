package edu.saracasas.fichajesapp.data.remote

import android.content.Context
import android.util.Base64
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import edu.saracasas.fichajesapp.MainActivity.Companion.tokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import kotlinx.coroutines.runBlocking

/**
 * FichajeApiService es un objeto que se encarga de configurar Retrofit para comunicarse con la API de fichajes.
 * Incluye un interceptor para añadir el token de autenticación a cada petición y una función para decodificar el token JWT.
 */
object FichajeApiService {
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    // TokenManager para gestionar el token de autenticación
    fun init(context: Context) {
        tokenManager = TokenManager(context)
    }

    // Interceptor para añadir el token a cada petición
    private val tokenInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        val requestWithToken = originalRequest.newBuilder()
            .header(
                "Authorization",
                "Bearer ${ runBlocking { tokenManager.getAuthToken() }}"
            ) // <-- Aquí es donde se añade el token a cada petición
            .build()

            chain.proceed(requestWithToken)
        }

    // Función para decodificar el token JWT y obtener su payload
    fun decodedToken(): String? {
        val token = runBlocking { tokenManager.getAuthToken() }
        return token?.let {
            val parts = it.split(".")
            if (parts.size == 3) {
                val payload = parts[1]
                String(Base64.decode(payload, android.util.Base64.DEFAULT))
            } else null
        }
    }

    // Configuramos el cliente OkHttp personalizado para usar el interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(tokenInterceptor) // Añadimos el interceptor al cliente
        .build()

    // Configuramos Gson para manejar la deserialización de LocalDateTime
    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
                return LocalDateTime.parse(json.asString) // Aquí es donde el String se vuelve objeto
            }
        }).create()

    // Creamos la instancia de Retrofit usando el cliente personalizado y el Gson configurado
    val apiService: ApiService by lazy {
        Retrofit
            .Builder().baseUrl(BASE_URL)
            .client(okHttpClient) // Usamos el cliente personalizado
            .addConverterFactory(GsonConverterFactory.create(gson)) // Usamos el Gson configurado
            .build()
            .create(ApiService::class.java)
    }
}