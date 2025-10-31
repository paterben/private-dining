package paterben.privatedining.service;

import java.util.List;
import java.util.Optional;

import paterben.privatedining.core.model.Table;

public interface TableService {
    public Table addTableToRestaurant(String restaurantId, Table table);

    public Optional<Table> getTableForRestaurantById(String restaurantId, String tableId);

    public Optional<List<Table>> listTablesForRestaurant(String restaurantId);
}
