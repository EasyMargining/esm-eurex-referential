package com.easymargining.service;

import com.easymargining.domain.Position;
import com.easymargining.domain.Product;
import com.easymargining.domain.enumeration.Exchange;
import com.easymargining.repository.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        HashMap<Position, Product> positionProductMap = new HashMap<>();
        for (Iterator<Position> positionIterator = positions.iterator(); positionIterator.hasNext();) {
            Position position = positionIterator.next();
            Product product = productService.findOne(position.getProductId());

            if (!product.getMaturityDate().isBefore(valuationDate)) {
                // We only keep positions with maturity date >= valuationDate
                positionProductMap.put(position, product);
            }
        }

        /*
            Map in which we calculate the quantity aggregation
            The key is a string concatenation of {ProductDefinitionId, MaturityDate, OptionType, Strike}
        */
        HashMap<String, AggregatedWrapper> aggregatedWrapperHashMap = new HashMap<>();
        for (Map.Entry<Position, Product> entry : positionProductMap.entrySet()) {
            Position position = entry.getKey();
            Product product = entry.getValue();

            //Replace the effectiveDate part of the productId with the valutionDate
            String key = product.getId().substring(0, product.getId().length() - 9)
                + "_" + valuationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            //Update the aggregatedWrapperHashMap with the position
            if (!aggregatedWrapperHashMap.containsKey(key)) {
                AggregatedWrapper aggregatedWrapper = new AggregatedWrapper(position);
                aggregatedWrapperHashMap.put(key, aggregatedWrapper);
            } else {
                aggregatedWrapperHashMap.get(key).addPosition(position);
            }

            AggregatedWrapper aggregatedWrapper = aggregatedWrapperHashMap.get(key);

            if (aggregatedWrapper.getAggregatedQuantity() == 0d
                && aggregatedWrapper.getLastDate().isBefore(valuationDate.minusDays(1))) {
                aggregatedWrapperHashMap.remove(key);
            }
        }

        List<Position> aggregatedPositions = new ArrayList<>();
        for (Map.Entry<String, AggregatedWrapper> entry : aggregatedWrapperHashMap.entrySet()) {
            aggregatedPositions.add(entry.getValue().getAggregatedPosition(entry.getKey()));
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

        public Position getAggregatedPosition(String key) {
            /*
             * This position is an aggregated one so :
             *      - does not have its own positionId, by default we put null
              *     - does not have productId, so we put key (a formated string understandable by the ui)
             */
            return new Position(null, key, portfolioId, lastDate, aggregatedQuantity, exchange);
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
