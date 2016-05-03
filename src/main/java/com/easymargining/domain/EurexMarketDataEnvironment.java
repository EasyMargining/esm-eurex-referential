package com.easymargining.domain;

import com.opengamma.margining.core.MarginEnvironment;
import com.opengamma.margining.core.MarginEnvironmentFactory;
import com.opengamma.margining.eurex.prisma.data.MarketDataFileResolver;
import com.opengamma.margining.eurex.prisma.loader.MarketDataLoaders;
import com.opengamma.margining.eurex.prisma.replication.EurexPrismaReplication;
import com.opengamma.margining.eurex.prisma.replication.data.EurexEtdMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexRiskMeasureConfigParser;
import com.opengamma.util.tuple.Triple;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by gmarchal on 22/02/2016.
 */
@Slf4j
public class EurexMarketDataEnvironment {

    private static EurexMarketDataEnvironment INSTANCE = null;

    private URL settlementPricesConfiguration = null;

    private LocalDate valuationDate = null;

    private EurexMarketDataEnvironment() {
    }

    /** Holder */
    private static class EurexMarketDataEnvironmentHolder
    {
        private final static EurexMarketDataEnvironment instance =
            new EurexMarketDataEnvironment();
    }

    public static EurexMarketDataEnvironment getInstance()
    {
        return EurexMarketDataEnvironmentHolder.instance;
    }

    // Initialize Eurex MarketData Environment
    public static void init(String marketDataDirectory, LocalDate valuationDate) {
        log.info("Initialize Eurex Market Data Environment for valuation date : " + valuationDate.toString());
        EurexMarketDataEnvironment environment =
            EurexMarketDataEnvironment.getInstance() ;

        // Convert LocalDate
        org.threeten.bp.LocalDate s_valuationDate = org.threeten.bp.LocalDate.parse(valuationDate.toString());

        // Initialize environment with data
        //MarginEnvironment marginEnvironment = MarginEnvironmentFactory.buildBasicEnvironment(new EurexPrismaReplication());

        // Use file resolver utility to discover data from standard Eurex directory structure
        String directoryFilePattern = new String("file:").concat(marketDataDirectory);
        MarketDataFileResolver fileResolver = new MarketDataFileResolver(directoryFilePattern, s_valuationDate);

        // Create ETD data load request, pointing to classpath, and load
        EurexEtdMarketDataLoadRequest etdDataLoadRequest = MarketDataLoaders.etdRequest(fileResolver);

       // EurexMarketDataLoadRequest loadRequest = EurexMarketDataLoadRequest.etdMarketDataRequest(s_valuationDate, etdDataLoadRequest);
       // marginEnvironment.getMarginData().loadData(loadRequest);


        environment.setSettlementPricesConfiguration(etdDataLoadRequest.getSettlementPrices());
        environment.setValuationDate(valuationDate);


        log.info("Eurex Market Data Environment for valuation date : " + s_valuationDate.toString() + " is initialized ");
    }

    public URL getSettlementPricesConfiguration() {
        return settlementPricesConfiguration;
    }

    public void setSettlementPricesConfiguration(URL settlementPricesConfiguration) {
        this.settlementPricesConfiguration = settlementPricesConfiguration;
    }

    public LocalDate getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(LocalDate valuationDate) {
        this.valuationDate = valuationDate;
    }

}
