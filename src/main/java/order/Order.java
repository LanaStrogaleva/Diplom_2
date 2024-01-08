package order;

import java.util.ArrayList;

public class Order {
    ArrayList<String> ingredients;
    public Order(){
        ingredients = new ArrayList<>();
    }
    public void addIngredientToOrder(String ingredient) {
        this.ingredients.add(ingredient);
    }


}
