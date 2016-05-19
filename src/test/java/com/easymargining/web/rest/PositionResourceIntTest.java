package com.easymargining.web.rest;

import com.easymargining.EsmeurexreferentialApp;
import com.easymargining.domain.Position;
import com.easymargining.domain.enumeration.Exchange;
import com.easymargining.repository.PositionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PositionResource REST controller.
 *
 * @see PositionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EsmeurexreferentialApp.class)
@WebAppConfiguration
@IntegrationTest
public class PositionResourceIntTest {

    private static final String DEFAULT_PRODUCT_ID = "AAAAA";
    private static final String UPDATED_PRODUCT_ID = "BBBBB";
    private static final String DEFAULT_PORTFOLIO_ID = "AAAAA";
    private static final String UPDATED_PORTFOLIO_ID = "BBBBB";

    private static final LocalDate DEFAULT_EFFECTIVE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EFFECTIVE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_QUANTITY = 1D;
    private static final Double UPDATED_QUANTITY = 2D;

    private static final Exchange DEFAULT_EXCHANGE = Exchange.eurex;
    private static final Exchange UPDATED_EXCHANGE = Exchange.lse;

    @Inject
    private PositionRepository positionRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPositionMockMvc;

    private Position position;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PositionResource positionResource = new PositionResource();
        ReflectionTestUtils.setField(positionResource, "positionRepository", positionRepository);
        this.restPositionMockMvc = MockMvcBuilders.standaloneSetup(positionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        positionRepository.deleteAll();
        position = new Position();
        position.setProductId(DEFAULT_PRODUCT_ID);
        position.setPortfolioId(DEFAULT_PORTFOLIO_ID);
        position.setEffectiveDate(DEFAULT_EFFECTIVE_DATE);
        position.setQuantity(DEFAULT_QUANTITY);
        position.setExchange(DEFAULT_EXCHANGE);
    }

    @Test
    public void createPosition() throws Exception {
        int databaseSizeBeforeCreate = positionRepository.findAll().size();

        // Create the Position

        restPositionMockMvc.perform(post("/api/positions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(position)))
                .andExpect(status().isCreated());

        // Validate the Position in the database
        List<Position> positions = positionRepository.findAll();
        assertThat(positions).hasSize(databaseSizeBeforeCreate + 1);
        Position testPosition = positions.get(positions.size() - 1);
        assertThat(testPosition.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testPosition.getPortfolioId()).isEqualTo(DEFAULT_PORTFOLIO_ID);
        assertThat(testPosition.getEffectiveDate()).isEqualTo(DEFAULT_EFFECTIVE_DATE);
        assertThat(testPosition.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testPosition.getExchange()).isEqualTo(DEFAULT_EXCHANGE);
    }

    @Test
    public void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().size();
        // set the field null
        position.setProductId(null);

        // Create the Position, which fails.

        restPositionMockMvc.perform(post("/api/positions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(position)))
                .andExpect(status().isBadRequest());

        List<Position> positions = positionRepository.findAll();
        assertThat(positions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkPortfolioIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().size();
        // set the field null
        position.setPortfolioId(null);

        // Create the Position, which fails.

        restPositionMockMvc.perform(post("/api/positions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(position)))
                .andExpect(status().isBadRequest());

        List<Position> positions = positionRepository.findAll();
        assertThat(positions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkEffectiveDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().size();
        // set the field null
        position.setEffectiveDate(null);

        // Create the Position, which fails.

        restPositionMockMvc.perform(post("/api/positions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(position)))
                .andExpect(status().isBadRequest());

        List<Position> positions = positionRepository.findAll();
        assertThat(positions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().size();
        // set the field null
        position.setQuantity(null);

        // Create the Position, which fails.

        restPositionMockMvc.perform(post("/api/positions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(position)))
                .andExpect(status().isBadRequest());

        List<Position> positions = positionRepository.findAll();
        assertThat(positions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkExchangeIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().size();
        // set the field null
        position.setExchange(null);

        // Create the Position, which fails.

        restPositionMockMvc.perform(post("/api/positions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(position)))
                .andExpect(status().isBadRequest());

        List<Position> positions = positionRepository.findAll();
        assertThat(positions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllPositions() throws Exception {
        // Initialize the database
        positionRepository.save(position);

        // Get all the positions
        restPositionMockMvc.perform(get("/api/positions?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId())))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.toString())))
                .andExpect(jsonPath("$.[*].portfolioId").value(hasItem(DEFAULT_PORTFOLIO_ID.toString())))
                .andExpect(jsonPath("$.[*].effectiveDate").value(hasItem(DEFAULT_EFFECTIVE_DATE.toString())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.doubleValue())))
                .andExpect(jsonPath("$.[*].exchange").value(hasItem(DEFAULT_EXCHANGE.toString())));
    }

    @Test
    public void getPosition() throws Exception {
        // Initialize the database
        positionRepository.save(position);

        // Get the position
        restPositionMockMvc.perform(get("/api/positions/{id}", position.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(position.getId()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.toString()))
            .andExpect(jsonPath("$.portfolioId").value(DEFAULT_PORTFOLIO_ID.toString()))
            .andExpect(jsonPath("$.effectiveDate").value(DEFAULT_EFFECTIVE_DATE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.doubleValue()))
            .andExpect(jsonPath("$.exchange").value(DEFAULT_EXCHANGE.toString()));
    }

    @Test
    public void getNonExistingPosition() throws Exception {
        // Get the position
        restPositionMockMvc.perform(get("/api/positions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePosition() throws Exception {
        // Initialize the database
        positionRepository.save(position);
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Update the position
        Position updatedPosition = new Position();
        updatedPosition.setId(position.getId());
        updatedPosition.setProductId(UPDATED_PRODUCT_ID);
        updatedPosition.setPortfolioId(UPDATED_PORTFOLIO_ID);
        updatedPosition.setEffectiveDate(UPDATED_EFFECTIVE_DATE);
        updatedPosition.setQuantity(UPDATED_QUANTITY);
        updatedPosition.setExchange(UPDATED_EXCHANGE);

        restPositionMockMvc.perform(put("/api/positions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPosition)))
                .andExpect(status().isOk());

        // Validate the Position in the database
        List<Position> positions = positionRepository.findAll();
        assertThat(positions).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positions.get(positions.size() - 1);
        assertThat(testPosition.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testPosition.getPortfolioId()).isEqualTo(UPDATED_PORTFOLIO_ID);
        assertThat(testPosition.getEffectiveDate()).isEqualTo(UPDATED_EFFECTIVE_DATE);
        assertThat(testPosition.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testPosition.getExchange()).isEqualTo(UPDATED_EXCHANGE);
    }

    @Test
    public void deletePosition() throws Exception {
        // Initialize the database
        positionRepository.save(position);
        int databaseSizeBeforeDelete = positionRepository.findAll().size();

        // Get the position
        restPositionMockMvc.perform(delete("/api/positions/{id}", position.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Position> positions = positionRepository.findAll();
        assertThat(positions).hasSize(databaseSizeBeforeDelete - 1);
    }
}
