package com.easymargining.repository;

import com.easymargining.domain.Portfolio;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Portfolio entity.
 */
public interface PortfolioRepository extends MongoRepository<Portfolio,String> {

}
