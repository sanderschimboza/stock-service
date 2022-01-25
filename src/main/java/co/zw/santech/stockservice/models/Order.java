package co.zw.santech.stockservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order
        implements Serializable {
    private static final long serialVersionUID = 7523967970034938905L;
    private Long orderId;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String date;
    private Integer productId;
    private Integer quantity;
    private String status;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private Long available;
}
