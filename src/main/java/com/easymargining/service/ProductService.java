package com.easymargining.service;

import com.easymargining.domain.ContractMaturity;
import com.easymargining.domain.ContractMaturityComparator;
import com.easymargining.domain.Product;
import com.easymargining.repository.ProductRepository;
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
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private Set<String> optionIdentifierSet = new HashSet<>();

    private Set<String> futuresIdentifierSet = new HashSet<>();

    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    public Product storeProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> storeProducts(List<Product> products) {
        for (Product product : products) {
            if (product.getInstrumentType().equals("Future")) {
                futuresIdentifierSet.add(product.getProductName());
                futuresIdentifierSet.add(product.getProductId());
            } else {
                optionIdentifierSet.add(product.getProductName());
                optionIdentifierSet.add(product.getProductId());
            }
        }
        return productRepository.save(products);
    }

    public void deleteProduct(String id) {
        productRepository.delete(id);
    }

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Set<String> getProductIdentifiersByInstrumentType(String instrumentType) {
        if (instrumentType.equals("Future")) {
            return futuresIdentifierSet;
        } else if (instrumentType.equals("Option")) {
            return optionIdentifierSet;
        }
        return null;
    }

    public Product getProduct(String _id) {
        return productRepository.findOne(_id);
    }

   public static Product convertFromEurex(EurexSettlementPriceDefinition eurexProductDefinition, LocalDate effectiveDate) {

        Product product = new Product(
            null,
            eurexProductDefinition.getProduct().getProductId(),
            effectiveDate,
            eurexProductDefinition.getProduct().getExpirationDate().getYear(),
            eurexProductDefinition.getProduct().getExpirationDate().getMonthValue(),
            eurexProductDefinition.getProduct().getExpirationDate().getDayOfMonth(),
            eurexProductDefinition.getProduct().getSeriesDefinition().getVersionNumber(),
            eurexProductDefinition.getProduct().getSeriesDefinition().getSettlementType().toString(),
            eurexProductDefinition.getProduct().getSeriesDefinition().getCallPutFlag().isPresent() ? eurexProductDefinition.getProduct().getSeriesDefinition().getCallPutFlag().get().name() : "",
            eurexProductDefinition.getProduct().getSeriesDefinition().getExercisePrice(),
            "EUR",
            eurexProductDefinition.getProduct().getSeriesDefinition().getExerciseFlag().isPresent() ? eurexProductDefinition.getProduct().getSeriesDefinition().getExerciseFlag().get().name() : "" ,
            eurexProductDefinition.getProduct().getSeriesDefinition().getCallPutFlag().isPresent() ? "Option" : "Future",
            eurexProductDefinition.getProduct().getTickSize(),
            eurexProductDefinition.getProduct().getTickValue(),
            eurexProductDefinition.getProduct().getMarginStyle().toString(),
            eurexProductDefinition.getProduct().getLiquidityClass(),
            eurexProductDefinition.getProduct().getLiquidationGroup(),
            eurexProductDefinition.getSettlementPrice(),
            "find product name",
            "find bloombergId",
            "find bloombergUrl",
            "find isin"
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
        System.out.print("ProductService::loadProducts( " + file.toString() + ", " + effectiveDate + " )");
        List<EurexSettlementPriceDefinition> eurexProductDefinitions = null;
        try {
            EurexSettlementPricesParser parser = new EurexSettlementPricesParser();
            eurexProductDefinitions = parser.parse(file);
            List<Product> productList = convertFromEurex(eurexProductDefinitions, effectiveDate);
            storeProducts(productList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Product> findDistinctProductByEffectiveDate(LocalDate effectiveDate) {
        return productRepository.findDistinctProductByEffectiveDate(effectiveDate);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByEffectiveDate(LocalDate effectiveDate) {
        return productRepository.findByEffectiveDate(effectiveDate);
    }

   /* public List<EurexSettlementPriceDefinition> findAllProductDefs() {
        return eurexProductRepository.findAll();
    }

    public List<EurexSettlementPriceDefinition> findProductWithCriteria(String productType, String like) {
        return eurexProductRepository.findByTypeAndProductNameLikeOrEurexCodeLike(productType, like, like);
    }*/

    public Set<ContractMaturity> getMaturities(String productId) {
        List<Product> products = productRepository.findMaturitiesByProductId(productId);
        Set<ContractMaturity> maturitiesSet= new ConcurrentSkipListSet<>(new ContractMaturityComparator());
        products.parallelStream().forEach(product -> {
            maturitiesSet.add(new ContractMaturity(product.getMaturityYear(), product.getMaturityMonth()));
        });
        return maturitiesSet;
    }

    // Maturity contract Ex : {2022-01}
    public Set<Double> getStrikes(String productId, ContractMaturity maturity) {
        log.info("ProductReferentialService::getStrikes( " + productId + ", " + maturity.getContractYear() + maturity.getContractMonth() + " )");
        List<Product> products = productRepository.findStrikesByProductIdAndContractMaturity(productId, maturity.getContractYear(), maturity.getContractMonth());
        log.info("ProductReferentialService::getStrikes( " + productId + ", " + maturity.getContractYear() + ", " + maturity.getContractMonth() + " ) return " +products.size() + " products.");
        Set<Double> strikes = new ConcurrentSkipListSet<>();
        products.parallelStream().forEach(product -> {
            strikes.add(product.getExercisePrice());
        });
        return strikes;
    }

    public List<Product> getProducts(String productId) {
        return productRepository.findByProductId(productId);
    }

    //Product Type = Future or Option
    public List<Product> getProductsByType(String productType) {

        List<String> parameter = new ArrayList<>();
        parameter.add("C");
        parameter.add("P");

        if (productType.equals("Option")) {
            return productRepository.findByOptionTypeIn(parameter);
        } else if (productType.equals("Future")) {
            return productRepository.findByOptionTypeNotIn(parameter);
        }

        return new ArrayList<>();

    }
}
