package com.easymargining.service;

import com.easymargining.domain.Position;
import com.easymargining.domain.Product;
import com.easymargining.domain.enumeration.Exchange;
import com.easymargining.repository.PositionRepository;
import com.easymargining.repository.ProductRepository;
import org.apache.shiro.crypto.hash.Hash;
import org.jboss.logging.annotations.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ProductService productService;

    private final Logger log = LoggerFactory.getLogger(PositionService.class);

    public Position save(Position position) {
        return positionRepository.save(position);
    }

    public Page<Position> getAllPositions(Pageable pageable) {
        return positionRepository.findAll(pageable);
    }

    public Position findOne(String id) {
        return positionRepository.findOne(id);
    }

    public void delete(String id) {
        positionRepository.delete(id);
    }

    public List<Position> getPositionByPortfolioIdAndValuationDate(String portfolioId, LocalDate valuationDate) {
        // Find positions in the given portfolio with effective date <= valuationDate
        List<Position> positions = positionRepository.findByPortfolioIdAndEffectiveDateLessThanEqualOrderByEffectiveDate(portfolioId, valuationDate);

        for (Iterator<Position> positionIterator = positions.iterator(); positionIterator.hasNext();) {
            Position position = positionIterator.next();
            Product product = productService.findOne(position.getProductId());

            if (product.getMaturityDate().isBefore(valuationDate)) {
                // Remove positions with maturity date < valuationDate
                positionIterator.remove();
            }
        }

        HashMap<String, AggregatedWrapper> aggregatedWrapperHashMap = new HashMap<>();
        for (Position position : positions) {
            //Update the aggregatedWrapperHashMap with the position
            if (!aggregatedWrapperHashMap.containsKey(position.getProductId())) {
                AggregatedWrapper aggregatedWrapper = new AggregatedWrapper(position);
                aggregatedWrapperHashMap.put(position.getProductId(), aggregatedWrapper);
            } else {
                aggregatedWrapperHashMap.get(position.getProductId()).addPosition(position);
            }

            AggregatedWrapper aggregatedWrapper = aggregatedWrapperHashMap.get(position.getProductId());

            if (aggregatedWrapper.getAggregatedQuantity() == 0d
                && aggregatedWrapper.getLastDate().isBefore(valuationDate.minusDays(1))) {
                aggregatedWrapperHashMap.remove(position.getProductId());
            }
        }

        List<Position> aggregatedPositions = new ArrayList<>();
        for (Map.Entry<String, AggregatedWrapper> entry : aggregatedWrapperHashMap.entrySet()) {
            aggregatedPositions.add(entry.getValue().getAggregatedPosition());
        }

        return aggregatedPositions;
    }

    private class AggregatedWrapper {

        private double aggregatedQuantity;
        private LocalDate lastDate;
        private String productId;
        private String portfolioId;
        private Exchange exchange;

        public AggregatedWrapper(Position position) {
            aggregatedQuantity = position.getQuantity();
            productId = position.getProductId();
            portfolioId = position.getPortfolioId();
            exchange = position.getExchange();
            lastDate = position.getEffectiveDate();
        }

        public void addPosition(Position position) {
            aggregatedQuantity += position.getQuantity();
            lastDate = position.getEffectiveDate();
        }

        public Position getAggregatedPosition() {
            //We return the productId as positionId because this position is an aggregated one
            //So does not have its own positionId
            return new Position(productId, productId, portfolioId, lastDate, aggregatedQuantity, exchange);
        }

        public double getAggregatedQuantity() {
            return aggregatedQuantity;
        }

        public LocalDate getLastDate() {
            return lastDate;
        }

        public String getProductId() {
            return productId;
        }
    }

}
