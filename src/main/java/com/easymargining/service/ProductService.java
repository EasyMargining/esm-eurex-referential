package com.easymargining.service;

import com.easymargining.domain.EurexProductDefinition;
import com.easymargining.domain.Product;
import com.easymargining.domain.ProductInformation;
import com.easymargining.repository.ProductDefinitionRepository;
import com.easymargining.repository.ProductRepository;
import com.easymargining.tools.eurex.EurexProductDefinitionParser;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexSettlementPriceDefinition;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexSettlementPricesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.DoubleAccumulator;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDefinitionRepository productDefRepository;

    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> storeProducts(List<Product> products) {
        return productRepository.save(products);
    }

    public void delete(String id) {
        productRepository.delete(id);
    }

    public void deleteAll() {
        productRepository.deleteAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Set<String> getProductIdentifiersByInstrumentType(String instrumentType) {
        Set<String> productIdentifiersList = new HashSet<>();
        List<EurexProductDefinition> productDefinitionsList = productDefRepository.findByType(instrumentType);
        productDefinitionsList.forEach((productDefinition) -> {
            productIdentifiersList.add(productDefinition.getProductName());
            productIdentifiersList.add(productDefinition.getEurexCode());
        });
        return productIdentifiersList;
    }

    public Product findOne(String _id) {
        Product product = productRepository.findOne(_id);
        EurexProductDefinition productDefinition = productDefRepository.findOne(product.getProductDefinitionId());
        product.setInstrumentType(productDefinition.getType());
        product.setProductName(productDefinition.getProductName());
        product.setBloombergId(productDefinition.getBbgCode());
        product.setCurrency(productDefinition.getCurrency());
        product.setIsin(productDefinition.getIsinCode());
        product.setTickSize(Double.parseDouble(productDefinition.getTickSize()));
        product.setTickValue(Double.parseDouble(productDefinition.getTickValue()));
        product.setTradeUnit(productDefinition.getTradUnit());
        product.setProductSettlementType(productDefinition.getSettlementType());
        return product;
    }

   public static Product convertFromEurex(EurexSettlementPriceDefinition eurexProductDefinition, LocalDate effectiveDate) {

       LocalDate maturityDate = LocalDate.of( eurexProductDefinition.getProduct().getExpirationDate().getYear(),
           eurexProductDefinition.getProduct().getExpirationDate().getMonthValue(),
           eurexProductDefinition.getProduct().getExpirationDate().getDayOfMonth());

        Product product = new Product(
            eurexProductDefinition.getProduct().getProductId(),
            maturityDate,
            eurexProductDefinition.getProduct().getSeriesDefinition().getCallPutFlag().isPresent() ? eurexProductDefinition.getProduct().getSeriesDefinition().getCallPutFlag().get().name() : "",
            effectiveDate,
            eurexProductDefinition.getProduct().getSeriesDefinition().getExercisePrice(),
            eurexProductDefinition.getSettlementPrice()
            );
        return product;
    }

    public static List<Product> convertFromEurex(List<EurexSettlementPriceDefinition> eurexProductDefinitions, LocalDate effectiveDate) {
        List<Product> listProducts = new ArrayList<>();
        for (EurexSettlementPriceDefinition eurexProductDefinition : eurexProductDefinitions) {
            listProducts.add(convertFromEurex(eurexProductDefinition, effectiveDate));
        }
        return listProducts;
    }

    public void loadProducts(URL file, LocalDate effectiveDate) throws IOException {
        log.info("ProductService::loadProducts( " + file.toString() + ", " + effectiveDate + " )");
        List<EurexSettlementPriceDefinition> eurexProductDefinitions = null;
        try {
            EurexSettlementPricesParser parser = new EurexSettlementPricesParser();
            eurexProductDefinitions = parser.parse(file);
            List<Product> productList = convertFromEurex(eurexProductDefinitions, effectiveDate);
            log.info("ProductService::loadProducts : all product have been succesfully parsed");
            storeProducts(productList);
            log.info("ProductService::loadProducts : all product have been successfully stored in MongoDB");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadEurexProductDefinition(List<URL> files, LocalDate effectiveDate) {
        log.info("ProductReferentialService::loadEurexProductDefinition( " + files.toString() + ", " + effectiveDate + " )");
        files.forEach( (file) -> {

            List<EurexProductDefinition> productDefinitions = null;
            try {
                productDefinitions = EurexProductDefinitionParser.parse(file);
                productDefRepository.save(productDefinitions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public List<Product> findDistinctProductByEffectiveDate(LocalDate effectiveDate) {
        return productRepository.findDistinctProductByEffectiveDate(effectiveDate);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> findByEffectiveDate(LocalDate effectiveDate) {
        return productRepository.findByEffectiveDate(effectiveDate);
    }

    public EurexProductDefinition getProductDefinitionByProductIdentifier(String productIdentifier) {
        log.debug("ProductService::getProductDefinitionByProductIdentifier(" + productIdentifier + ")");
        EurexProductDefinition eurexProductDefinition = productDefRepository.findByEurexCodeLikeOrProductNameLike(productIdentifier, productIdentifier);
        log.debug("ProductService::getProductDefinitionByProductIdentifier : eurexProductDefinition" + eurexProductDefinition);
        return eurexProductDefinition;
    }

    public List<Product> getProductsByProductDefinitionIdAndEffectiveDate(String productDefinitionId, LocalDate effectiveDate) {
        log.debug("ProductService::getProductsByProductIdentifier(" + productDefinitionId + ", " + effectiveDate + ")");
        return productRepository.findByProductDefinitionIdAndEffectiveDate(productDefinitionId, effectiveDate);
    }

    public ProductInformation getProductInformation(String productIdentifier, LocalDate effectiveDate) {
        EurexProductDefinition productDefinition = getProductDefinitionByProductIdentifier(productIdentifier);
        List<Product> products = getProductsByProductDefinitionIdAndEffectiveDate(productDefinition.getEurexCode(), effectiveDate);
        return new ProductInformation(products, productDefinition);
    }

}
