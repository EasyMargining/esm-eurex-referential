package com.easymargining.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.easymargining.domain.Position;
import com.easymargining.service.PositionService;
import com.easymargining.web.rest.util.HeaderUtil;
import com.easymargining.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Position.
 */
@RestController
@RequestMapping("/api")
public class PositionResource {

    private final Logger log = LoggerFactory.getLogger(PositionResource.class);

    @Inject
    private PositionService positionService;

    /**
     * POST  /positions : Create a new position.
     *
     * @param position the position to create
     * @return the ResponseEntity with status 201 (Created) and with body the new position, or with status 400 (Bad Request) if the position has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/positions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Position> createPosition(@Valid @RequestBody Position position) throws URISyntaxException {
        log.debug("REST request to save Position : {}", position);
        if (position.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("position", "idexists", "A new position cannot already have an ID")).body(null);
        }
        Position result = positionService.save(position);
        return ResponseEntity.created(new URI("/api/positions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("position", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /positions : Updates an existing position.
     *
     * @param position the position to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated position,
     * or with status 400 (Bad Request) if the position is not valid,
     * or with status 500 (Internal Server Error) if the position couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/positions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Position> updatePosition(@Valid @RequestBody Position position) throws URISyntaxException {
        log.debug("REST request to update Position : {}", position);
        if (position.getId() == null) {
            return createPosition(position);
        }
        Position result = positionService.save(position);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("position", position.getId().toString()))
            .body(result);
    }

    /**
     * GET  /positions : get all the positions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of positions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/positions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Position>> getAllPositions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Positions");
        Page<Position> page = positionService.getAllPositions(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/positions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /positions/:id : get the "id" position.
     *
     * @param id the id of the position to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the position, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/positions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Position> getPosition(@PathVariable String id) {
        log.debug("REST request to get Position : {}", id);
        Position position = positionService.findOne(id);
        return Optional.ofNullable(position)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /positions/byPortfolio/:portfolioId/:valuationDate : get positions for a "portfolioId" at a given time
     *
     * @param portfolioId the portfolioId of the positions to retrieve
     * @param valuationDate the date at which we want to retrieve the positions
     *
     * @return the ResponseEntity with status 200 (OK) and with body the positions, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/positions/portfolio/{portfolioId}/valuation-date/{valuationDate}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Position>> getPositionByPortfolio(@PathVariable String portfolioId, @PathVariable String valuationDate) {
        log.debug("REST request to get Positions : {}", portfolioId);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate valuationLocalDate = LocalDate.parse(valuationDate, dateFormat);

        List<Position> positions = positionService.getPositionByPortfolioIdAndValuationDate(portfolioId, valuationLocalDate);
        return Optional.ofNullable(positions)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * DELETE  /positions/:id : delete the "id" position.
     *
     * @param id the id of the position to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/positions/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePosition(@PathVariable String id) {
        log.debug("REST request to delete Position : {}", id);
        positionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("position", id.toString())).build();
    }

}
