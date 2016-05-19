package com.easymargining.web.rest;

import com.easymargining.EsmeurexreferentialApp;
import com.easymargining.domain.Product;
import com.easymargining.repository.ProductRepository;
import com.easymargining.service.ProductService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ProductResource REST controller.
 *
 * @see ProductResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EsmeurexreferentialApp.class)
@WebAppConfiguration
@IntegrationTest
public class ProductResourceIntTest {

    private static final String DEFAULT_PRODUCT_ID = "AAAAA";
    private static final String UPDATED_PRODUCT_ID = "BBBBB";

    private static final LocalDate DEFAULT_EFFECTIVE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EFFECTIVE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_MATURITY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_MATURITY_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_VERSION_NUMBER = 1;
    private static final Integer UPDATED_VERSION_NUMBER = 2;
    private static final String DEFAULT_PRODUCT_SETTLEMENT_TYPE = "AAAAA";
    private static final String UPDATED_PRODUCT_SETTLEMENT_TYPE = "BBBBB";
    private static final String DEFAULT_OPTION_TYPE = "AAAAA";
    private static final String UPDATED_OPTION_TYPE = "BBBBB";

    private static final Double DEFAULT_EXERCISE_PRICE = 1D;
    private static final Double UPDATED_EXERCISE_PRICE = 2D;
    private static final String DEFAULT_CURRENCY = "AAAAA";
    private static final String UPDATED_CURRENCY = "BBBBB";
    private static final String DEFAULT_EXERCISE_STYLE_FLAG = "AAAAA";
    private static final String UPDATED_EXERCISE_STYLE_FLAG = "BBBBB";
    private static final String DEFAULT_INSTRUMENT_TYPE = "AAAAA";
    private static final String UPDATED_INSTRUMENT_TYPE = "BBBBB";

    private static final Double DEFAULT_TICK_SIZE = 1D;
    private static final Double UPDATED_TICK_SIZE = 2D;

    private static final Double DEFAULT_TICK_VALUE = 1D;
    private static final Double UPDATED_TICK_VALUE = 2D;
    private static final String DEFAULT_MARGIN_STYLE = "AAAAA";
    private static final String UPDATED_MARGIN_STYLE = "BBBBB";
    private static final String DEFAULT_LIQUIDITY_CLASS = "AAAAA";
    private static final String UPDATED_LIQUIDITY_CLASS = "BBBBB";
    private static final String DEFAULT_LIQUIDATION_GROUP = "AAAAA";
    private static final String UPDATED_LIQUIDATION_GROUP = "BBBBB";

    private static final Double DEFAULT_SETTLEMENT_PRICE = 1D;
    private static final Double UPDATED_SETTLEMENT_PRICE = 2D;
    private static final String DEFAULT_PRODUCT_NAME = "AAAAA";
    private static final String UPDATED_PRODUCT_NAME = "BBBBB";
    private static final String DEFAULT_BLOOMBERG_ID = "AAAAA";
    private static final String UPDATED_BLOOMBERG_ID = "BBBBB";
    private static final String DEFAULT_BLOOMBERG_URL = "AAAAA";
    private static final String UPDATED_BLOOMBERG_URL = "BBBBB";
    private static final String DEFAULT_ISIN = "AAAAA";
    private static final String UPDATED_ISIN = "BBBBB";

    @Inject
    private ProductRepository productRepository;

    @Inject
    private ProductService productService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restProductMockMvc;

