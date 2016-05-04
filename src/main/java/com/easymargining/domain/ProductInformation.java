package com.easymargining.domain;

import com.fasterxml.jackson.databind.ser.std.StdArraySerializers;
import com.sleepycat.bind.serial.SerialBase;
import lombok.*;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rjean030116 on 03/05/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductInformation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productId;
    private Double tickSize;
    private Double tickValue;
    private String currency;
    private String marginStyle;
    private String productName;
    private String bloombergId;
    private String bloombergUrl;
    private String isin;

    private List<ProductPrices> futuresPrices;

    private List<ProductPrices> callPrices;
    private List<ProductPrices> putPrices;
    public ProductInformation(List<Product> products) {
        Assert.notNull(products);
        this.productId = products.get(0).getProductId();
        this.tickSize = products.get(0).getTickSize();
        this.tickValue = products.get(0).getTickValue();
        this.currency = products.get(0).getCurrency();
        this.marginStyle = products.get(0).getMarginStyle();
        this.productName = products.get(0).getProductName();
        this.bloombergId = products.get(0).getBloombergId();
        this.bloombergUrl = products.get(0).getBloombergUrl();
        this.isin = products.get(0).getIsin();

        if (products.get(0).getInstrumentType().equals("Option")) {
            callPrices = new ArrayList<>();
            putPrices = new ArrayList<>();
            for (Product option : products) {
                if (option.getOptionType().equals("CALL")) {
                    callPrices.add(new ProductPrices(
                        option.getMaturityDate(),
                        option.getExercisePrice(),
                        option.getSettlementPrice()));
                } else {
                    putPrices.add(new ProductPrices(
                        option.getMaturityDate(),
                        option.getExercisePrice(),
                        option.getSettlementPrice()));
                }
            }
        } else {
            futuresPrices = new ArrayList<>();
            for (Product future : products) {
                futuresPrices.add(new ProductPrices(
                    future.getMaturityDate(),
                    future.getExercisePrice(),
                    future.getSettlementPrice()));
            }
            System.out.print(futuresPrices.size());
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    class ProductPrices implements Serializable {

        private static final long serialVersionUID = 1L;

        private LocalDate maturityDate;
        private Double exercisePrice;
        private Double settlementPrice;

    }
}
