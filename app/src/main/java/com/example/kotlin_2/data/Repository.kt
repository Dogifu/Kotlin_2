package com.example.kotlin_2.data

class Repository(private val apiService: ApiService) {
    suspend fun fetchImages(page: Int, limit: Int): List<ImageData> {
        return apiService.getImages(page, limit)
    }
}
