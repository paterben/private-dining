package paterben.privatedining.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import paterben.privatedining.core.model.RestaurantTables;
import paterben.privatedining.core.model.Table;
import paterben.privatedining.core.model.TableReservations;
import paterben.privatedining.repository.RestaurantTablesRepository;
import paterben.privatedining.repository.TableReservationsRepository;

@Service
public class TableServiceImpl implements TableService {
    @Autowired
    private RestaurantTablesRepository restaurantTablesRepository;

    @Autowired
    private TableReservationsRepository tableReservationsRepository;

    @Override
    @Transactional
    public Table addTableToRestaurant(String restaurantId, Table table) {
        Optional<RestaurantTables> restaurantTables = restaurantTablesRepository.findById(restaurantId);
        if (restaurantTables.isEmpty()) {
            throw new RestaurantServiceException("Restaurant with ID " + restaurantId + " not found",
                    HttpStatus.NOT_FOUND);
        }
        if (restaurantTables.get().getTables().stream()
                .anyMatch(t -> t.getName() != null && t.getName().equals(table.getName()))) {
            throw new RestaurantServiceException("Table with name \"" + table.getName() + "\" already exists",
                    HttpStatus.CONFLICT);
        }
        // We generate the table ID ourselves since it is an embedded document in
        // restaurantTables.
        String tableId = new ObjectId().toString();
        table.setId(tableId);
        restaurantTables.get().getTables().addLast(table);
        RestaurantTables newRestaurantTables = restaurantTablesRepository.save(restaurantTables.get());
        Table newTable = newRestaurantTables.getTables().getLast();

        // Create an empty document in the tableReservations repository within the same
        // transaction, so it does not have to be created when adding the first
        // reservation.
        TableReservations tableReservations = new TableReservations();
        tableReservations.setId(tableId);
        tableReservationsRepository.save(tableReservations);

        return newTable;
    }

    @Override
    public Optional<Table> getTableForRestaurantById(String restaurantId, String tableId) {
        Optional<RestaurantTables> restaurantTables = restaurantTablesRepository.findById(restaurantId);
        if (!restaurantTables.isPresent()) {
            return Optional.empty();
        }
        Optional<Table> table = restaurantTables.get().getTables().stream()
                .filter(t -> t.getId() != null && t.getId().equals(tableId)).findFirst();
        return table;
    }

    public Optional<List<Table>> listTablesForRestaurant(String restaurantId) {
        Optional<RestaurantTables> restaurantTables = restaurantTablesRepository.findById(restaurantId);
        if (!restaurantTables.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(restaurantTables.get().getTables());
    }
}
