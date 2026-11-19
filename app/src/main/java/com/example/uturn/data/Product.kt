package com.example.uturn.data

import com.example.uturn.R

enum class ProductCategory(val label: String) {
    COFFEE("Coffees"),
    TEA("Tea"),
    COLD("Cold")
}

/** sizeId = the API's id for this size of the drink (what the server needs when ordering). */
data class SizeOption(val volumeMl: Int, val price: Double, val sizeId: Int = 0)

data class Product(
    val id: Int,
    val name: String,
    val category: ProductCategory,
    val description: String,
    val imageRes: Int? = null,     // bundled photo — takes priority
    val imageUrl: String? = null,  // photo uploaded through the admin app
    val sizes: List<SizeOption>
) {
    val displaySize: SizeOption
        get() = sizes.firstOrNull { it.volumeMl == 350 } ?: sizes.first()
}

fun formatPrice(price: Double): String =
    if (price % 1.0 == 0.0) "R${price.toInt()}" else "R${"%.2f".format(price)}"

/** The API doesn't send image files for the seed drinks, so match the bundled photos by name. */
fun drawableForProduct(name: String): Int? = when (name.trim().lowercase()) {
    "espresso" -> R.drawable.espresso
    "red cappuccino", "red cuppuccino" -> R.drawable.red_cappuccino
    "flat white" -> R.drawable.flat_white
    "chai latte" -> R.drawable.chai_latte
    "cappuccino" -> R.drawable.cappuccino
    "cafe latte", "café latte" -> R.drawable.cafe_latte
    "iced americano" -> R.drawable.iced_americano
    "iced mocha" -> R.drawable.iced_mocha
    "rooibos latte" -> R.drawable.rooibos_latte
    "green tea" -> R.drawable.green_tea
    else -> null
}

/** Holds the menu most recently loaded from the API (filled by CatalogViewModel). */
object ProductRepository {
    private var cache: List<Product> = emptyList()

    val products: List<Product> get() = cache

    fun set(list: List<Product>) {
        cache = list
    }

    fun byId(id: Int): Product? = cache.firstOrNull { it.id == id }
}
/*import com.example.uturn.R

enum class ProductCategory(val label: String) {
    COFFEE("Coffees"),
    TEA("Tea"),
    COLD("Cold")
}

data class SizeOption(val volumeMl: Int, val price: Double)

data class Product(
    val id: Int,
    val name: String,
    val category: ProductCategory,
    val description: String,
    val imageRes: Int? = null,     // bundled photo (dev) — takes priority
    val imageUrl: String? = null,  // used later when photos come from the Azure API
    val sizes: List<SizeOption>
) {
    val displaySize: SizeOption
        get() = sizes.firstOrNull { it.volumeMl == 350 } ?: sizes.first()
}

fun formatPrice(price: Double): String =
    if (price % 1.0 == 0.0) "R${price.toInt()}" else "R${"%.2f".format(price)}"

object ProductRepository {
    val products: List<Product> = listOf(
        Product(1, "Espresso", ProductCategory.COFFEE,
            "Short, intense and full-bodied — a pure shot of our House Blend.",
            imageRes = R.drawable.espresso,
            sizes = listOf(SizeOption(250, 20.0), SizeOption(350, 25.0), SizeOption(500, 30.0))),
        Product(2, "Red Cuppuccino", ProductCategory.COFFEE,
            "Our cappuccino with a dusting of red cocoa on a deep foam cap.",
            imageRes = R.drawable.red_cappuccino,
            sizes = listOf(SizeOption(250, 35.0), SizeOption(350, 40.0), SizeOption(500, 45.0))),
        Product(3, "Flat White", ProductCategory.COFFEE,
            "A velvety, rich espresso drink crafted from our small-batch House Blend — smooth caramel and warm toasted nut notes.",
            imageRes = R.drawable.flat_white,
            sizes = listOf(SizeOption(250, 30.0), SizeOption(350, 35.0), SizeOption(500, 40.0))),
        Product(4, "Chai Latte", ProductCategory.TEA,
            "Spiced black tea blended with steamed milk and honey.",
            imageRes = R.drawable.chai_latte,
            sizes = listOf(SizeOption(250, 30.0), SizeOption(350, 35.0), SizeOption(500, 40.0))),
        Product(5, "Cappuccino", ProductCategory.COFFEE,
            "Classic thirds — espresso, steamed milk, and thick foam.",
            imageRes = R.drawable.cappuccino,
            sizes = listOf(SizeOption(250, 27.0), SizeOption(350, 32.0), SizeOption(500, 37.0))),
        Product(6, "Cafe Latte", ProductCategory.COFFEE,
            "Mellow espresso under a generous layer of silky milk.",
            imageRes = R.drawable.cafe_latte,
            sizes = listOf(SizeOption(250, 25.0), SizeOption(350, 30.0), SizeOption(500, 35.0))),
        Product(7, "Iced Americano", ProductCategory.COLD,
            "Double espresso over ice, topped with cold water.",
            imageRes = R.drawable.iced_americano,
            sizes = listOf(SizeOption(250, 25.0), SizeOption(350, 30.0), SizeOption(500, 35.0))),
        Product(8, "Iced Mocha", ProductCategory.COLD,
            "Espresso, chocolate and cold milk over ice — dessert in a cup.",
            imageRes = R.drawable.iced_mocha,
            sizes = listOf(SizeOption(250, 33.0), SizeOption(350, 38.0), SizeOption(500, 43.0))),
        Product(9, "Rooibos Latte", ProductCategory.TEA,
            "South African red bush tea with steamed milk — naturally caffeine free.",
            imageRes = R.drawable.rooibos_latte,
            sizes = listOf(SizeOption(250, 25.0), SizeOption(350, 30.0), SizeOption(500, 35.0))),
        Product(10, "Green Tea", ProductCategory.TEA,
            "A light, antioxidant-rich green tea served piping hot.",
            imageRes = R.drawable.green_tea,
            sizes = listOf(SizeOption(250, 20.0), SizeOption(350, 25.0), SizeOption(500, 30.0)))
    )

    fun byId(id: Int): Product? = products.firstOrNull { it.id == id }
}*/