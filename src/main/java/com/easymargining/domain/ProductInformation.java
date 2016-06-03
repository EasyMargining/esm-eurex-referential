package com.easymargining.domain;

import lombok.*;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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

    private String productDefinitionId;
    private Double tickSize;
    private Double tickValue;
    private String currency;
    private String marginStyle;
    private String productName;
    private String bloombergId;
    private String isin;
    private Integer versionNumber;
    private LocalDate effectiveDate;
    private String instrumentType;
    private String productSettlementType;
    private String exerciseStyleFlag;
    private String minBlockSize;
    private String tradeUnit;

    private TreeMap<LocalDate, List<ProductPrices>> futuresPrices;

    private TreeMap<LocalDate, List<ProductPrices>> callPrices;
    private TreeMap<LocalDate, List<ProductPrices>> putPrices;
    public ProductInformation(List<Product> products, EurexProductDefinition productDefinition) {
        Assert.notNull(products);
        this.productDefinitionId = productDefinition.getProductDefinitionId();
        this.tickSize = Double.parseDouble(productDefinition.getTickSize());
        this.tickValue = Double.parseDouble(productDefinition.getTickValue());
        this.currency = productDefinition.getCurrency();
        this.productName = productDefinition.getProductName();
        this.bloombergId = productDefinition.getBbgCode();
        this.isin = productDefinition.getIsinCode();
        this.effectiveDate = productDefinition.getEffectiveDate();
        this.instrumentType = productDefinition.getType();
        this.productSettlementType = productDefinition.getSettlementType();
        this.tradeUnit = productDefinition.getTradUnit();
        this.minBlockSize = productDefinition.getMinBlockSize();

        this.marginStyle = products.get(0).getMarginStyle();
        this.versionNumber = products.get(0).getVersionNumber();
        this.exerciseStyleFlag = products.get(0).getExerciseStyleFlag();

        if (productDefinition.getType().equals("Option")) {
            callPrices = new TreeMap<>();
            putPrices = new TreeMap<>();
            for (Product option : products) {
                if (option.getOptionType().equals("CALL")) {
                    if (!callPrices.containsKey(option.getMaturityDate())) {
                        callPrices.put(option.getMaturityDate(), new ArrayList<>());
                    }
                    callPrices.get(option.getMaturityDate())
                        .add(new ProductPrices(
                            option.getId(),
                            option.getExercisePrice(),
                            option.getSettlementPrice()));
                } else {
                    if (!putPrices.containsKey(option.getMaturityDate())) {
                        putPrices.put(option.getMaturityDate(), new ArrayList<>());
                    }
                    putPrices.get(option.getMaturityDate())
                        .add(new ProductPrices(
                            option.getId(),
                            option.getExercisePrice(),
                            option.getSettlementPrice()));
                }
            }
        } else {
            futuresPrices = new TreeMap<>();
            for (Product future : products) {
                if (!futuresPrices.containsKey(future.getMaturityDate())) {
                    futuresPrices.put(future.getMaturityDate(), new ArrayList<>());
                }
                futuresPrices.get(future.getMaturityDate())
                    .add(new ProductPrices(
                        future.getId(),
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

        private String _id;
        private Double exercisePrice;
        private Double settlementPrice;

    }
}
