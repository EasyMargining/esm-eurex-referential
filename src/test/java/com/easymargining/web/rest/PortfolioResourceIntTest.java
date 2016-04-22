package com.easymargining.web.rest;

import com.easymargining.EsmeurexreferentialApp;
import com.easymargining.domain.Portfolio;
import com.easymargining.repository.PortfolioRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the PortfolioResource REST controller.
 *
 * @see PortfolioResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EsmeurexreferentialApp.class)
@WebAppConfiguration
@IntegrationTest
public class PortfolioResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_OWNER = "AAAAA";
    private static final String UPDATED_OWNER = "BBBBB";

    @Inject
    private PortfolioRepository portfolioRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPortfolioMockMvc;

    private Portfolio portfolio;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PortfolioResource portfolioResource = new PortfolioResource();
        ReflectionTestUtils.setField(portfolioResource, "portfolioRepository", portfolioRepository);
        this.restPortfolioMockMvc = MockMvcBuilders.standaloneSetup(portfolioResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        portfolioRepository.deleteAll();
        portfolio = new Portfolio();
        portfolio.setName(DEFAULT_NAME);
        portfolio.setOwner(DEFAULT_OWNER);
    }

    @Test
    public void createPortfolio() throws Exception {
        int databaseSizeBeforeCreate = portfolioRepository.findAll().size();

        // Create the Portfolio

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isCreated());

        // Validate the Portfolio in the database
        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeCreate + 1);
        Portfolio testPortfolio = portfolios.get(portfolios.size() - 1);
        assertThat(testPortfolio.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPortfolio.getOwner()).isEqualTo(DEFAULT_OWNER);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioRepository.findAll().size();
        // set the field null
        portfolio.setName(null);

        // Create the Portfolio, which fails.

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isBadRequest());

        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkOwnerIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioRepository.findAll().size();
        // set the field null
        portfolio.setOwner(null);

        // Create the Portfolio, which fails.

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isBadRequest());

        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllPortfolios() throws Exception {
        // Initialize the database
        portfolioRepository.save(portfolio);

        // Get all the portfolios
        restPortfolioMockMvc.perform(get("/api/portfolios?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(portfolio.getId())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER.toString())));
    }

    @Test
    public void getPortfolio() throws Exception {
        // Initialize the database
        portfolioRepository.save(portfolio);

        // Get the portfolio
        restPortfolioMockMvc.perform(get("/api/portfolios/{id}", portfolio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(portfolio.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER.toString()));
    }

    @Test
    public void getNonExistingPortfolio() throws Exception {
        // Get the portfolio
        restPortfolioMockMvc.perform(get("/api/portfolios/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePortfolio() throws Exception {
        // Initialize the database
        portfolioRepository.save(portfolio);
        int databaseSizeBeforeUpdate = portfolioRepository.findAll().size();

        // Update the portfolio
        Portfolio updatedPortfolio = new Portfolio();
        updatedPortfolio.setId(portfolio.getId());
        updatedPortfolio.setName(UPDATED_NAME);
        updatedPortfolio.setOwner(UPDATED_OWNER);

        restPortfolioMockMvc.perform(put("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPortfolio)))
                .andExpect(status().isOk());

        // Validate the Portfolio in the database
        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeUpdate);
        Portfolio testPortfolio = portfolios.get(portfolios.size() - 1);
        assertThat(testPortfolio.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPortfolio.getOwner()).isEqualTo(UPDATED_OWNER);
    }

    @Test
    public void deletePortfolio() throws Exception {
        // Initialize the database
        portfolioRepository.save(portfolio);
        int databaseSizeBeforeDelete = portfolioRepository.findAll().size();

        // Get the portfolio
        restPortfolioMockMvc.perform(delete("/api/portfolios/{id}", portfolio.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeDelete - 1);
    }
}
