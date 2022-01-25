package co.zw.santech.stockservice.dto;

import lombok.Data;

@Data
public class StockDTO {
    private Integer productId;
    private String productName;
    private String barcode;
    private String expiryDate;
    private String dateAdded;
    private String category;
    private String narration;
    private Long quantity;
}
