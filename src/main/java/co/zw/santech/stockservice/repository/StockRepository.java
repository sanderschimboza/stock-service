package co.zw.santech.stockservice.repository;

import co.zw.santech.stockservice.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    Optional<Stock> findStockByProductIdAndExpiryDateIsGreaterThan(
            Integer productId, String expiryDate);
}
