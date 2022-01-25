package co.zw.santech.stockservice.controllers;

import co.zw.santech.stockservice.dto.StockDTO;
import co.zw.santech.stockservice.models.Stock;
import co.zw.santech.stockservice.services.StockService;
import lombok.extern.slf4j.Slf4j;
import org.jsondoc.core.annotation.*;
import org.jsondoc.core.pojo.ApiStage;
import org.jsondoc.core.pojo.ApiVisibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
@RestController
@Api(description = "Methods for managing stock entries", name = "stock services", group = "Stocks", visibility = ApiVisibility.PUBLIC, stage = ApiStage.RC)
@ApiVersion(since = "1.0", until = "1.5")
@RequestMapping("/stock")
public class StockController {

    private
    final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @RequestMapping(value = "/updateItem", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiMethod(description = "Method to Update an already existing stock item")
    @ApiHeaders(headers = {
            @ApiHeader(name = ACCEPT),
            @ApiHeader(name = CONTENT_TYPE)
    })
    @ResponseStatus(HttpStatus.OK)
    @ApiResponseObject
    public Stock updateStockItem(@RequestBody StockDTO stockDTO) {

        Optional<Stock> stockPresent = stockService.findStockItemById(stockDTO.getProductId());
        if (stockPresent.isPresent()) {
            stockPresent.get().setProductId(stockDTO.getProductId());
            stockPresent.get().setProductName(stockDTO.getProductName());
            stockPresent.get().setBarcode(stockDTO.getBarcode());
            stockPresent.get().setQuantity(stockDTO.getQuantity());
            stockPresent.get().setCategory(stockDTO.getCategory());
            stockPresent.get().setNarration("STOCK EDITED");
            stockPresent.get().setTotal(stockDTO.getQuantity() + stockPresent.get().getTotal());

            Integer httpResponse = this.stockService.
                    saveStockItem(stockPresent.get());
            if (httpResponse == 200) {
                log.info("Stock Item was updated successfully -) {}", stockPresent);
            } else {
                log.error("There was an error trying to update Stock Item! {}", stockPresent);
            }
            return stockPresent.get();
        } else {
            return null;
        }
    }

    @ApiMethod(description = "Method to Find all stock items in db")
    @RequestMapping(value = "/findAllItems", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.ALL_VALUE)
    public List<Stock> findAllStockItems() {
        List<Stock> stockList = this.stockService.findAllStockItems();
        log.info("Stock Size: {}", stockList.size());
        return stockList;
    }

    @RequestMapping(value = "/removeItem/{id}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiMethod(description = "Method to Delete single Stock Item", responsestatuscode = "200 - OK")
    @ApiHeaders(headers = {
            @ApiHeader(name = ACCEPT),
            @ApiHeader(name = CONTENT_TYPE)
    })
    @ResponseStatus(HttpStatus.OK)
    @ApiResponseObject
    public String removeStockItem(@PathVariable("id") Integer id) {
        log.info("Removing StockItem with Id: {}", id);
        stockService.removeStockItem(id);
        return "Stock Item Deleted: " + id;
    }

    @RequestMapping(value = "/findItem/{prodId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.ALL_VALUE)
    @ApiMethod(description = "Method to Find a single Stock Item", responsestatuscode = "200 - OK")
    @ApiHeaders(headers = {
            @ApiHeader(name = ACCEPT),
            @ApiHeader(name = CONTENT_TYPE)
    })
    @ResponseStatus(HttpStatus.OK)
    @ApiResponseObject
    public Object findItem(@PathVariable("prodId") Integer prodId) {
        Optional<Stock> stock = stockService.findStockItemById(prodId);
        if (stock.isPresent()) {
            return stock;
        }
        return null;
    }
}
