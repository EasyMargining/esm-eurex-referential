package com.easymargining.domain;

import com.univocity.parsers.annotations.Parsed;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * Created by Gilles Marchal on 20/02/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "productDefinition")
public class EurexProductDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Parsed(field = "Eurex Code")
    private String productDefinitionId;

    @Field("instrument_type")
    @Parsed(field = "Type")
    private String type;

    @Field("product_name")
    @Parsed(field = "Product Name")
    private String productName;

    @Field("bloomberg_url")
    @Parsed(field = "Code & Link")
    private String bbgCode;

    @Field("currency")
    @Parsed(field = "CUR")
    private String currency;

    @Field("isin")
    @Parsed(field = "Product ISIN")
    private String isinCode;

    @Field("tick_size")
    @Parsed(field = "Tick Size")
    private String tickSize;

    @Field("trad_unit")
    @Parsed(field = "Trad Unit")
    private String tradUnit;

    @Field("tick_value")
    @Parsed(field = "Tick Value")
    private String tickValue;

    @Field("min_block_size")
    @Parsed(field = "Min Block Size")
    private String minBlockSize;

    @Field("product_settlement_type")
    @Parsed(field = "Settlement Type")
    private String settlementType;

}
