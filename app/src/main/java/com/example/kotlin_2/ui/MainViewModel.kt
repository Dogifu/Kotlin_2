package com.example.kotlin_2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_2.data.ImageData
import com.example.kotlin_2.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val _images = MutableStateFlow<List<ImageData>>(emptyList())
    val images: StateFlow<List<ImageData>> = _images

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _loadMoreError = MutableStateFlow<String?>(null)
    val loadMoreError: StateFlow<String?> = _loadMoreError

    private var currentPage = 1
    private val pageSize = 10

    init {
        loadImages()
    }

    fun loadImages() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.fetchImages(page = currentPage, limit = pageSize)
                _images.value = result
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreImages() {
        if (_isLoadingMore.value) return // Предотвращаем повторную загрузку

        viewModelScope.launch {
            _isLoadingMore.value = true
            _loadMoreError.value = null
            try {
                val result = repository.fetchImages(page = currentPage + 1, limit = pageSize)
                _images.value = _images.value + result
                currentPage++
            } catch (e: Exception) {
                _loadMoreError.value = "Ошибка догрузки: ${e.message}"
            } finally {
                _isLoadingMore.value = false
            }
        }
    }
}
