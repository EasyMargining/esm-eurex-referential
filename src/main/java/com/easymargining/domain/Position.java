package com.easymargining.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Position.
 */

@Document(collection = "position")
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("product_id")
    private String productId;

    @Field("expiry_date")
    private LocalDate expiryDate;

    @Field("version_number")
    private String versionNumber;

    @Field("product_settlement_type")
    private String productSettlementType;

    @Field("option_type")
    private String optionType;

    @Field("exercise_price")
    private Double exercisePrice;

    @Field("exercise_style_flag")
    private String exerciseStyleFlag;

    @Field("instrument_type")
    private String instrumentType;

    @NotNull
    @Field("quantity")
    private Double quantity;

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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        if(position.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, position.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Position{" +
            "id=" + id +
            ", productId='" + productId + "'" +
            ", expiryDate='" + expiryDate + "'" +
            ", versionNumber='" + versionNumber + "'" +
            ", productSettlementType='" + productSettlementType + "'" +
            ", optionType='" + optionType + "'" +
            ", exercisePrice='" + exercisePrice + "'" +
            ", exerciseStyleFlag='" + exerciseStyleFlag + "'" +
            ", instrumentType='" + instrumentType + "'" +
            ", quantity='" + quantity + "'" +
            '}';
    }
}
