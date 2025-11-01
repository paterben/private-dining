package paterben.privatedining.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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

import paterben.privatedining.core.model.RestaurantTables;
import paterben.privatedining.core.model.RoomType;
import paterben.privatedining.core.model.Table;
import paterben.privatedining.core.model.TableReservations;
import paterben.privatedining.repository.RestaurantTablesRepository;
import paterben.privatedining.repository.TableReservationsRepository;

@ExtendWith(MockitoExtension.class)
public class TableServiceImplTest {
    @Mock
    private RestaurantTablesRepository restaurantTablesRepository;

    @Mock
    private TableReservationsRepository tableReservationsRepository;

    @InjectMocks
    private TableServiceImpl tableService;

    @BeforeEach
    void setUp() {
        lenient().when(restaurantTablesRepository.findById(any())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("When restaurant exists, listTablesForRestaurant returns the list")
    void testListTablesForRestaurant() {
        // Arrange
        Table foundTable1 = new Table("2345", "table1", 1, 3, RoomType.HALL, 1.5);
        Table foundTable2 = new Table("3456", "table2", 0, 3, RoomType.HALL, 0);
        RestaurantTables foundRestaurantTables = new RestaurantTables("1234", Arrays.asList(foundTable1, foundTable2));
        when(restaurantTablesRepository.findById("1234")).thenReturn(Optional.of(foundRestaurantTables));

        // Act
        Optional<List<Table>> result = tableService.listTablesForRestaurant("1234");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).satisfiesExactly(
                t -> assertEquals(foundTable1, t),
                t -> assertEquals(foundTable2, t));
    }

    @Test
    @DisplayName("When restaurant exists and has an empty list of tables, listTablesForRestaurant returns an empty list")
    void testListEmptyTablesForRestaurant() {
        // Arrange
        RestaurantTables foundRestaurantTables = new RestaurantTables("1234");
        when(restaurantTablesRepository.findById("1234")).thenReturn(Optional.of(foundRestaurantTables));

        // Act
        Optional<List<Table>> result = tableService.listTablesForRestaurant("1234");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    @DisplayName("When restaurant doesn't exist, listRestaurants returns empty")
    void testListTablesForRestaurantNotFound() {
        // Act
        Optional<List<Table>> result = tableService.listTablesForRestaurant("1234");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When table exists, getTableForRestaurantById returns it")
    void testGetTableForRestaurantByIdFound() {
        // Arrange
        Table foundTable1 = new Table("2345", "table1", 1, 3, RoomType.HALL, 1.5);
        Table foundTable2 = new Table("3456", "table2", 0, 3, RoomType.HALL, 0);
        RestaurantTables foundRestaurantTables = new RestaurantTables("1234", Arrays.asList(foundTable1, foundTable2));
        when(restaurantTablesRepository.findById("1234")).thenReturn(Optional.of(foundRestaurantTables));

        // Act
        Optional<Table> result = tableService.getTableForRestaurantById("1234", "3456");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(foundTable2);
    }

    @Test
    @DisplayName("When table doesn't exist, getTableForRestaurantById returns empty")
    void testGetTableForRestaurantByIdTableNotFound() {
        // Arrange
        Table foundTable1 = new Table("2345", "table1", 1, 3, RoomType.HALL, 1.5);
        Table foundTable2 = new Table("4567", "table2", 0, 3, RoomType.HALL, 0);
        RestaurantTables foundRestaurantTables = new RestaurantTables("1234", Arrays.asList(foundTable1, foundTable2));
        when(restaurantTablesRepository.findById("1234")).thenReturn(Optional.of(foundRestaurantTables));

        // Act
        Optional<Table> result = tableService.getTableForRestaurantById("1234", "3456");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When restaurant doesn't exist, getTableForRestaurantById returns empty")
    void testGetTableForRestaurantByIdRestaurantNotFound() {
        // Act
        Optional<Table> result = tableService.getTableForRestaurantById("1234", "3456");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When table is valid and restaurant exists, addTableToRestaurant saves to restaurantTables and tableReservations and returns new table")
    void testAddTableToRestaurant() {
        // Arrange
        Table foundTable1 = new Table("2345", "table1", 1, 3, RoomType.HALL, 1.5);
        Table foundTable2 = new Table("3456", "table2", 0, 3, RoomType.HALL, 0);
        RestaurantTables foundRestaurantTables = new RestaurantTables("1234",
                new ArrayList<Table>(Arrays.asList(foundTable1, foundTable2)));
        when(restaurantTablesRepository.findById("1234")).thenReturn(Optional.of(foundRestaurantTables));
        when(restaurantTablesRepository.save(any())).thenAnswer(makeRestaurantTablesAnswer());
        when(tableReservationsRepository.save(any())).thenAnswer(makeTableReservationsAnswer());

        // Act
        Table table = new Table("table3", 1, 3, RoomType.HALL, 1.5);
        Table result = tableService.addTableToRestaurant("1234", table);

        // Assert
        Table expectedTable = new Table(result.getId(), "table3", 1, 3, RoomType.HALL, 1.5);
        assertThat(result).isEqualTo(expectedTable);
        RestaurantTables restaurantTables = new RestaurantTables("1234",
                Arrays.asList(foundTable1, foundTable2, expectedTable));
        verify(restaurantTablesRepository).save(ArgumentMatchers.eq(restaurantTables));
        TableReservations tableReservations = new TableReservations(result.getId(), "1234");
        verify(tableReservationsRepository).save(ArgumentMatchers.eq(tableReservations));
    }

    @Test
    @DisplayName("When restaurant doesn't exist, addTableToRestaurant fails with NOT_FOUND")
    void testAddTableToRestaurantNotFound() {
        // Act
        Table table = new Table("table3", 1, 3, RoomType.HALL, 1.5);
        try {
            tableService.addTableToRestaurant("1234", table);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getMessage()).contains("Restaurant with ID 1234 not found");
        }
    }

    @Test
    @DisplayName("When table with same name already exists in restaurant, addTableToRestaurant fails with CONFLICT")
    void testAddTableToRestaurantNameAlreadyExists() {
        // Arrange
        Table foundTable1 = new Table("2345", "table1", 1, 3, RoomType.HALL, 1.5);
        Table foundTable2 = new Table("3456", "table2", 0, 3, RoomType.HALL, 0);
        RestaurantTables foundRestaurantTables = new RestaurantTables("1234",
                new ArrayList<Table>(Arrays.asList(foundTable1, foundTable2)));
        when(restaurantTablesRepository.findById("1234")).thenReturn(Optional.of(foundRestaurantTables));

        // Act
        Table table = new Table("table2", 1, 3, RoomType.HALL, 1.5);
        try {
            tableService.addTableToRestaurant("1234", table);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getMessage()).contains("Table with name \"table2\" already exists");
        }
    }

    @Test
    @DisplayName("When table to create has ID set, addTableToRestaurant fails with BAD_REQUEST")
    void testAddTableToRestaurantWithIdFailsWithBadRequest() {
        // Act
        Table table = new Table("table1", 1, 3, RoomType.HALL, 1.5);
        table.setId("2345");
        try {
            tableService.addTableToRestaurant("1234", table);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`id` must not be set");
        }
    }

    @Test
    @DisplayName("When table to create doesn't have name set, addTableToRestaurant fails with BAD_REQUEST")
    void testAddTableToRestaurantWithoutNameFailsWithBadRequest() {
        // Act
        Table table = new Table("", 1, 3, RoomType.HALL, 1.5);
        try {
            tableService.addTableToRestaurant("1234", table);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`name` is required");
        }
    }

    @Test
    @DisplayName("When table to create doesn't have maxCapacity set, addTableToRestaurant fails with BAD_REQUEST")
    void testAddTableToRestaurantWithoutMaxCapacityFailsWithBadRequest() {
        // Act
        Table table = new Table("table1", 1, 0, RoomType.HALL, 1.5);
        try {
            tableService.addTableToRestaurant("1234", table);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`maxCapacity` is required");
        }
    }

    @Test
    @DisplayName("When table to create doesn't have roomType set, addTableToRestaurant fails with BAD_REQUEST")
    void testAddTableToRestaurantWithoutRoomTypeFailsWithBadRequest() {
        // Act
        Table table = new Table("table1", 1, 3, null, 1.5);
        try {
            tableService.addTableToRestaurant("1234", table);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`roomType` is required");
        }
    }

    private Answer<RestaurantTables> makeRestaurantTablesAnswer() {
        return new Answer<RestaurantTables>() {
            public RestaurantTables answer(InvocationOnMock invocation) {
                RestaurantTables rt = invocation.getArgument(0, RestaurantTables.class);
                return rt;
            }
        };
    }

    private Answer<TableReservations> makeTableReservationsAnswer() {
        return new Answer<TableReservations>() {
            public TableReservations answer(InvocationOnMock invocation) {
                TableReservations tr = invocation.getArgument(0, TableReservations.class);
                return tr;
            }
        };
    }
}
