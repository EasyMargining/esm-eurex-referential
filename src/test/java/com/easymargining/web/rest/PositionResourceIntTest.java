package com.easymargining.web.rest;

import com.easymargining.EsmeurexreferentialApp;
import com.easymargining.domain.Position;
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

    private static final LocalDate DEFAULT_EXPIRY_DATE = LocalDate.parse("01-01-2016");
    private static final LocalDate UPDATED_EXPIRY_DATE = LocalDate.parse("01-01-2017");
    private static final String DEFAULT_VERSION_NUMBER = "AAAAA";
    private static final String UPDATED_VERSION_NUMBER = "BBBBB";
    private static final String DEFAULT_PRODUCT_SETTLEMENT_TYPE = "AAAAA";
    private static final String UPDATED_PRODUCT_SETTLEMENT_TYPE = "BBBBB";
    private static final String DEFAULT_OPTION_TYPE = "AAAAA";
    private static final String UPDATED_OPTION_TYPE = "BBBBB";

    private static final Double DEFAULT_EXERCISE_PRICE = 1D;
    private static final Double UPDATED_EXERCISE_PRICE = 2D;
    private static final String DEFAULT_EXERCISE_STYLE_FLAG = "AAAAA";
    private static final String UPDATED_EXERCISE_STYLE_FLAG = "BBBBB";
    private static final String DEFAULT_INSTRUMENT_TYPE = "AAAAA";
    private static final String UPDATED_INSTRUMENT_TYPE = "BBBBB";

    private static final Double DEFAULT_QUANTITY = 1D;
    private static final Double UPDATED_QUANTITY = 2D;

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
        position.setExpiryDate(DEFAULT_EXPIRY_DATE);
        position.setVersionNumber(DEFAULT_VERSION_NUMBER);
        position.setProductSettlementType(DEFAULT_PRODUCT_SETTLEMENT_TYPE);
        position.setOptionType(DEFAULT_OPTION_TYPE);
        position.setExercisePrice(DEFAULT_EXERCISE_PRICE);
        position.setExerciseStyleFlag(DEFAULT_EXERCISE_STYLE_FLAG);
        position.setInstrumentType(DEFAULT_INSTRUMENT_TYPE);
        position.setQuantity(DEFAULT_QUANTITY);
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
        assertThat(testPosition.getExpiryDate()).isEqualTo(DEFAULT_EXPIRY_DATE);
        assertThat(testPosition.getVersionNumber()).isEqualTo(DEFAULT_VERSION_NUMBER);
        assertThat(testPosition.getProductSettlementType()).isEqualTo(DEFAULT_PRODUCT_SETTLEMENT_TYPE);
        assertThat(testPosition.getOptionType()).isEqualTo(DEFAULT_OPTION_TYPE);
        assertThat(testPosition.getExercisePrice()).isEqualTo(DEFAULT_EXERCISE_PRICE);
        assertThat(testPosition.getExerciseStyleFlag()).isEqualTo(DEFAULT_EXERCISE_STYLE_FLAG);
        assertThat(testPosition.getInstrumentType()).isEqualTo(DEFAULT_INSTRUMENT_TYPE);
        assertThat(testPosition.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
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
    public void getAllPositions() throws Exception {
        // Initialize the database
        positionRepository.save(position);

        // Get all the positions
        restPositionMockMvc.perform(get("/api/positions?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId())))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.toString())))
                .andExpect(jsonPath("$.[*].expiryDate").value(hasItem(DEFAULT_EXPIRY_DATE.toString())))
                .andExpect(jsonPath("$.[*].versionNumber").value(hasItem(DEFAULT_VERSION_NUMBER.toString())))
                .andExpect(jsonPath("$.[*].productSettlementType").value(hasItem(DEFAULT_PRODUCT_SETTLEMENT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].optionType").value(hasItem(DEFAULT_OPTION_TYPE.toString())))
                .andExpect(jsonPath("$.[*].exercisePrice").value(hasItem(DEFAULT_EXERCISE_PRICE.doubleValue())))
                .andExpect(jsonPath("$.[*].exerciseStyleFlag").value(hasItem(DEFAULT_EXERCISE_STYLE_FLAG.toString())))
                .andExpect(jsonPath("$.[*].instrumentType").value(hasItem(DEFAULT_INSTRUMENT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.doubleValue())));
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
            .andExpect(jsonPath("$.expiryDate").value(DEFAULT_EXPIRY_DATE.toString()))
            .andExpect(jsonPath("$.versionNumber").value(DEFAULT_VERSION_NUMBER.toString()))
            .andExpect(jsonPath("$.productSettlementType").value(DEFAULT_PRODUCT_SETTLEMENT_TYPE.toString()))
            .andExpect(jsonPath("$.optionType").value(DEFAULT_OPTION_TYPE.toString()))
            .andExpect(jsonPath("$.exercisePrice").value(DEFAULT_EXERCISE_PRICE.doubleValue()))
            .andExpect(jsonPath("$.exerciseStyleFlag").value(DEFAULT_EXERCISE_STYLE_FLAG.toString()))
            .andExpect(jsonPath("$.instrumentType").value(DEFAULT_INSTRUMENT_TYPE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.doubleValue()));
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
        updatedPosition.setExpiryDate(UPDATED_EXPIRY_DATE);
        updatedPosition.setVersionNumber(UPDATED_VERSION_NUMBER);
        updatedPosition.setProductSettlementType(UPDATED_PRODUCT_SETTLEMENT_TYPE);
        updatedPosition.setOptionType(UPDATED_OPTION_TYPE);
        updatedPosition.setExercisePrice(UPDATED_EXERCISE_PRICE);
        updatedPosition.setExerciseStyleFlag(UPDATED_EXERCISE_STYLE_FLAG);
        updatedPosition.setInstrumentType(UPDATED_INSTRUMENT_TYPE);
        updatedPosition.setQuantity(UPDATED_QUANTITY);

        restPositionMockMvc.perform(put("/api/positions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPosition)))
                .andExpect(status().isOk());

        // Validate the Position in the database
        List<Position> positions = positionRepository.findAll();
        assertThat(positions).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positions.get(positions.size() - 1);
        assertThat(testPosition.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testPosition.getExpiryDate()).isEqualTo(UPDATED_EXPIRY_DATE);
        assertThat(testPosition.getVersionNumber()).isEqualTo(UPDATED_VERSION_NUMBER);
        assertThat(testPosition.getProductSettlementType()).isEqualTo(UPDATED_PRODUCT_SETTLEMENT_TYPE);
        assertThat(testPosition.getOptionType()).isEqualTo(UPDATED_OPTION_TYPE);
        assertThat(testPosition.getExercisePrice()).isEqualTo(UPDATED_EXERCISE_PRICE);
        assertThat(testPosition.getExerciseStyleFlag()).isEqualTo(UPDATED_EXERCISE_STYLE_FLAG);
        assertThat(testPosition.getInstrumentType()).isEqualTo(UPDATED_INSTRUMENT_TYPE);
        assertThat(testPosition.getQuantity()).isEqualTo(UPDATED_QUANTITY);
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