    private Product product;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProductResource productResource = new ProductResource();
        ReflectionTestUtils.setField(productResource, "productService", productService);
        this.restProductMockMvc = MockMvcBuilders.standaloneSetup(productResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        productRepository.deleteAll();
        product = new Product();
        product.setProductId(DEFAULT_PRODUCT_ID);
        product.setEffectiveDate(DEFAULT_EFFECTIVE_DATE);
        product.setMaturityDate(DEFAULT_MATURITY_DATE);
        product.setVersionNumber(DEFAULT_VERSION_NUMBER);
        product.setProductSettlementType(DEFAULT_PRODUCT_SETTLEMENT_TYPE);
        product.setOptionType(DEFAULT_OPTION_TYPE);
        product.setExercisePrice(DEFAULT_EXERCISE_PRICE);
        product.setCurrency(DEFAULT_CURRENCY);
        product.setExerciseStyleFlag(DEFAULT_EXERCISE_STYLE_FLAG);
        product.setInstrumentType(DEFAULT_INSTRUMENT_TYPE);
        product.setTickSize(DEFAULT_TICK_SIZE);
        product.setTickValue(DEFAULT_TICK_VALUE);
        product.setMarginStyle(DEFAULT_MARGIN_STYLE);
        product.setLiquidityClass(DEFAULT_LIQUIDITY_CLASS);
        product.setLiquidationGroup(DEFAULT_LIQUIDATION_GROUP);
        product.setSettlementPrice(DEFAULT_SETTLEMENT_PRICE);
        product.setProductName(DEFAULT_PRODUCT_NAME);
        product.setBloombergId(DEFAULT_BLOOMBERG_ID);
        product.setBloombergUrl(DEFAULT_BLOOMBERG_URL);
        product.setIsin(DEFAULT_ISIN);
    }

    @Test
    public void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product

