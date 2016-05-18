package com.easymargining.repository;

import com.easymargining.domain.Position;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data MongoDB repository for the Position entity.
 */
public interface PositionRepository extends MongoRepository<Position,String> {

    List<Position> findByPortfolioId(String portfolio_id);

    List<Position> findByPortfolioIdAndEffectiveDateLessThanEqualOrderByEffectiveDate(String portfolio_id, LocalDate effective_date);

}
