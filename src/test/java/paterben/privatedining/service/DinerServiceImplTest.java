package paterben.privatedining.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;

import paterben.privatedining.core.model.Diner;
import paterben.privatedining.core.model.DinerReservations;
import paterben.privatedining.repository.DinerRepository;
import paterben.privatedining.repository.DinerReservationsRepository;

@ExtendWith(MockitoExtension.class)
public class DinerServiceImplTest {
    @Mock
    private DinerRepository dinerRepository;

    @Mock
    private DinerReservationsRepository dinerReservationsRepository;

    @InjectMocks
    private DinerServiceImpl dinerService;

    @BeforeEach
    void setUp() {
        lenient().when(dinerRepository.findAll()).thenReturn(Collections.emptyList());
        lenient().when(dinerRepository.findById(any())).thenReturn(Optional.empty());
        lenient().when(dinerRepository.findByEmail(any())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("When diners exist, listDiners returns the list")
    void testListDiners() {
        // Arrange
        Diner foundDiner1 = new Diner("1234", "diner1", "email1", Instant.ofEpochSecond(1234));
        Diner foundDiner2 = new Diner("2345", "diner2", "email2", Instant.ofEpochSecond(2345));
        when(dinerRepository.findAll()).thenReturn(Arrays.asList(foundDiner1, foundDiner2));

        // Act
        List<Diner> result = dinerService.listDiners();

        // Assert
        assertThat(result).satisfiesExactly(
                d -> assertEquals(foundDiner1, d),
                d -> assertEquals(foundDiner2, d));
    }

    @Test
    @DisplayName("When no diners exist, listDiners returns an empty list")
    void testListDinersEmpty() {
        // Act
        List<Diner> result = dinerService.listDiners();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("When diner exists, getDinerById returns it")
    void testGetDinerByIdFound() {
        // Arrange
        Diner foundDiner = new Diner("1234", "diner1", "email1", Instant.ofEpochSecond(1234));
        when(dinerRepository.findById("1234")).thenReturn(Optional.of(foundDiner));

        // Act
        Optional<Diner> result = dinerService.getDinerById("1234");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(foundDiner);
    }

    @Test
    @DisplayName("When diner doesn't exist, getDinerById returns empty")
    void testGetDinerByIdNotFound() {
        // Act
        Optional<Diner> result = dinerService.getDinerById("1234");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When diner is valid, createDiner saves to diner and dinerReservations and returns new diner")
    void testCreateDiner() {
        // Arrange
        when(dinerRepository.save(any()))
                .thenAnswer(makeSetIdAndCreatedTimeOnDinerAnswer("1234", Instant.ofEpochSecond(1234)));
        when(dinerReservationsRepository.save(any())).thenAnswer(makeDinerReservationsAnswer());

        // Act
        Diner diner = new Diner("diner1", "email1");
        Diner result = dinerService.createDiner(diner);

        // Assert
        Diner expectedDiner = new Diner("1234", "diner1", "email1", Instant.ofEpochSecond(1234));
        assertThat(result).isEqualTo(expectedDiner);
        verify(dinerRepository).save(ArgumentMatchers.eq(diner));
        DinerReservations dinerReservations = new DinerReservations("1234");
        verify(dinerReservationsRepository).save(ArgumentMatchers.eq(dinerReservations));
    }

    @Test
    @DisplayName("createDiner truncates created time to millis")
    void testCreateRestaurantTruncatesCreatedTimeToMillis() {
        // Arrange
        Instant nano = Instant.ofEpochSecond(1234, 111222333);
        Instant truncated = Instant.ofEpochSecond(1234, 111000000);
        when(dinerRepository.save(any()))
                .thenAnswer(makeSetIdAndCreatedTimeOnDinerAnswer("1234", nano));
        when(dinerReservationsRepository.save(any())).thenAnswer(makeDinerReservationsAnswer());

        // Act
        Diner diner = new Diner("diner1", "email1");
        Diner result = dinerService.createDiner(diner);

        // Assert
        Diner expectedDiner = new Diner("1234", "diner1", "email1", truncated);
        assertThat(result).isEqualTo(expectedDiner);
    }

    @Test
    @DisplayName("When diner with same email already exists, createDiner fails with CONFLICT")
    void testCreateDinerSameEmailFailsWithConflict() {
        // Arrange
        Diner foundDiner = new Diner("1234", "diner1", "email1", Instant.ofEpochSecond(1111));
        when(dinerRepository.findByEmail("email1")).thenReturn(Optional.of(foundDiner));

        // Act
        Diner diner = new Diner("diner2", "email1");
        try {
            dinerService.createDiner(diner);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getMessage()).contains("already exists");
        }
    }

    @Test
    @DisplayName("When diner to create has ID set, createDiner fails with BAD_REQUEST")
    void testCreateDinerWithIdFailsWithBadRequest() {
        // Act
        Diner diner = new Diner("diner1", "email1");
        diner.setId("1234");
        try {
            dinerService.createDiner(diner);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`id` must not be set");
        }
    }

    @Test
    @DisplayName("When diner to create doesn't have name set, createDiner fails with BAD_REQUEST")
    void testCreateDinerWithoutNameFailsWithBadRequest() {
        // Act
        Diner diner = new Diner("", "email1");
        try {
            dinerService.createDiner(diner);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`name` is required");
        }
    }

    @Test
    @DisplayName("When diner to create doesn't have email set, createDiner fails with BAD_REQUEST")
    void testCreateDinerWithoutEmailFailsWithBadRequest() {
        // Act
        Diner diner = new Diner("diner1", "");
        try {
            dinerService.createDiner(diner);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`email` is required");
        }
    }

    @Test
    @DisplayName("When diner to create has createdAt set, createDiner fails with BAD_REQUEST")
    void testCreateDinerWithCreatedAtFailsWithBadRequest() {
        // Act
        Diner diner = new Diner("diner1", "email1");
        diner.setCreatedAt(Instant.ofEpochSecond(1234));
        try {
            dinerService.createDiner(diner);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`createdAt` must not be set");
        }
    }

    private Answer<Diner> makeSetIdAndCreatedTimeOnDinerAnswer(String id, Instant createdAt) {
        return new Answer<Diner>() {
            public Diner answer(InvocationOnMock invocation) {
                Diner diner = invocation.getArgument(0, Diner.class);
                diner.setId(id);
                diner.setCreatedAt(createdAt);
                return diner;
            }
        };
    }

    private Answer<DinerReservations> makeDinerReservationsAnswer() {
        return new Answer<DinerReservations>() {
            public DinerReservations answer(InvocationOnMock invocation) {
                DinerReservations dr = invocation.getArgument(0, DinerReservations.class);
                return dr;
            }
        };
    }
}
