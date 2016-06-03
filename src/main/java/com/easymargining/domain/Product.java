package com.easymargining.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Product.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Document(collection = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * Id format :
     *              - example if option : SDF_PUT_20181218_0.01_20151127
     *              - example if futures : COV_20181218_20151127
     */
    @Id
    private String id;

    @NotNull
    @Field("product_definition_id")
    private String productDefinitionId;

    @NotNull
    @Field("maturity_date")
    private LocalDate maturityDate;

    @Field("option_type")
    private String optionType;

    @NotNull
    @Field("effective_date")
    private LocalDate effectiveDate;

    @Field("exercise_price")
    private Double exercisePrice;

    @Field("settlement_price")
    private Double settlementPrice;

    @Field("version_number")
    private Integer versionNumber;

    @Field("exercise_style_flag")
    private String exerciseStyleFlag;

    @Field("margin_style")
    private String marginStyle; //This field could be stored on productDefinition mongo document

    private String productSettlementType;
    private String currency;
    private String instrumentType;
    private Double tickSize;
    private Double tickValue;
//    private String liquidityClass;
//    private String liquidationGroup;
    private String productName;
    private String bloombergId;
    private String isin;
    private String tradeUnit;
    private String minBlockSize;

    public Product(String id, String productDefinitionId, LocalDate maturityDate, String optionType,
                   LocalDate effectiveDate, Double exercisePrice, Double settlementPrice,
                   Integer versionNumber, String exerciseStyleFlag, String marginStyle) {
        this.id = id;
        this.productDefinitionId = productDefinitionId;
        this.maturityDate = maturityDate;
        this.optionType = optionType;
        this.effectiveDate = effectiveDate;
        this.exercisePrice = exercisePrice;
        this.settlementPrice = settlementPrice;
        this.versionNumber = versionNumber;
        this.exerciseStyleFlag = exerciseStyleFlag;
        this.marginStyle = marginStyle;
    }
}
