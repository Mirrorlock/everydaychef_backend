package everydaychef.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import everydaychef.api.repository.FamilyRepository;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String name;
    private String email;
    private String password;

    private char account_type;

    public Set<Family> getFamilyInviters() {
        return familyInviters;
    }

    public void setFamilyInviters(Set<Family> familyInviters) {
        this.familyInviters = familyInviters;
    }

    @ManyToOne
    @JoinColumn(name="family_id")
    @JsonManagedReference
    private Family family;

    @OneToMany(mappedBy = "creator")
    @JsonBackReference
    private List<Recipe> recipes;

    @ManyToMany
    @JoinTable(name="liked_recipes", inverseJoinColumns = @JoinColumn(name="recipe_id"))
    @JsonBackReference
    private Set<Recipe> likedRecipes;

    @JoinTable(name="user_invitations",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns={@JoinColumn(name="family_id")})
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonBackReference
    private Set<Family> familyInviters;

    public User() {
        likedRecipes = new HashSet<>();
        familyInviters = new HashSet<>();
    }

    public User(String name, String email, String password, Family family, char account_type){
        this.name = name;
        this.email = email;
        this.password = password;
        this.account_type = account_type;
        this.family = family;
        likedRecipes = new HashSet<>();
        familyInviters = new HashSet<>();
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

    public Family setDefaultFamily(FamilyRepository familyRepository){
        String defaultFamilyName = name.substring(0,1).toUpperCase() + name.substring(1) + "'s Family";
        familyRepository.findByName(defaultFamilyName).ifPresentOrElse(family -> {
            this.family = family;
        }, ()-> {
            this.family = familyRepository.save(new Family(defaultFamilyName));
        });
        return family;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", account_type=" + account_type +
                ", family=" + family +
                ", familyInviters=" + familyInviters +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId() &&
                getAccount_type() == user.getAccount_type() &&
                Objects.equals(getName(), user.getName()) &&
                Objects.equals(getEmail(), user.getEmail()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getFamily(), user.getFamily()) &&
                Objects.equals(getLikedRecipes(), user.getLikedRecipes()) &&
                Objects.equals(familyInviters, user.familyInviters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getEmail(), getPassword(), getAccount_type(), getLikedRecipes(), familyInviters);
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
