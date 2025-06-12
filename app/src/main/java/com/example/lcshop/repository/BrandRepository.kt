import com.example.lcshop.config.RetrofitInstance
import com.example.lcshop.config.RetrofitInstance.productApi
import com.example.lcshop.data.model.Brand
import com.example.lcshop.repository.ProductRepository
import com.example.lcshop.viewmodel.ProductViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BrandsRepository {
    private val BrandApiService = RetrofitInstance.brandApi
    suspend fun getBrands(): List<Brand> = withContext(Dispatchers.IO) {
        try {
            val response = BrandApiService.getAllBrands()
            if (response.isSuccessful) {
                response.body()?.brands ?: emptyList()
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}

