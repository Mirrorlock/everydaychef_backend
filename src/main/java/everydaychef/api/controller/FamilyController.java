package everydaychef.api.controller;


import everydaychef.api.model.*;
import everydaychef.api.repository.*;
import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class FamilyController {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final ShoppingListRepository shoppingListRepository;
    private Logger logger = LoggerFactory.getLogger(UserController.class);


    public FamilyController(FamilyRepository familyRepository, UserRepository userRepository,
                            IngredientRepository ingredientRepository, RecipeRepository recipeRepository, ShoppingListRepository shoppingListRepository) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.shoppingListRepository = shoppingListRepository;
    }

    private Optional<Family> findFamilyById(String familyIdStr){
        int familyId = Integer.parseInt(familyIdStr);
        return familyRepository.findById(familyId);
    }



    @GetMapping("/family")
    public ResponseEntity<List<Family>> index(){
        return new ResponseEntity<>( familyRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/family/{id}")
    public ResponseEntity<Family> show(@PathVariable String id){
        return findFamilyById(id)
                .map(family -> new ResponseEntity<>(family, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/family/{id}/nonmembers")
    public ResponseEntity<Set<User>> getNonMembers(@PathVariable("id") String id){
        Family family = findFamilyById(id).orElse(null);
        ResponseEntity<Set<User>> result;
        if(family != null){
            Set<User> nonMembers = userRepository.findAll()
                    .stream()
                    .filter(user -> !user.getFamily().equals(family))
                    .collect(Collectors.toSet());
            result = ResponseEntity.ok().body(nonMembers);
        }else{
            result = ResponseEntity.notFound().build();
        }
        return result;
    }

    @GetMapping("/family/{id}/members")
    public ResponseEntity<Set<User>> getMembers(@PathVariable String id){
        return findFamilyById(id).map(family -> ResponseEntity.ok().body(family.getUsers()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("family/{id}/ingredients")
    public ResponseEntity<List<Ingredient>> getIngredients(@PathVariable String id){
        return findFamilyById(id)
                .map(family -> ResponseEntity.ok().body(family.getIngredients()))
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("family/{id}/recommended_recipes")
    public ResponseEntity<List<Recipe>> getRecommendedRecipes(@PathVariable String id){
        List<Recipe> allRecipes = recipeRepository.findAll();
        return findFamilyById(id).map(family -> {
            List<Recipe> recommendedRecipes = allRecipes;
            List<Ingredient> familyIngredients = family.getIngredients();
            if(!familyIngredients.isEmpty()) {
                Map<Recipe, Float> recipeSuitabilityPercentages = new HashMap<>();
                allRecipes.forEach(recipe -> {
                    float percentage = 0;
                    int numIngredients = recipe.getIngredients().size();
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (familyIngredients.contains(ingredient)) {
                            percentage += 100.0 / numIngredients;
                        }
                    }
                    recipeSuitabilityPercentages.put(recipe, percentage);
                });
                recommendedRecipes = recipeSuitabilityPercentages.entrySet()
                        .stream()
                        .sorted(Map.Entry.<Recipe, Float>comparingByValue().reversed())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
            }
            return ResponseEntity.ok().body(recommendedRecipes);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("family/{id}/shopping_lists")
    public ResponseEntity<List<ShoppingList>> getFamilyShoppingLists(@PathVariable String id){
        return familyRepository
                .findById(Integer.parseInt(id))
                .map(family -> ResponseEntity.ok().body(family.getShoppingLists()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("family/{familyId}/ingredients/{ingredientId}")
    public ResponseEntity<Family> addIngredients(@PathVariable String familyId, @PathVariable String ingredientId){
        int ingredientIntId = Integer.parseInt(ingredientId);
        Ingredient ingredient = ingredientRepository.findById(ingredientIntId).orElse(null);
        if(ingredient != null){
            return findFamilyById(familyId)
                    .map(family -> {
                        family.getIngredients().add(ingredient);
                        return family;
                    })
                    .map(family -> ResponseEntity.ok().body(familyRepository.save(family)))
                    .orElse(ResponseEntity.notFound().build());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

//    @PostMapping("/family/search")
//    public List<Family> search(@RequestBody Map<String, String> body){
//        String searchTerm = body.get("text");
//        return familyRepository.findBy(searchTerm, searchTerm);
//    }

    @PostMapping("/family")
    public ResponseEntity<Family> create(@RequestBody Map<String, String> body){
        String name = body.get("name");
        return new ResponseEntity<>(familyRepository.save(new Family(name)), HttpStatus.OK);
    }






    @PutMapping("/family/{id}")
    public ResponseEntity<Family> update(@PathVariable String id, @RequestBody Map<String, String> body){

        Family family = findFamilyById(id).orElse(new Family());
        family.setName(body.get("name"));
        return new ResponseEntity<>(familyRepository.save(family), HttpStatus.OK);
    }






    @DeleteMapping("family/{familyId}/ingredients/{ingredientId}")
    public ResponseEntity<List<Ingredient>> removeIngredients(@PathVariable String familyId, @PathVariable String ingredientId){
        int ingredientIntId = Integer.parseInt(ingredientId);
        Ingredient ingredient = ingredientRepository.findById(ingredientIntId).orElse(null);
        if(ingredient != null){
            return findFamilyById(familyId)
                    .map(family -> {
                        family.getIngredients().remove(ingredient);
                        familyRepository.save(family);
                        return family.getIngredients();
                    })
                    .map(ingredients -> ResponseEntity.ok().body(ingredients))
                    .orElse(ResponseEntity.notFound().build());
        }else{
            return ResponseEntity.notFound().build();
        }
    }
//
//    @DeleteMapping("/family/{id}")
//    public ResponseEntity<Boolean> delete(@PathVariable String id){
//        int familyId = Integer.parseInt(id);
//        familyRepository.findById(familyId).ifPresent(family -> {
//            family.getUsers().forEach(user->user.setFamily(new Family(user.getName() + "'s Family")));
//        });
//        familyRepository.deleteById(familyId);
//        return new ResponseEntity<>(true, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/family/{familyId}/user/{userId}")
//    public ResponseEntity<Boolean> kickUser(@PathVariable String familyId, @PathVariable String userId){
//        int userIdNum = Integer.parseInt(userId);
//        Family family = familyRepository.findById(Integer.parseInt(familyId)).orElse(null);
//        boolean result = false;
//        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
//        if(family != null) {
//            User foundUser = family.getUsers()
//                    .stream()
//                    .filter(user -> user.getId() == userIdNum)
//                    .findAny()
//                    .orElse(null);
//            if (foundUser != null) {
//                family.getUsers().remove(foundUser);
//                result = true;
//                httpStatus = HttpStatus.OK;
//            }
//        }
//        return new ResponseEntity<>(result, httpStatus);
//    }

}
