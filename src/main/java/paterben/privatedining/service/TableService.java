package paterben.privatedining.service;

import java.util.List;
import java.util.Optional;

import paterben.privatedining.core.model.Table;

/**
 * Service for managing {@link Table Tables}.
 */
public interface TableService {
    /**
     * Adds the given table to the restaurant with the given ID.
     * 
     * @param restaurantId the restaurant ID.
     * @param table        the table to add. Required fields must be set. Fields
     *                     that are set automatically must not be set.
     * @throws ServiceException if the {@code table} is invalid, the restaurant does
     *                          not exist or a table with the same name already
     *                          exists within the restaurant.
     * @return the created table.
     */
    public Table addTableToRestaurant(String restaurantId, Table table) throws ServiceException;

    /**
     * Gets the table with the given ID for the restaurant with the given ID.
     * 
     * @param restaurantId the restaurant ID.
     * @param tableId      the table ID.
     * @return the table metadata, or an empty {@link Optional} if the
     *         restaurant or table does not exist.
     */
    public Optional<Table> getTableForRestaurantById(String restaurantId, String tableId);

    /**
     * Lists all tables for the given restaurant.
     * 
     * @param restaurantId the restaurant ID.
     * @return the list of tables, or an empty {@link Optional} if the restaurant
     *         does not exist.
     */
    public Optional<List<Table>> listTablesForRestaurant(String restaurantId);
}
