package com.easymargining.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.easymargining.domain.EurexMarketDataEnvironment;
import com.easymargining.domain.EurexProductEnvironment;
import com.easymargining.domain.Product;
import com.easymargining.domain.ProductInformation;
import com.easymargining.service.ProductService;
import com.easymargining.web.rest.util.HeaderUtil;
import com.easymargining.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for managing Product.
 */
@RestController
@RequestMapping("/api")
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    @Inject
    private ProductService productService;

    /**
     * POST  /products : Create a new product.
     *
     * @param product the product to create
     * @return the ResponseEntity with status 201 (Created) and with body the new product, or with status 400 (Bad Request) if the product has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/products",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) throws URISyntaxException {
        log.debug("REST request to save Product : {}", product);
        if (product.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("product", "idexists", "A new product cannot already have an ID")).body(null);
        }
        Product result = productService.save(product);
        return ResponseEntity.created(new URI("/api/products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("product", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /products : Updates an existing product.
     *
     * @param product the product to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated product,
     * or with status 400 (Bad Request) if the product is not valid,
     * or with status 500 (Internal Server Error) if the product couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/products",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product product) throws URISyntaxException {
        log.debug("REST request to update Product : {}", product);
        if (product.getId() == null) {
            return createProduct(product);
        }
        Product result = productService.save(product);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("product", product.getId().toString()))
            .body(result);
    }

    /**
     * GET  /products : get all the products.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of products in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/products",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Product>> getAllProducts(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Products");
        Page<Product> page = productService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/products");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /products/:id : get the "id" product.
     *
     * @param id the id of the product to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the product, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/products/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        log.debug("REST request to get Product : {}", id);
        Product product = productService.findOne(id);
        return Optional.ofNullable(product)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /products/:id : delete the "id" product.
     *
     * @param id the id of the product to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/products/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log.debug("REST request to delete Product : {}", id);
        productService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("product", id.toString())).build();
    }

    /**
     * GET  /products/byInstrumentType/:instrumentType : get products of type instrumentType (Future or Option).
     *
     * @param instrumentType the instrument type of the products to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the products, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/products/instrument-type/{instrumentType}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Set<String>> getProductsByInstrumentType(@PathVariable String instrumentType) {
        log.debug("REST request to get Products by Instrument Type : {}", instrumentType);
        Set<String> products = productService.getProductIdentifiersByInstrumentType(instrumentType);
        return Optional.ofNullable(products)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST  /products/loadProduct : post the EurexSettlementPriceDefinitions of the products
     *
     * @param
     * @return the ResponseEntity with status 200 (OK)
     */

    @RequestMapping(value = "/products/load-products",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> loadProducts() {
        log.debug("REST request to loadProducts : {}");

        // Initialize Product Definition Referential
        try {
            URL settlementPricesFile = EurexMarketDataEnvironment.getInstance().getSettlementPricesConfiguration();
            LocalDate valuationDate = EurexMarketDataEnvironment.getInstance().getValuationDate();
            productService.loadProducts(settlementPricesFile, valuationDate);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * POST  /products/loadProductDefs : post the definition of the Eurex Product
     *
     * @param
     * @return the ResponseEntity with status 200 (OK)
     */

    @RequestMapping(value = "/products/load-products-definitions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> loadProductsDefinitions() {
        log.debug("REST request to loadProductsDefinitions : {}");

        // Initialize Product Definition Referential
        List<URL> list = new ArrayList<URL>();
        list.add(EurexProductEnvironment.getInstance().getEurexProductDefinition());
        LocalDate valuationDate = EurexProductEnvironment.getInstance().getValuationDate();
        productService.loadEurexProductDefinition(list, valuationDate);

        return ResponseEntity.ok().build();
    }

    /**
     * GET  /products/productInformation/:productIdentifier : get products information (product description + list of maturities
     * and strikes associated)
     *
     * @param productIdentifier the productId or productName of the products to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the products, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/products/product-information/{productIdentifier}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProductInformation> getProductInformation(@PathVariable String productIdentifier) {
        log.debug("REST request to get ProductsInformation for productId : {}", productIdentifier);
        ProductInformation productInformation = productService.getProductInformation(productIdentifier);
        return Optional.ofNullable(productInformation)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
