package com.easymargining.repository;

import com.easymargining.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data MongoDB repository for the Product entity.
 */
@SuppressWarnings("unused")
public interface ProductRepository extends MongoRepository<Product,String> {

    /*@Query(value="{ 'instrumentType' : ?0 }", fields="{ 'productId' : 1, 'productName' : 1}")
    List<Product> findByInstrumentTypeAndProductIdRegex(String instrumentType, String productId);*/

    List<Product> findByProductDefinitionIdAndEffectiveDate(String productDefinitionId, LocalDate effectiveDate);

    List<Product> findDistinctProductByEffectiveDate(LocalDate effectiveDate);

    List<Product> findByEffectiveDate(LocalDate effectiveDate);

}
