package com.wafeer.app.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val isHidden: Boolean = false,
    val usageCount: Int = 0,
    val lastUsedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val parentId: Long? = null,
    val nameResKey: String? = null,
    val iconName: String? = null
) {
    companion object {
        fun create(
            name: String,
            isHidden: Boolean = false,
            usageCount: Int = 0,
            lastUsedAt: Long? = null,
            createdAt: Long = System.currentTimeMillis(),
            parentId: Long? = null,
            nameResKey: String? = null,
            iconName: String? = null
        ): Category = Category(
            id = 0,
            name = name,
            isHidden = isHidden,
            usageCount = usageCount,
            lastUsedAt = lastUsedAt,
            createdAt = createdAt,
            parentId = parentId,
            nameResKey = nameResKey,
            iconName = iconName
        )
    }
}