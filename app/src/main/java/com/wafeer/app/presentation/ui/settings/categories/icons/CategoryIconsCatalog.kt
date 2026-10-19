package com.wafeer.app.presentation.ui.settings.categories.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.BakeryDining
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Commute
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Egg
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Kitchen
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.LocalPharmacy
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryIconItem(
    val id: String,
    val vector: ImageVector,
    val categoryGroup: String, // e.g. "Food", "Transport", "Bills", "Family"
    val tags: List<String>
)

object CategoryIconsCatalog {

    val icons: List<CategoryIconItem> = listOf(
        // Food & Dairy & Groceries
        CategoryIconItem("ic_grocery", Icons.Rounded.LocalGroceryStore, "Food", listOf("grocery", "بقالة", "سوبرماركت", "سوق")),
        CategoryIconItem("ic_cart", Icons.Rounded.ShoppingCart, "Food", listOf("cart", "عربة", "مشتريات", "شراء")),
        CategoryIconItem("ic_milk", Icons.Rounded.Egg, "Food", listOf("milk", "لبن", "حليب", "زبادي", "ألبان", "dairy", "cheese", "جبنة")),
        CategoryIconItem("ic_restaurant", Icons.Rounded.Restaurant, "Food", listOf("restaurant", "مطعم", "أكل", "طعام", "غداء", "عشاء")),
        CategoryIconItem("ic_fastfood", Icons.Rounded.Fastfood, "Food", listOf("fastfood", "وجبات", "سريعة", "برجر", "food")),
        CategoryIconItem("ic_bakery", Icons.Rounded.BakeryDining, "Food", listOf("bakery", "مخبز", "عيش", "خبز", "حلويات", "فطائر")),
        CategoryIconItem("ic_cafe", Icons.Rounded.LocalCafe, "Food", listOf("cafe", "قهوة", "مقهى", "كافيه", "شاي", "tea")),
        CategoryIconItem("ic_coffee", Icons.Rounded.Coffee, "Food", listOf("coffee", "قهوة", "بن", "اسبريسو")),
        CategoryIconItem("ic_kitchen", Icons.Rounded.Kitchen, "Food", listOf("kitchen", "مطبخ", "ثلاجة", "مستلزمات منزلية")),
        CategoryIconItem("ic_lunch", Icons.Rounded.LunchDining, "Food", listOf("lunch", "سندوتش", "فطار", "غداء")),

        // Transportation & Fuel
        CategoryIconItem("ic_car", Icons.Rounded.DirectionsCar, "Transport", listOf("car", "سيارة", "عربية", "مواصلات")),
        CategoryIconItem("ic_gas", Icons.Rounded.LocalGasStation, "Transport", listOf("gas", "بنزين", "وقود", "محطة")),
        CategoryIconItem("ic_bus", Icons.Rounded.DirectionsBus, "Transport", listOf("bus", "باص", "أتوبيس", "ميكروباص")),
        CategoryIconItem("ic_commute", Icons.Rounded.Commute, "Transport", listOf("commute", "تنقل", "مترو", "قطار")),
        CategoryIconItem("ic_flight", Icons.Rounded.Flight, "Transport", listOf("flight", "طيران", "سفر", "تذاكر")),

        // Family & Kids & Education
        CategoryIconItem("ic_kids", Icons.Rounded.ChildCare, "Family", listOf("kids", "أطفال", "أولاد", "مصروف", "بيبي", "baby", "ابني")),
        CategoryIconItem("ic_school", Icons.Rounded.School, "Family", listOf("school", "مدرسة", "جامعة", "دروس", "تعليم", "كتب", "مصاريف")),
        CategoryIconItem("ic_home", Icons.Rounded.Home, "Family", listOf("home", "بيت", "منزل", "إيجار", "مصروف البيت")),

        // Bills & Utilities & Subscriptions
        CategoryIconItem("ic_receipt", Icons.AutoMirrored.Rounded.ReceiptLong, "Bills", listOf("receipt", "فاتورة", "فواتير", "إيصال")),
        CategoryIconItem("ic_bolt", Icons.Rounded.Bolt, "Bills", listOf("electricity", "كهرباء", "نور", "طاقة")),
        CategoryIconItem("ic_water", Icons.Rounded.WaterDrop, "Bills", listOf("water", "مياه", "مية", "فاتورة المياه")),
        CategoryIconItem("ic_wifi", Icons.Rounded.Wifi, "Bills", listOf("wifi", "واي فاي", "نت", "إنترنت", "اشتراك")),
        CategoryIconItem("ic_phone", Icons.Rounded.Smartphone, "Bills", listOf("phone", "موبايل", "هاتف", "شحن", "رصيد", "اتصالات")),
        CategoryIconItem("ic_card", Icons.Rounded.CreditCard, "Bills", listOf("card", "بطاقة", "فيزا", "ائتمان", "قسط")),
        CategoryIconItem("ic_bank", Icons.Rounded.AccountBalance, "Bills", listOf("bank", "بنك", "حساب", "تحويل", "سداد")),

        // Health & Wellness
        CategoryIconItem("ic_hospital", Icons.Rounded.LocalHospital, "Health", listOf("hospital", "مستشفى", "طبيب", "دكتور", "كشف")),
        CategoryIconItem("ic_pharmacy", Icons.Rounded.LocalPharmacy, "Health", listOf("pharmacy", "صيدلية", "علاج", "دواء")),
        CategoryIconItem("ic_medication", Icons.Rounded.Medication, "Health", listOf("medicine", "أدوية", "حبوب", "فيتامينات")),
        CategoryIconItem("ic_fitness", Icons.Rounded.FitnessCenter, "Health", listOf("gym", "جيم", "رياضة", "لياقة")),
        CategoryIconItem("ic_run", Icons.AutoMirrored.Rounded.DirectionsRun, "Health", listOf("run", "جري", "تمرين")),
        CategoryIconItem("ic_spa", Icons.Rounded.Spa, "Health", listOf("spa", "عناية", "حلاقة", "صالون", "مظهر")),

        // Shopping & Entertainment
        CategoryIconItem("ic_shopping", Icons.Rounded.ShoppingBag, "Shopping", listOf("shopping", "تسوق", "شنطة", "ملابس", "شراء")),
        CategoryIconItem("ic_mall", Icons.Rounded.LocalMall, "Shopping", listOf("mall", "مول", "شراء", "تجزئة")),
        CategoryIconItem("ic_game", Icons.Rounded.SportsEsports, "Shopping", listOf("games", "ألعاب", "بلايستيشن", "ترفيه")),
        CategoryIconItem("ic_movie", Icons.Rounded.Movie, "Shopping", listOf("movie", "سينما", "فيلم", "سهرة", "ترفيه"))
    )

    fun getIcon(id: String?): ImageVector {
        if (id == null) return Icons.Rounded.LocalGroceryStore
        return icons.firstOrNull { it.id == id }?.vector ?: Icons.Rounded.LocalGroceryStore
    }

    fun searchIcons(query: String, groupFilter: String? = null): List<CategoryIconItem> {
        val trimmed = query.trim().lowercase()
        return icons.filter { item ->
            val matchesGroup = groupFilter == null || item.categoryGroup.equals(groupFilter, ignoreCase = true)
            val matchesQuery = trimmed.isEmpty() || item.tags.any { it.contains(trimmed, ignoreCase = true) }
            matchesGroup && matchesQuery
        }
    }
}
