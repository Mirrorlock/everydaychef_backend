package everydaychef.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="recipes")
public class Recipe {

    public Recipe(){}

    public Recipe(String name, String description, String picture_url, User creator) {
        this.name = name;
        this.description = description;
        this.picture_url = picture_url;
        this.ingredients = new HashSet<Ingredient>();
        this.likes = new HashSet<User>();
        this.creator = creator;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    private String name;
    private String description;
    private String picture_url;
    private int number_of_likes;



    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonIgnoreProperties({"family", "invitations"})
    @JsonManagedReference
    private User creator;

    @ManyToMany
    @JoinTable(name = "liked_recipes",
            joinColumns = @JoinColumn(name="recipe_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonManagedReference
    private Set<User> likes;

    @ManyToMany
    @JoinTable(name="recipe_ingredients", inverseJoinColumns = @JoinColumn(name="ingredient_id"))
    @JsonManagedReference
    private Set<Ingredient> ingredients;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public int getNumber_of_likes() {
        return number_of_likes;
    }

    public void setNumber_of_likes(int number_of_likes) {
        this.number_of_likes = number_of_likes;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe)) return false;
        Recipe recipe = (Recipe) o;
        return getId() == recipe.getId() &&
                getNumber_of_likes() == recipe.getNumber_of_likes() &&
                getName().equals(recipe.getName()) &&
                Objects.equals(getDescription(), recipe.getDescription()) &&
                Objects.equals(getPicture_url(), recipe.getPicture_url());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getPicture_url(), getNumber_of_likes());
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture_url='" + picture_url + '\'' +
                ", number_of_likes=" + number_of_likes +
                ", creator=" + creator +
                ", likes=" + likes +
                ", ingredients=" + ingredients +
                '}';
    }
}
