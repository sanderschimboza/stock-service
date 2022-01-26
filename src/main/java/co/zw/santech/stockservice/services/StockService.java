package co.zw.santech.stockservice.services;

import co.zw.santech.stockservice.models.Order;
import co.zw.santech.stockservice.models.Stock;
import co.zw.santech.stockservice.repository.StockRepository;
import com.google.gson.Gson;
import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StockService {

    private
    final StockRepository stockRepository;

    private final
    ProducerTemplate producerTemplate;

    private final
    Gson gson = new Gson();

    @Autowired
    public StockService(StockRepository stockRepository,
                        ProducerTemplate producerTemplate) {
        this.stockRepository = stockRepository;
        this.producerTemplate = producerTemplate;
    }

    public Integer saveStockItem(Stock stock) {
        try {
            this.stockRepository.save(stock);
            return 200;
        } catch (Exception e) {
            return 403;
        }
    }

    public List<Stock> findAllStockItems() {
        return this.stockRepository.findAll();
    }

    public void removeStockItem(Integer id) {
        this.stockRepository.deleteById(id);
    }

    @Transactional
    public Integer processStockItem(Order order) {

        Optional<Stock> stockItem = stockRepository
                .findStockByProductIdAndExpiryDateIsGreaterThan(
                        order.getProductId(), LocalDateTime.now().toString());

        if (stockItem.isPresent()) {
            log.info("NEW ORDER STOCK ITEM: {}", stockItem);
            Long countStockItemQty = stockItem.get().getTotal();

            if (countStockItemQty >= order.getQuantity()) {
                order.setStatus("PROCESSING");
                order.setPrice(stockItem.get().getPrice());
                order.setTotalPrice(stockItem.get().getPrice()
                        .multiply(BigDecimal.valueOf(order.getQuantity())));
                stockItem.get().setTotal(stockItem.get().getTotal() - order.getQuantity());
                sendMessageToQueue(order);
                return 200;

            } else {
                log.warn("OUT OF STOCK! " + countStockItemQty);
                order.setPrice(stockItem.get().getPrice());
                order.setTotalPrice(BigDecimal.valueOf(0.0));
                order.setAvailable(stockItem.get().getTotal());
                order.setStatus("OUT OF STOCK");
                sendMessageToQueue(order);
                return 201;
            }
        }
        log.warn("EXPIRED PRODUCT!");
        order.setPrice(BigDecimal.valueOf(0.0));
        order.setTotalPrice(BigDecimal.valueOf(0.0));
        order.setStatus("OUT OF STOCK");
        sendMessageToQueue(order);
        return 404;
    }

    public Optional<Stock> findStockItemById(Integer prodId) {
        return stockRepository.findById(prodId);
    }

    @NotNull
    public void sendMessageToQueue(Order order) {
        try {
            producerTemplate.sendBody("activemq:queue:stock.queue",
                    gson.toJson(order));
            log.info("Message sent to stock.queue successfully {}", order);

        } catch (Exception e) {
            log.error("Could not send Payload to Queue! {}", e.getMessage());
        }
    }
}
