package everydaychef.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String name;
    private String password;
    private char account_type;

    @ManyToOne
    @JoinColumn(name="family_id")
    @JsonManagedReference
    private Family family;

    @ManyToMany
    @JoinTable(name="liked_recipes", inverseJoinColumns = @JoinColumn(name="recipe_id"))
    @JsonBackReference
    private Set<Recipe> likedRecipes;

    @JoinTable(name="user_invitations",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns={@JoinColumn(name="family_id")})
    @ManyToMany()
    @JsonBackReference
    private Set<Family> familyInviters;

    public User() {
    }

    public User(String name, String password, Family family){
        this.name = name;
        this.password = password;
        this.family = family;
        likedRecipes = new HashSet<>();
    }

    public User(String name, char account_type, Family family){
        this.name = name;
        this.account_type = account_type;
        this.family = family;
        likedRecipes = new HashSet<>();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public void setDefaultFamily(){
        this.family = new Family(name + "'s Family");
    }

    public Set<Recipe> getLikedRecipes() {
        return likedRecipes;
    }

    public void setLikedRecipes(Set<Recipe> likedRecipes) {
        this.likedRecipes = likedRecipes;
    }

    public char getAccount_type() {
        return account_type;
    }

    public void setAccount_type(char account_type) {
        this.account_type = account_type;
    }

    public void setInvitations(Set<Family> invitations) {
        this.familyInviters = invitations;
    }

    public void addInvitation(Family inviter){
        this.familyInviters.add(inviter);
    }

    public Set<Family> getInvitations() {
        return familyInviters;
    }


}