        restProductMockMvc.perform(post("/api/products")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product)))
                .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = products.get(products.size() - 1);
        assertThat(testProduct.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testProduct.getEffectiveDate()).isEqualTo(DEFAULT_EFFECTIVE_DATE);
        assertThat(testProduct.getMaturityDate()).isEqualTo(DEFAULT_MATURITY_DATE);
        assertThat(testProduct.getVersionNumber()).isEqualTo(DEFAULT_VERSION_NUMBER);
        assertThat(testProduct.getProductSettlementType()).isEqualTo(DEFAULT_PRODUCT_SETTLEMENT_TYPE);
        assertThat(testProduct.getOptionType()).isEqualTo(DEFAULT_OPTION_TYPE);
        assertThat(testProduct.getExercisePrice()).isEqualTo(DEFAULT_EXERCISE_PRICE);
        assertThat(testProduct.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testProduct.getExerciseStyleFlag()).isEqualTo(DEFAULT_EXERCISE_STYLE_FLAG);
        assertThat(testProduct.getInstrumentType()).isEqualTo(DEFAULT_INSTRUMENT_TYPE);
        assertThat(testProduct.getTickSize()).isEqualTo(DEFAULT_TICK_SIZE);
        assertThat(testProduct.getTickValue()).isEqualTo(DEFAULT_TICK_VALUE);
        assertThat(testProduct.getMarginStyle()).isEqualTo(DEFAULT_MARGIN_STYLE);
        assertThat(testProduct.getLiquidityClass()).isEqualTo(DEFAULT_LIQUIDITY_CLASS);
        assertThat(testProduct.getLiquidationGroup()).isEqualTo(DEFAULT_LIQUIDATION_GROUP);
        assertThat(testProduct.getSettlementPrice()).isEqualTo(DEFAULT_SETTLEMENT_PRICE);
        assertThat(testProduct.getProductName()).isEqualTo(DEFAULT_PRODUCT_NAME);
        assertThat(testProduct.getBloombergId()).isEqualTo(DEFAULT_BLOOMBERG_ID);
        assertThat(testProduct.getBloombergUrl()).isEqualTo(DEFAULT_BLOOMBERG_URL);
        assertThat(testProduct.getIsin()).isEqualTo(DEFAULT_ISIN);
    }

    @Test
    public void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setProductId(null);

        // Create the Product, which fails.

        restProductMockMvc.perform(post("/api/products")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product)))
                .andExpect(status().isBadRequest());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkEffectiveDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setEffectiveDate(null);

        // Create the Product, which fails.

        restProductMockMvc.perform(post("/api/products")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product)))
                .andExpect(status().isBadRequest());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkMaturityDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setMaturityDate(null);

        // Create the Product, which fails.

        restProductMockMvc.perform(post("/api/products")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product)))
                .andExpect(status().isBadRequest());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.save(product);

        // Get all the products
        restProductMockMvc.perform(get("/api/products?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId())))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.toString())))
                .andExpect(jsonPath("$.[*].effectiveDate").value(hasItem(DEFAULT_EFFECTIVE_DATE.toString())))
                .andExpect(jsonPath("$.[*].MaturityDate").value(hasItem(DEFAULT_MATURITY_DATE.toString())))
                .andExpect(jsonPath("$.[*].versionNumber").value(hasItem(DEFAULT_VERSION_NUMBER)))
                .andExpect(jsonPath("$.[*].productSettlementType").value(hasItem(DEFAULT_PRODUCT_SETTLEMENT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].optionType").value(hasItem(DEFAULT_OPTION_TYPE.toString())))
                .andExpect(jsonPath("$.[*].exercisePrice").value(hasItem(DEFAULT_EXERCISE_PRICE.doubleValue())))
                .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
                .andExpect(jsonPath("$.[*].exerciseStyleFlag").value(hasItem(DEFAULT_EXERCISE_STYLE_FLAG.toString())))
                .andExpect(jsonPath("$.[*].instrumentType").value(hasItem(DEFAULT_INSTRUMENT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].tickSize").value(hasItem(DEFAULT_TICK_SIZE.doubleValue())))
                .andExpect(jsonPath("$.[*].tickValue").value(hasItem(DEFAULT_TICK_VALUE.doubleValue())))
                .andExpect(jsonPath("$.[*].marginStyle").value(hasItem(DEFAULT_MARGIN_STYLE.toString())))
                .andExpect(jsonPath("$.[*].liquidityClass").value(hasItem(DEFAULT_LIQUIDITY_CLASS.toString())))
                .andExpect(jsonPath("$.[*].liquidationGroup").value(hasItem(DEFAULT_LIQUIDATION_GROUP.toString())))
                .andExpect(jsonPath("$.[*].settlementPrice").value(hasItem(DEFAULT_SETTLEMENT_PRICE.doubleValue())))
                .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME.toString())))
                .andExpect(jsonPath("$.[*].bloombergId").value(hasItem(DEFAULT_BLOOMBERG_ID.toString())))
                .andExpect(jsonPath("$.[*].bloombergUrl").value(hasItem(DEFAULT_BLOOMBERG_URL.toString())))
                .andExpect(jsonPath("$.[*].isin").value(hasItem(DEFAULT_ISIN.toString())));
    }

    @Test
    public void getProduct() throws Exception {
        // Initialize the database
        productRepository.save(product);

        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(product.getId()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.toString()))
            .andExpect(jsonPath("$.effectiveDate").value(DEFAULT_EFFECTIVE_DATE.toString()))
            .andExpect(jsonPath("$.MaturityDate").value(DEFAULT_MATURITY_DATE.toString()))
            .andExpect(jsonPath("$.versionNumber").value(DEFAULT_VERSION_NUMBER))
            .andExpect(jsonPath("$.productSettlementType").value(DEFAULT_PRODUCT_SETTLEMENT_TYPE.toString()))
            .andExpect(jsonPath("$.optionType").value(DEFAULT_OPTION_TYPE.toString()))
            .andExpect(jsonPath("$.exercisePrice").value(DEFAULT_EXERCISE_PRICE.doubleValue()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY.toString()))
            .andExpect(jsonPath("$.exerciseStyleFlag").value(DEFAULT_EXERCISE_STYLE_FLAG.toString()))
            .andExpect(jsonPath("$.instrumentType").value(DEFAULT_INSTRUMENT_TYPE.toString()))
            .andExpect(jsonPath("$.tickSize").value(DEFAULT_TICK_SIZE.doubleValue()))
            .andExpect(jsonPath("$.tickValue").value(DEFAULT_TICK_VALUE.doubleValue()))
            .andExpect(jsonPath("$.marginStyle").value(DEFAULT_MARGIN_STYLE.toString()))
            .andExpect(jsonPath("$.liquidityClass").value(DEFAULT_LIQUIDITY_CLASS.toString()))
            .andExpect(jsonPath("$.liquidationGroup").value(DEFAULT_LIQUIDATION_GROUP.toString()))
            .andExpect(jsonPath("$.settlementPrice").value(DEFAULT_SETTLEMENT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.productName").value(DEFAULT_PRODUCT_NAME.toString()))
            .andExpect(jsonPath("$.bloombergId").value(DEFAULT_BLOOMBERG_ID.toString()))
            .andExpect(jsonPath("$.bloombergUrl").value(DEFAULT_BLOOMBERG_URL.toString()))
            .andExpect(jsonPath("$.isin").value(DEFAULT_ISIN.toString()));
    }

    @Test
    public void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateProduct() throws Exception {
        // Initialize the database
        productService.save(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = new Product();
        updatedProduct.setId(product.getId());
        updatedProduct.setProductId(UPDATED_PRODUCT_ID);
        updatedProduct.setEffectiveDate(UPDATED_EFFECTIVE_DATE);
        updatedProduct.setMaturityDate(UPDATED_MATURITY_DATE);
        updatedProduct.setVersionNumber(UPDATED_VERSION_NUMBER);
        updatedProduct.setProductSettlementType(UPDATED_PRODUCT_SETTLEMENT_TYPE);
        updatedProduct.setOptionType(UPDATED_OPTION_TYPE);
        updatedProduct.setExercisePrice(UPDATED_EXERCISE_PRICE);
        updatedProduct.setCurrency(UPDATED_CURRENCY);
        updatedProduct.setExerciseStyleFlag(UPDATED_EXERCISE_STYLE_FLAG);
        updatedProduct.setInstrumentType(UPDATED_INSTRUMENT_TYPE);
        updatedProduct.setTickSize(UPDATED_TICK_SIZE);
        updatedProduct.setTickValue(UPDATED_TICK_VALUE);
        updatedProduct.setMarginStyle(UPDATED_MARGIN_STYLE);
        updatedProduct.setLiquidityClass(UPDATED_LIQUIDITY_CLASS);
        updatedProduct.setLiquidationGroup(UPDATED_LIQUIDATION_GROUP);
        updatedProduct.setSettlementPrice(UPDATED_SETTLEMENT_PRICE);
        updatedProduct.setProductName(UPDATED_PRODUCT_NAME);
        updatedProduct.setBloombergId(UPDATED_BLOOMBERG_ID);
        updatedProduct.setBloombergUrl(UPDATED_BLOOMBERG_URL);
        updatedProduct.setIsin(UPDATED_ISIN);

        restProductMockMvc.perform(put("/api/products")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedProduct)))
                .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = products.get(products.size() - 1);
        assertThat(testProduct.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testProduct.getEffectiveDate()).isEqualTo(UPDATED_EFFECTIVE_DATE);
        assertThat(testProduct.getMaturityDate()).isEqualTo(UPDATED_MATURITY_DATE);
        assertThat(testProduct.getVersionNumber()).isEqualTo(UPDATED_VERSION_NUMBER);
        assertThat(testProduct.getProductSettlementType()).isEqualTo(UPDATED_PRODUCT_SETTLEMENT_TYPE);
        assertThat(testProduct.getOptionType()).isEqualTo(UPDATED_OPTION_TYPE);
        assertThat(testProduct.getExercisePrice()).isEqualTo(UPDATED_EXERCISE_PRICE);
        assertThat(testProduct.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testProduct.getExerciseStyleFlag()).isEqualTo(UPDATED_EXERCISE_STYLE_FLAG);
        assertThat(testProduct.getInstrumentType()).isEqualTo(UPDATED_INSTRUMENT_TYPE);
        assertThat(testProduct.getTickSize()).isEqualTo(UPDATED_TICK_SIZE);
        assertThat(testProduct.getTickValue()).isEqualTo(UPDATED_TICK_VALUE);
        assertThat(testProduct.getMarginStyle()).isEqualTo(UPDATED_MARGIN_STYLE);
        assertThat(testProduct.getLiquidityClass()).isEqualTo(UPDATED_LIQUIDITY_CLASS);
        assertThat(testProduct.getLiquidationGroup()).isEqualTo(UPDATED_LIQUIDATION_GROUP);
        assertThat(testProduct.getSettlementPrice()).isEqualTo(UPDATED_SETTLEMENT_PRICE);
        assertThat(testProduct.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testProduct.getBloombergId()).isEqualTo(UPDATED_BLOOMBERG_ID);
        assertThat(testProduct.getBloombergUrl()).isEqualTo(UPDATED_BLOOMBERG_URL);
        assertThat(testProduct.getIsin()).isEqualTo(UPDATED_ISIN);
    }

    @Test
    public void deleteProduct() throws Exception {
        // Initialize the database
        productService.save(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Get the product
        restProductMockMvc.perform(delete("/api/products/{id}", product.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeDelete - 1);
    }
}
