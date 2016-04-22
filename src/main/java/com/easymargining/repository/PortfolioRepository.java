package com.easymargining.repository;

import com.easymargining.domain.Portfolio;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the Portfolio entity.
 */
public interface PortfolioRepository extends MongoRepository<Portfolio,String> {

    Portfolio findByName(String name);

    List<Portfolio> findByOwner(String owner);

    Portfolio findByOwnerAndName(String owner, String name);

}
