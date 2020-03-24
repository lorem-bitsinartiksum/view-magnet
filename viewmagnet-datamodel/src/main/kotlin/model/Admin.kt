package model

data class AdminDTO(val admin: Admin? = null)

data class Admin(val id: Long? = null,
                val email: String,
                val token: String? = null,
                val username: String? = null,
                val password: String? = null,
                val phone: String? = null)
