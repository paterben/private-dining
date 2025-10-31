package paterben.privatedining.api.conversion;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.api.model.ApiTable;
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.core.model.Table;

@Component
public class ApiConverter {
    @Autowired
    private ModelMapper modelMapper;

    public Restaurant ToCore(ApiRestaurant apiRestaurant) {
        Restaurant restaurant = this.modelMapper.map(apiRestaurant, Restaurant.class);
        return restaurant;
    }

    public ApiRestaurant ToApi(Restaurant restaurant) {
        ApiRestaurant apiRestaurant = this.modelMapper.map(restaurant, ApiRestaurant.class);
        return apiRestaurant;
    }

    public Table ToCore(ApiTable apiTable) {
        Table table = this.modelMapper.map(apiTable, Table.class);
        return table;
    }

    public ApiTable ToApi(Table table) {
        ApiTable apiTable = this.modelMapper.map(table, ApiTable.class);
        return apiTable;
    }
}
