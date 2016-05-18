package com.easymargining.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import com.easymargining.domain.enumeration.Exchange;

/**
 * A Position.
 */
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "position")
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("product_id")
    private String productId;

    @NotNull
    @Field("portfolio_id")
    private String portfolioId;

    @NotNull
    @Field("effective_date")
    private LocalDate effectiveDate;

    @NotNull
    @Field("quantity")
    private Double quantity;

    @NotNull
    @Field("exchange")
    private Exchange exchange;

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

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
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
            ", portfolioId='" + portfolioId + "'" +
            ", effectiveDate='" + effectiveDate + "'" +
            ", quantity='" + quantity + "'" +
            ", exchange='" + exchange + "'" +
            '}';
    }
}
