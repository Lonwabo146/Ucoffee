package com.example.uturn.data

/** An order the server just created, plus the PayFast page the customer must pay on. */
data class PlacedOrder(val order: Order, val paymentUrl: String?)

/** Every call the app makes to the API goes through here, already converted to the app's own models. */
object ShopRepository {

    suspend fun loadCatalog(): List<Product> = apiCall {
        ApiClient.api.catalog().map { it.toProduct() }
    }

    suspend fun login(email: String, password: String): ShopUser = apiCall {
        val res = ApiClient.api.login(LoginBody(email.trim(), password))
        TokenStore.save(res.token)
        res.user
    }

    suspend fun register(name: String, email: String, password: String, phone: String): ShopUser = apiCall {
        val res = ApiClient.api.register(
            RegisterBody(name.trim(), email.trim(), password, phone.trim().ifBlank { null })
        )
        TokenStore.save(res.token)
        res.user
    }

    /** The server recomputes every price from its own database; the totals shown in the app are never trusted. */
    suspend fun createOrder(
        lines: List<CartLine>,
        method: PaymentMethod,
        delivery: DeliveryDetails,
        fulfilment: FulfilmentType
    ): PlacedOrder = apiCall {
        if (lines.isEmpty() || lines.any { it.sizeId <= 0 }) {
            throw ApiException("Your cart is out of date. Please clear it and add your drinks again.")
        }
        val body = CreateOrderBody(
            fullName = delivery.fullName,
            phone = delivery.phone,
            address = delivery.address,
            email = null,
            paymentMethod = method.name,
            fulfilmentType = fulfilment.name,
            items = lines.map { CreateOrderLineBody(it.sizeId, it.quantity) }
        )
        val res = ApiClient.api.createOrder(body)
        PlacedOrder(res.toOrder(), res.paymentUrl)
    }

    suspend fun myOrders(): List<Order> = apiCall {
        ApiClient.api.myOrders().map { it.toOrder() }
    }

    fun logout() = TokenStore.clear()
}

private fun CatalogProduct.toProduct(): Product = Product(
    id = id,
    name = name,
    category = runCatching { ProductCategory.valueOf(category) }.getOrDefault(ProductCategory.COFFEE),
    description = description.orEmpty(),
    imageRes = drawableForProduct(name),
    imageUrl = fixImageUrl(imageUrl),
    sizes = sizes.map { SizeOption(volumeMl = it.volumeMl, price = it.price, sizeId = it.productId) }
)

private fun ServerOrder.toOrder(): Order = Order(
    number = number,
    lines = lines.map {
        OrderLine(
            productId = it.productId,
            name = it.name,
            volumeMl = it.volumeMl,
            unitPriceCents = it.unitPriceCents,
            quantity = it.quantity
        )
    },
    subtotalCents = subtotalCents,
    deliveryCents = deliveryCents,
    totalCents = totalCents,
    status = when (status) {
        "AWAITING_PAYMENT" -> OrderStatus.AWAITING_PAYMENT
        "PREPARING" -> OrderStatus.PREPARING
        "READY_FOR_PICKUP" -> OrderStatus.READY_FOR_PICKUP
        "OUT_FOR_DELIVERY" -> OrderStatus.OUT_FOR_DELIVERY
        "DELIVERED" -> OrderStatus.DELIVERED
        "CANCELLED" -> OrderStatus.CANCELLED
        else -> OrderStatus.PLACED
    },
    paymentMethod = runCatching { PaymentMethod.valueOf(paymentMethod) }.getOrDefault(PaymentMethod.PAYFAST),
    delivery = DeliveryDetails(fullName = fullName, phone = phone, address = address),
    createdAtMillis = createdAtMillis,
    fulfilmentType = if (fulfilmentType == "PICKUP") FulfilmentType.PICKUP else FulfilmentType.DELIVERY,
    isPaid = paymentStatus == "Paid"
)