package co.zw.santech.stockservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "stock")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock
        implements Serializable {
    @Id
    private Integer productId;
    private String productName;
    private String barcode;
    private String expiryDate;
    private String dateAdded;
    private String category;
    private String narration;
    private Long quantity;
    private BigDecimal price;
    private Long total;
}
