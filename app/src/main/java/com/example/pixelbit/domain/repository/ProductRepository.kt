import com.example.pixelbit.domain.model.Product

interface ProductRepository {
    suspend fun getProductById(productId: String): Result<Product>
}