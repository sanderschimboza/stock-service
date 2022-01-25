package co.zw.santech.stockservice.routes;

import co.zw.santech.stockservice.models.Order;
import co.zw.santech.stockservice.services.StockService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class OrdersConsumer extends RouteBuilder {

    private final
    StockService stockService;

    @Autowired
    public OrdersConsumer(StockService stockService) {
        this.stockService = stockService;
    }

    @Override
    public void configure() {
        from("activemq:queue:orders.queue")
                .unmarshal(new JacksonDataFormat(Order.class))
                .process(exchange -> {
                    Order order = exchange.getIn().getBody(Order.class);
                    log.info("Received NEW ORDER payload: {}", order);
                  Integer res =  stockService.processStockItem(order);
                  log.info(res.toString());
                    exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE,
                            HttpStatus.ACCEPTED);
                });
    }
}
