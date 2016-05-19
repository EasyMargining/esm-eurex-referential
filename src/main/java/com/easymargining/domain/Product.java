package com.easymargining.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Product.
 */
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("product_id")
    private String productId;

    @NotNull
    @Field("effective_date")
    private LocalDate effectiveDate;

    @NotNull
    @Field("maturity_date")
    private LocalDate MaturityDate;

    @Field("version_number")
    private Integer versionNumber;

    @Field("product_settlement_type")
    private String productSettlementType;

    @Field("option_type")
    private String optionType;

    @Field("exercise_price")
    private Double exercisePrice;

    @Field("currency")
    private String currency;

    @Field("exercise_style_flag")
    private String exerciseStyleFlag;

    @Field("instrument_type")
    private String instrumentType;

    @Field("tick_size")
    private Double tickSize;

    @Field("tick_value")
    private Double tickValue;

    @Field("margin_style")
    private String marginStyle;

    @Field("liquidity_class")
    private String liquidityClass;

    @Field("liquidation_group")
    private String liquidationGroup;

    @Field("settlement_price")
    private Double settlementPrice;

    @Field("product_name")
    private String productName;

    @Field("bloomberg_id")
    private String bloombergId;

    @Field("bloomberg_url")
    private String bloombergUrl;

    @Field("isin")
    private String isin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getMaturityDate() {
        return MaturityDate;
    }

    public void setMaturityDate(LocalDate MaturityDate) {
        this.MaturityDate = MaturityDate;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getProductSettlementType() {
        return productSettlementType;
    }

    public void setProductSettlementType(String productSettlementType) {
        this.productSettlementType = productSettlementType;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public Double getExercisePrice() {
        return exercisePrice;
    }

    public void setExercisePrice(Double exercisePrice) {
        this.exercisePrice = exercisePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExerciseStyleFlag() {
        return exerciseStyleFlag;
    }

    public void setExerciseStyleFlag(String exerciseStyleFlag) {
        this.exerciseStyleFlag = exerciseStyleFlag;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public Double getTickSize() {
        return tickSize;
    }

    public void setTickSize(Double tickSize) {
        this.tickSize = tickSize;
    }

    public Double getTickValue() {
        return tickValue;
    }

    public void setTickValue(Double tickValue) {
        this.tickValue = tickValue;
    }

    public String getMarginStyle() {
        return marginStyle;
    }

    public void setMarginStyle(String marginStyle) {
        this.marginStyle = marginStyle;
    }

    public String getLiquidityClass() {
        return liquidityClass;
    }

    public void setLiquidityClass(String liquidityClass) {
        this.liquidityClass = liquidityClass;
    }

    public String getLiquidationGroup() {
        return liquidationGroup;
    }

    public void setLiquidationGroup(String liquidationGroup) {
        this.liquidationGroup = liquidationGroup;
    }

    public Double getSettlementPrice() {
        return settlementPrice;
    }

    public void setSettlementPrice(Double settlementPrice) {
        this.settlementPrice = settlementPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBloombergId() {
        return bloombergId;
    }

    public void setBloombergId(String bloombergId) {
        this.bloombergId = bloombergId;
    }

    public String getBloombergUrl() {
        return bloombergUrl;
    }

    public void setBloombergUrl(String bloombergUrl) {
        this.bloombergUrl = bloombergUrl;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        if(product.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Product{" +
            "id=" + id +
            ", productId='" + productId + "'" +
            ", effectiveDate='" + effectiveDate + "'" +
            ", MaturityDate='" + MaturityDate + "'" +
            ", versionNumber='" + versionNumber + "'" +
            ", productSettlementType='" + productSettlementType + "'" +
            ", optionType='" + optionType + "'" +
            ", exercisePrice='" + exercisePrice + "'" +
            ", currency='" + currency + "'" +
            ", exerciseStyleFlag='" + exerciseStyleFlag + "'" +
            ", instrumentType='" + instrumentType + "'" +
            ", tickSize='" + tickSize + "'" +
            ", tickValue='" + tickValue + "'" +
            ", marginStyle='" + marginStyle + "'" +
            ", liquidityClass='" + liquidityClass + "'" +
            ", liquidationGroup='" + liquidationGroup + "'" +
            ", settlementPrice='" + settlementPrice + "'" +
            ", productName='" + productName + "'" +
            ", bloombergId='" + bloombergId + "'" +
            ", bloombergUrl='" + bloombergUrl + "'" +
            ", isin='" + isin + "'" +
            '}';
    }
}
