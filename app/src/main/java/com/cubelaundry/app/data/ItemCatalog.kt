package com.cubelaundry.app.data

data class CategoryGroup(val title: String, val itemNames: List<String>)
data class GenderSection(val title: String, val categories: List<CategoryGroup>)

val SERVICE_TYPES = listOf("Wash + Iron", "Iron", "Urgent")

val MEN_SECTION = GenderSection(
    title = "Men",
    categories = listOf(
        CategoryGroup(
            "Suits & Formal Wear",
            listOf("Cotton Suit", "Silk Suit", "Boski Suit", "Coat", "Sweater", "Shawl")
        ),
        CategoryGroup(
            "Everyday Wear",
            listOf("Pant & Shirt", "Shalwar (Large)", "Shirt", "Vest (Banyan)", "Cap")
        ),
        CategoryGroup(
            "Home Linen",
            listOf("Pillow Cover", "Towel", "Bedsheet (Chadar)", "Blanket (Kambal)", "Rilli (Quilt)")
        ),
        CategoryGroup(
            "Curtains",
            listOf("Double Curtain", "Single Curtain")
        )
    )
)

val LADIES_SECTION = GenderSection(
    title = "Ladies",
    categories = listOf(
        CategoryGroup(
            "Suits & Formal Wear",
            listOf("Kameez", "Shalwar", "Dupatta", "Chiffon Suit", "Lawn Suit", "Embroidered Suit")
        ),
        CategoryGroup(
            "Everyday Wear",
            listOf("Kurti", "Tunic", "Trouser", "Skirt")
        ),
        CategoryGroup(
            "Home Linen",
            listOf("Bedsheet", "Pillow Cover", "Towel")
        )
    )
)

val ALL_SECTIONS = listOf(MEN_SECTION, LADIES_SECTION)
