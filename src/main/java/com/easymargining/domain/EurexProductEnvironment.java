package com.easymargining.domain;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by rjean030116 on 20/05/2016.
 */
@Slf4j
public class EurexProductEnvironment {

    private static EurexProductEnvironment INSTANCE = null;

    private URL eurexProductDefinition = null;
    private LocalDate valuationDate = null;

    private EurexProductEnvironment() {
    }

    /** Holder */
    private static class EurexProductEnvironmentHolder
    {
        private final static EurexProductEnvironment instance =
            new EurexProductEnvironment();
    }

    public static EurexProductEnvironment getInstance()
    {
        return EurexProductEnvironmentHolder.instance;
    }

    // Initialize Eurex Product Environment
    public static void init(String productEnvironmentDirectory,  LocalDate valuationDate) {
        log.info("Initialize Eurex Product Environment for valuation date : " + valuationDate.toString());
        EurexProductEnvironment environment =
            EurexProductEnvironment.getInstance();

        try {
            String productDefinitionFile = productEnvironmentDirectory + "/PRODUCTDEFINITION" +
                valuationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
            log.debug("productDefinitionFile : " + productDefinitionFile);
            environment.setEurexProductDefinition(new File(productDefinitionFile).toURI().toURL());
            environment.setValuationDate(valuationDate);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        log.info("Eurex Product Environment for valuation date : " + valuationDate.toString() + " is initialized ");
    }

    public URL getEurexProductDefinition() {
        return eurexProductDefinition;
    }

    public void setEurexProductDefinition(URL eurexProductDefinition) {
        this.eurexProductDefinition = eurexProductDefinition;
    }

    public LocalDate getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(LocalDate valuationDate) {
        this.valuationDate = valuationDate;
    }
}
