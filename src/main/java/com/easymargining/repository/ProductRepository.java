package com.easymargining.repository;

import com.easymargining.domain.Product;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data MongoDB repository for the Product entity.
 */
@SuppressWarnings("unused")
public interface ProductRepository extends MongoRepository<Product,String> {

    @Query(value="{ 'instrumentType' : ?0 }", fields="{ 'productId' : 1, 'productName' : 1}")
    List<Product> findByInstrumentTypeAndProductIdRegex(String instrumentType, String productId);

    List<Product> findByProductId(String productId);

    List<Product> findDistinctProductByEffectiveDate(LocalDate effectiveDate);

    List<Product> findByEffectiveDate(LocalDate effectiveDate);

    List<Product> findByProductIdAndEffectiveDate(String productId, LocalDate effectiveDate);

    List<Product> findByOptionTypeIn(List<String> optionType);

    List<Product> findByOptionTypeNotIn(List<String> optionType);

    @Query(value="{ 'productId' : ?0 }", fields="{ 'contractYear' : 1, 'contractMonth' : 1}")
    List<Product> findMaturitiesByProductId(String productId);

    @Query(value="{ 'productId' : ?0, 'contractYear' : ?1, 'contractMonth' : ?2 }", fields="{ 'exercisePrice' : 1}")
    List<Product> findStrikesByProductIdAndContractMaturity(String productId, Integer contractYear, Integer contractMonth);

}
