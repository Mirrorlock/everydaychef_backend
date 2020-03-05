package everydaychef.api.controller;

import everydaychef.api.model.Ingredient;
import everydaychef.api.model.ShoppingList;
import everydaychef.api.repository.FamilyRepository;
import everydaychef.api.repository.IngredientRepository;
import everydaychef.api.repository.ShoppingListRepository;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class ShoppingListController {
    private final ShoppingListRepository shoppingListRepository;
    private final FamilyRepository familyRepository;
    private final IngredientRepository ingredientRepository;

    public ShoppingListController(ShoppingListRepository shoppingListRepository, FamilyRepository familyRepository, IngredientRepository ingredientRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.familyRepository = familyRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping("shopping_list/{id}/ingredients")
    public ResponseEntity<Set<Ingredient>> getIngredients(@PathVariable String id){
        return shoppingListRepository
                .findById(Integer.parseInt(id))
                .map(shoppingList -> ResponseEntity.ok().body(shoppingList.getIngredients()))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("shopping_list/family/{id}")
    public ResponseEntity<ShoppingList> create(@PathVariable String id, @RequestBody Map<String, String> body){
        return familyRepository.findById(Integer.parseInt(id)).map(family -> {
            ShoppingList newShoppingList = new ShoppingList(family, body.get("name"));
            newShoppingList = shoppingListRepository.save(newShoppingList);
            family.getShoppingLists().add(newShoppingList);
            return ResponseEntity.ok().body(newShoppingList);
        }).orElse(ResponseEntity.notFound().build());

    }


    @PutMapping("shopping_list/{shoppingListId}/ingredient/{ingredientId}")
    public ResponseEntity<Set<Ingredient>> addItem(@PathVariable String shoppingListId, @PathVariable String ingredientId){
        return shoppingListRepository.findById(Integer.parseInt(shoppingListId)).map(shoppingList ->
                ingredientRepository.findById(Integer.parseInt(ingredientId)).map(ingredient -> {
                    shoppingList.getIngredients().add(ingredient);
                    shoppingListRepository.save(shoppingList);
                    return ResponseEntity.ok().body(shoppingList.getIngredients());
                }).orElse(ResponseEntity.notFound().build())
        ).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("shopping_list/{id}")
    public ResponseEntity<Boolean> deleteShoppingList(@PathVariable String id){
        return shoppingListRepository.findById(Integer.parseInt(id)).map(shoppingList -> {
            shoppingList.getCreatorFamily().getShoppingLists().remove(shoppingList);
            familyRepository.save(shoppingList.getCreatorFamily());
            shoppingListRepository.delete(shoppingList);
            return ResponseEntity.ok().body(true);
        }).orElse(ResponseEntity.notFound().build());

    }



    @DeleteMapping("shopping_list/{shoppingListId}/ingredient/{ingredientId}")
    public ResponseEntity<ShoppingList> removeIngredient(@PathVariable String shoppingListId, @PathVariable String ingredientId){
        return shoppingListRepository.findById(Integer.parseInt(shoppingListId)).map(shoppingList ->
                ingredientRepository.findById(Integer.parseInt(ingredientId)).map(ingredient -> {
                    System.out.println("Ingredients before remove: " + shoppingList.getIngredients());
                    shoppingList.getIngredients().remove(ingredient);
                    System.out.println("Ingredients after remove: " + shoppingList.getIngredients());
                    shoppingListRepository.save(shoppingList);
                    return ResponseEntity.ok().body(shoppingList);
            }).orElse(ResponseEntity.notFound().build())
        ).orElse(ResponseEntity.notFound().build());
    }
}
