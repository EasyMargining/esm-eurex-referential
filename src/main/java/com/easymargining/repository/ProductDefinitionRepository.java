package com.easymargining.repository;

import com.easymargining.domain.EurexProductDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by rjean030116 on 20/05/2016.
 */
public interface ProductDefinitionRepository extends MongoRepository<EurexProductDefinition, String> {

    // ProductType : Future or Option
    List<EurexProductDefinition> findByType(String instrumentType);

    EurexProductDefinition findByEurexCodeLikeOrProductNameLike(String like1, String like2);

}
