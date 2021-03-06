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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Set<String> getProductIdentifiersByInstrumentTypeAndEffectiveDate(String instrumentType, LocalDate effectiveDate) {
        Set<String> productIdentifiersList = new HashSet<>();
        List<EurexProductDefinition> productDefinitionsList = productDefRepository.findByTypeAndEffectiveDate(instrumentType, effectiveDate);
        productDefinitionsList.forEach((productDefinition) -> {
            productIdentifiersList.add(productDefinition.getProductName());
            productIdentifiersList.add(productDefinition.getProductDefinitionId());
        });
        log.debug("ProductService.getProductIdentifiersByInstrumentTypeAndEffectiveDate returned {} products identifier", productDefinitionsList.size());
        return productIdentifiersList;
    }

    public Product findOne(String _id) {
        Product product = productRepository.findOne(_id);
        EurexProductDefinition productDefinition = productDefRepository.findByProductDefinitionIdAndEffectiveDate(
            product.getProductDefinitionId(), product.getEffectiveDate());
        product.setInstrumentType(productDefinition.getType());
        product.setProductName(productDefinition.getProductName());
        product.setBloombergId(productDefinition.getBbgCode());
        product.setCurrency(productDefinition.getCurrency());
        product.setIsin(productDefinition.getIsinCode());
        product.setTickSize(Double.parseDouble(productDefinition.getTickSize()));
        product.setTickValue(Double.parseDouble(productDefinition.getTickValue()));
        product.setTradeUnit(productDefinition.getTradUnit());
        product.setProductSettlementType(productDefinition.getSettlementType());
        product.setMinBlockSize(productDefinition.getMinBlockSize());
        return product;
    }

    public static String formatId(EurexSettlementPriceDefinition eurexSettlementPriceDefinition, LocalDate effectiveDate) {
        String _id;
        if (eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getExerciseFlag().isPresent()) {
            _id =  eurexSettlementPriceDefinition.getProduct().getProductId() + "_"
                + eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getCallPutFlag().get().name() + "_"
                + eurexSettlementPriceDefinition.getProduct().getExpirationDate().toString(org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "_"
                + eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getExercisePrice() + "_"
                + effectiveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        } else {
            _id =  eurexSettlementPriceDefinition.getProduct().getProductId() + "_"
                + eurexSettlementPriceDefinition.getProduct().getExpirationDate().toString(org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "_"
                + effectiveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        return _id;
    }

    public static Product convertFromEurex(EurexSettlementPriceDefinition eurexSettlementPriceDefinition, LocalDate effectiveDate) {

        LocalDate maturityDate = LocalDate.of( eurexSettlementPriceDefinition.getProduct().getExpirationDate().getYear(),
            eurexSettlementPriceDefinition.getProduct().getExpirationDate().getMonthValue(),
            eurexSettlementPriceDefinition.getProduct().getExpirationDate().getDayOfMonth());

        Product product = new Product(
            formatId(eurexSettlementPriceDefinition, effectiveDate),
            eurexSettlementPriceDefinition.getProduct().getProductId(),
            maturityDate,
            eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getCallPutFlag().isPresent() ? eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getCallPutFlag().get().name() : "",
            effectiveDate,
            eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getExercisePrice(),
            eurexSettlementPriceDefinition.getSettlementPrice(),
            eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getVersionNumber(),
            eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getExerciseFlag().isPresent() ? eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getExerciseFlag().get().name() : "" ,
            eurexSettlementPriceDefinition.getProduct().getMarginStyle().toString()
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
                //Add the effectiveDate in order to be able to get the product definition on a daily basis
                productDefinitions.forEach((productDefinition) -> {
                    productDefinition.setEffectiveDate(effectiveDate);
                });
                productDefRepository.save(productDefinitions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public EurexProductDefinition getProductDefinitionByProductIdentifierAndEffectiveDate(String productIdentifier, LocalDate effectiveDate) {
        log.debug("ProductService::getProductDefinitionByProductIdentifier({}, {})", productIdentifier, effectiveDate);
        EurexProductDefinition eurexProductDefinition = productDefRepository.findByEffectiveDateAndProductDefinitionIdLikeOrProductNameLike(effectiveDate, productIdentifier, productIdentifier);
        log.debug("ProductService::getProductDefinitionByProductIdentifier : eurexProductDefinition" + eurexProductDefinition);
        return eurexProductDefinition;
    }

    public List<Product> getProductsByProductDefinitionIdAndEffectiveDate(String productDefinitionId, LocalDate effectiveDate) {
        log.debug("ProductService::getProductsByProductIdentifier(" + productDefinitionId + ", " + effectiveDate + ")");
        return productRepository.findByProductDefinitionIdAndEffectiveDate(productDefinitionId, effectiveDate);
    }

    public ProductInformation getProductInformation(String productIdentifier, LocalDate effectiveDate) {
        EurexProductDefinition productDefinition = getProductDefinitionByProductIdentifierAndEffectiveDate(productIdentifier, effectiveDate);
        List<Product> products = getProductsByProductDefinitionIdAndEffectiveDate(productDefinition.getProductDefinitionId(), effectiveDate);
        return new ProductInformation(products, productDefinition);
    }
}
