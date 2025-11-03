package paterben.privatedining.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;

import paterben.privatedining.api.model.ApiDiner;
import paterben.privatedining.core.model.Diner;
import paterben.privatedining.core.model.DinerReservations;
import paterben.privatedining.repository.DinerRepository;
import paterben.privatedining.repository.DinerReservationsRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTestUtils.class)
@ActiveProfiles("test")
// Integration tests for diner creation and retrieval.
// Requires a running MongoDB instance using `docker compose up -d` from the
// root directory.
public class DinerIT {
    @Autowired
    private DinerRepository dinerRepository;

    @Autowired
    private DinerReservationsRepository dinerReservationsRepository;

    @Autowired
    private IntegrationTestUtils utils;

    @BeforeEach
    void setUp() {
        dinerRepository.deleteAll();
        dinerReservationsRepository.deleteAll();
    }

    @Test
    @DisplayName("Listing diners when none exist returns empty")
    void testListDinersEmpty() throws JsonProcessingException, UnsupportedEncodingException {
        // Call list diners API.
        List<ApiDiner> diners = utils.listDinersAndGetResult();

        // Check that returned list is empty.
        assertThat(diners).isEmpty();
    }

    @Test
    @DisplayName("Getting a diner that doesn't exist returns NOT_FOUND")
    void testGetNonExistentDiner() {
        // Call get diner API.
        MvcTestResult getResult = utils.getDiner("1234");
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Diner creation works and creates diner in database")
    void testCreateDinerWorksAndCreatesDinerInDatabase() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);

        // Check that returned diner matches request.
        String dinerId = newDiner.getId();
        assertThat(dinerId).isNotBlank();
        Instant createdAt = newDiner.getCreatedAt();
        assertThat(createdAt).isNotNull();
        apiDiner.setId(dinerId);
        apiDiner.setCreatedAt(createdAt);
        assertEquals(apiDiner, newDiner);

        // Check that diner was created in DB.
        Optional<Diner> foundDiner = dinerRepository.findById(dinerId);
        assertThat(foundDiner).isPresent();
        assertEquals(dinerId, foundDiner.get().getId());
        assertEquals(createdAt, foundDiner.get().getCreatedAt());
        assertEquals(newDiner.getName(), foundDiner.get().getName());
        assertEquals(newDiner.getEmail(), foundDiner.get().getEmail());

        // Check that dinerReservations was created in DB.
        Optional<DinerReservations> foundDinerReservations = dinerReservationsRepository.findById(dinerId);
        assertThat(foundDinerReservations).isPresent();
        assertEquals(dinerId, foundDinerReservations.get().getId());
        assertThat(foundDinerReservations.get().getReservations()).isEmpty();
    }

    @Test
    @DisplayName("Diner creation followed by get for the same diner")
    void testCreateAndGetDiner() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);

        // Call get diner API.
        ApiDiner getDiner = utils.getDinerAndGetResult(newDiner.getId());

        // Check that returned diner matches created one.
        assertEquals(newDiner, getDiner);
    }

    @Test
    @DisplayName("Multiple diner creation followed by list diners returns all diners")
    void testCreateAndListMultipleDiners() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create diner API twice.
        ApiDiner apiDiner1 = new ApiDiner("diner1", "email1");
        ApiDiner apiDiner2 = new ApiDiner("diner2", "email2");
        ApiDiner newDiner1 = utils.createDinerAndGetResult(apiDiner1);
        ApiDiner newDiner2 = utils.createDinerAndGetResult(apiDiner2);

        // Call list diners API.
        List<ApiDiner> diners = utils.listDinersAndGetResult();

        // Check that returned list matches.
        assertThat(diners).satisfiesExactly(
                r -> assertEquals(newDiner1, r),
                r -> assertEquals(newDiner2, r));
    }

    @Test
    @DisplayName("Creating multiple diners with same email fails")
    void testCreateMultipleDinersWithSameEmailFails() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create diner API twice with same email.
        ApiDiner apiDiner1 = new ApiDiner("diner1", "email1");
        ApiDiner apiDiner2 = new ApiDiner("diner2", "email1");
        ApiDiner newDiner1 = utils.createDinerAndGetResult(apiDiner1);
        MvcTestResult createResult2 = utils.createDiner(apiDiner2);

        // Check that second request fails.
        assertThat(createResult2).hasStatus(HttpStatus.CONFLICT);
        assertThat(createResult2).bodyText().contains("already exists");

        // Check that only first diner exists by calling listDiners API.
        List<ApiDiner> diners = utils.listDinersAndGetResult();
        assertThat(diners).satisfiesExactly(r -> assertEquals(newDiner1, r));
    }

    @Test
    @DisplayName("Diner creation without a name fails")
    void testCreateDinerWithoutNameFails() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create diner API without name.
        ApiDiner apiDiner = new ApiDiner("", "email1");
        MvcTestResult createResult = utils.createDiner(apiDiner);

        // Check that request fails.
        assertThat(createResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(createResult).bodyText().contains("`name` is required");

        // Check that no diners exist by calling listDiners API.
        List<ApiDiner> diners = utils.listDinersAndGetResult();
        assertThat(diners).isEmpty();
    }
}
