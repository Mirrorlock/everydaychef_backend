package everydaychef.api.controller;

import everydaychef.api.model.Ingredient;
import everydaychef.api.repository.FamilyRepository;
import everydaychef.api.repository.IngredientRepository;
import everydaychef.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IngredientController {

    final private UserRepository userRepository;
    final private FamilyRepository familyRepository;
    final private IngredientRepository ingredientRepository;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public IngredientController(UserRepository userRepository,
                                FamilyRepository familyRepository,
                                IngredientRepository ingredientRepository) {
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping("ingredients")
    public ResponseEntity<List<Ingredient>> getAllIngredients(){
        return ResponseEntity.ok().body(ingredientRepository.findAll());
    }

}