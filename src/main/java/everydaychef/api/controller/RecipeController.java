package everydaychef.api.controller;

import everydaychef.api.model.Ingredient;
import everydaychef.api.model.Recipe;
import everydaychef.api.model.User;
import everydaychef.api.repository.*;
import everydaychef.api.service.FileStorageService;
import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
public class RecipeController {
    final private UserRepository userRepository;
    final private FileStorageService storageService;
//    final private FamilyRepository familyRepository;
    final private IngredientRepository ingredientRepository;
    final private RecipeRepository recipeRepository;
    final private ShoppingListRepository shoppingListRepository;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public RecipeController(UserRepository userRepository,
                            FamilyRepository familyRepository,
                            IngredientRepository ingredientRepository, FileStorageService storageService, RecipeRepository recipeRepository, ShoppingListRepository shoppingListRepository) {
        this.userRepository = userRepository;
        this.storageService = storageService;
//        this.familyRepository = familyRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.shoppingListRepository = shoppingListRepository;
    }

//    @GetMapping("recipes")
//    public ResponseEntity<List<Recipe>> getAllRecipes(){
//        return ResponseEntity.ok().body(recipeRepository.findAll());
//    }


//    @GetMapping("recipe/{filename}/image")
//    public ResponseEntity<Resource> getRecipeLocalImage(@PathVariable("filename") String filename){
//        if(filename.contains("local:")){
//            try{
//                Resource file = storageService.loadFileAsResource(filename.replaceFirst("^local:", ""));
//                logger.info("File storage: Received file " + file.getFilename());
//                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//        return ResponseEntity.notFound().build();
//    }
//



    @PostMapping("recipe")
    public ResponseEntity<?> createRecipe(@RequestParam("creatorId") String creatorId, @RequestParam("recipeName") String recipeName,
                                          @RequestParam("recipeDescription") String recipeDescription,
                                          @RequestParam("picture") MultipartFile picture){
        User user = userRepository.findById(Integer.parseInt(creatorId)).orElse(null);
        if(user != null){
            if(recipeRepository.findByName(recipeName).isPresent()){
                return ResponseEntity.badRequest().body("Recipe exists with name " + recipeName);
            }
            String originalImageName = picture.getOriginalFilename();
            if(originalImageName != null){
                String newRecipeImageName = recipeName.replace(" ", "_").toLowerCase() +"_image" + originalImageName.substring(originalImageName.indexOf("."));
                storageService.storeFile(picture, newRecipeImageName);

                Recipe recipe = new Recipe(recipeName, recipeDescription, "local:img/" + newRecipeImageName, user);
                recipe = recipeRepository.save(recipe);
                return ResponseEntity.ok().body(recipe);
            }
            return ResponseEntity.badRequest().body("Image was null!");
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("recipe/{recipeId}/shopping_list/{shoppingListId}")
    public ResponseEntity<Boolean> addToShoppingList(@PathVariable String recipeId, @PathVariable String shoppingListId){
        return recipeRepository.findById(Integer.parseInt(recipeId))
                .map(recipe -> shoppingListRepository.findById(Integer.parseInt(shoppingListId)).map(shoppingList -> {
                    shoppingList.getIngredients().addAll(recipe.getIngredients());
                    shoppingListRepository.save(shoppingList);
                    return ResponseEntity.ok().body(true);
                }).orElse(ResponseEntity.notFound().build())).orElse(ResponseEntity.notFound().build());
    }







    @PutMapping("recipe/{id}/ingredients")
    public ResponseEntity<Recipe> updateIngredients(@PathVariable String id, @RequestBody Map<String, ArrayList<Integer>> ingredientsBody){
        return recipeRepository.findById(Integer.parseInt(id)).map(recipe -> {
            ArrayList<Ingredient> ingredients = (ArrayList<Ingredient>) ingredientRepository.findAllById(ingredientsBody.get("ingredients"));
            recipe.setIngredients(new HashSet<>(ingredients));
            recipe = recipeRepository.save(recipe);
            return ResponseEntity.ok().body(recipe);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("recipe/{recipeId}/user/{userId}/{rate}")
    public ResponseEntity<Recipe> rateRecipe(@PathVariable String recipeId, @PathVariable String userId, @PathVariable String rate){
        User user = userRepository.findById(Integer.parseInt(userId)).orElse(null);
        if(user != null){
            return recipeRepository.findById(Integer.parseInt(recipeId))
                    .map(recipe -> {
                        if(rate.equals("like")) likeRecipe(user, recipe); else dislikeRecipe(user, recipe);
                        userRepository.save(user);
                        return ResponseEntity.ok().body(recipeRepository.save(recipe));
                    }).orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }

    private void likeRecipe(User user, Recipe recipe){
        user.getRecipes().add(recipe);
        recipe.getLikes().add(user);
        recipe.setNumber_of_likes(recipe.getNumber_of_likes()+1);
    }

    private void dislikeRecipe(User user, Recipe recipe){
        user.getRecipes().remove(recipe);
        recipe.getLikes().remove(user);
        recipe.setNumber_of_likes(recipe.getNumber_of_likes()-1);
    }

}
