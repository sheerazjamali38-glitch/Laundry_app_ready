package com.cubelaundry.app.api

import retrofit2.http.*
import com.cubelaundry.app.models.*

interface ApiService {
    @GET("api_order.php")
    suspend fun getRates(
        @Query("action") action: String = "rates"
    ): RatesResponse

    @POST("api_order.php")
    suspend fun placeOrder(
        @Query("action") action: String = "place",
        @Body order: OrderRequest
    ): OrderResponse

    @GET("api_order.php")
    suspend fun getInvoice(
        @Query("action") action: String = "invoice",
        @Query("invoice") invoice: String
    ): InvoiceResponse

    @GET("api_order.php")
    suspend fun getHistory(
        @Query("action") action: String = "history",
        @Query("mobile") mobile: String
    ): HistoryResponse
}
