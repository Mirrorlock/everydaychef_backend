package everydaychef.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="shopping_lists")
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String name;


    @ManyToOne
    @JoinColumn(name = "family_id")
    @JsonManagedReference
    private Family creatorFamily;

    @ManyToMany
    @JoinTable(name="shopping_list_ingredients", joinColumns = @JoinColumn(name="shopping_list_id"),  inverseJoinColumns = @JoinColumn(name="ingredient_id"))
    @JsonManagedReference
    private Set<Ingredient> ingredients;

    public ShoppingList() {
    }

    public ShoppingList(Family creatorFamily, String name){
        this.name = name;
        this.creatorFamily = creatorFamily;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Family getCreatorFamily() {
        return creatorFamily;
    }

    public void setCreatorFamily(Family creatorFamily) {
        this.creatorFamily = creatorFamily;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
