package paterben.privatedining.api.conversion;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.core.model.Restaurant;

@Component
public class RestaurantApiConverter {
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
}
