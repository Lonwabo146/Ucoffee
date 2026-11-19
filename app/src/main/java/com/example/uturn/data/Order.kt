package com.example.uturn.data

import org.json.JSONArray
import org.json.JSONObject

/** The cart works in rands (Double); orders store Int cents. Convert once, here. */
fun Double.toCents(): Int = Math.round(this * 100).toInt()

/** Overload for order amounts stored as Int cents; reuses your existing rand-based formatPrice. */
fun formatPrice(cents: Int): String = formatPrice(cents / 100.0)

enum class OrderStatus(val label: String) {
    AWAITING_PAYMENT("Awaiting payment"),
    PLACED("Placed"),
    PREPARING("Preparing"),
    READY_FOR_PICKUP("Ready for pickup"),
    OUT_FOR_DELIVERY("Out for delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

enum class FulfilmentType(val label: String) {
    DELIVERY("Delivery"),
    PICKUP("Pickup")
}

enum class PaymentMethod(val label: String) {
    PAYFAST("PayFast"),
    YOCO("Yoco")
}

data class DeliveryDetails(
    val fullName: String,
    val phone: String,
    val address: String
)

data class OrderLine(
    val productId: Int,
    val name: String,
    val volumeMl: Int,
    val unitPriceCents: Int,
    val quantity: Int
) {
    val lineTotalCents: Int get() = unitPriceCents * quantity
}

data class Order(
    val number: String,              // e.g. "UC-2481" (shown as "#UC-2481")
    val lines: List<OrderLine>,
    val subtotalCents: Int,
    val deliveryCents: Int,
    val totalCents: Int,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val delivery: DeliveryDetails,
    val createdAtMillis: Long,
    val fulfilmentType: FulfilmentType = FulfilmentType.DELIVERY,
    val isPaid: Boolean = false
) {
    val displayNumber: String get() = "#$number"
    val itemCount: Int get() = lines.sumOf { it.quantity }
}

// ---------- JSON (org.json ships with Android, so no new Gradle dependency) ----------

private fun Order.toJson(): JSONObject = JSONObject().apply {
    put("number", number)
    put("lines", JSONArray().apply {
        lines.forEach { l ->
            put(JSONObject().apply {
                put("productId", l.productId)
                put("name", l.name)
                put("volumeMl", l.volumeMl)
                put("unitPriceCents", l.unitPriceCents)
                put("quantity", l.quantity)
            })
        }
    })
    put("subtotalCents", subtotalCents)
    put("deliveryCents", deliveryCents)
    put("totalCents", totalCents)
    put("status", status.name)
    put("paymentMethod", paymentMethod.name)
    put("fullName", delivery.fullName)
    put("phone", delivery.phone)
    put("address", delivery.address)
    put("createdAtMillis", createdAtMillis)
    put("fulfilmentType", fulfilmentType.name)
    put("isPaid", isPaid)
}

private fun orderFromJson(o: JSONObject): Order {
    val linesJson = o.getJSONArray("lines")
    val lines = (0 until linesJson.length()).map { i ->
        val l = linesJson.getJSONObject(i)
        OrderLine(
            productId = l.getInt("productId"),
            name = l.getString("name"),
            volumeMl = l.getInt("volumeMl"),
            unitPriceCents = l.getInt("unitPriceCents"),
            quantity = l.getInt("quantity")
        )
    }
    return Order(
        number = o.getString("number"),
        lines = lines,
        subtotalCents = o.getInt("subtotalCents"),
        deliveryCents = o.getInt("deliveryCents"),
        totalCents = o.getInt("totalCents"),
        status = runCatching { OrderStatus.valueOf(o.getString("status")) }
            .getOrDefault(OrderStatus.PLACED),
        paymentMethod = runCatching { PaymentMethod.valueOf(o.getString("paymentMethod")) }
            .getOrDefault(PaymentMethod.PAYFAST),
        delivery = DeliveryDetails(
            fullName = o.getString("fullName"),
            phone = o.getString("phone"),
            address = o.getString("address")
        ),
        createdAtMillis = o.getLong("createdAtMillis"),
        fulfilmentType = runCatching { FulfilmentType.valueOf(o.getString("fulfilmentType")) }
            .getOrDefault(FulfilmentType.DELIVERY),
        isPaid = o.optBoolean("isPaid", false)
    )
}

internal fun ordersToJson(orders: List<Order>): String =
    JSONArray().apply { orders.forEach { put(it.toJson()) } }.toString()

/** Returns an empty list if nothing is stored or the stored text is corrupt. */
internal fun ordersFromJson(json: String?): List<Order> {
    if (json.isNullOrBlank()) return emptyList()
    return runCatching {
        val arr = JSONArray(json)
        (0 until arr.length()).map { orderFromJson(arr.getJSONObject(it)) }
    }.getOrDefault(emptyList())
}