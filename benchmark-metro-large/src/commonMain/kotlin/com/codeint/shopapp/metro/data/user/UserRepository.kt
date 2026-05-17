package com.codeint.shopapp.metro.data.user

import com.codeint.shopapp.metro.domain.user.*

interface UserRepository {
    fun getAll(request: UserRequest = UserRequest()): PagedResult<UserDomainModel>
    fun getById(id: String): UserDomainModel?
    fun create(model: UserDomainModel): UserDomainModel
    fun update(id: String, model: UserDomainModel): UserDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<UserDomainModel>
    fun clearCache()
}
