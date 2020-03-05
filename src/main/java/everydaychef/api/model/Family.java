package everydaychef.api.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "families")
public class Family {

    public Family() {
    }

    public Family(String name){
        this.name = name;
        users = new HashSet<>();
        invitedUsers = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "family")
    private Set<User> users;

    @ManyToMany(mappedBy = "familyInviters")
    @JsonBackReference
    private Set<User> invitedUsers;

    @ManyToMany
    @JoinTable(name="family_ingredients", joinColumns = @JoinColumn(name = "family_id"),
            inverseJoinColumns = @JoinColumn(name="ingredient_id"))
    @JsonBackReference
    private List<Ingredient> ingredients;

    @OneToMany(mappedBy = "creatorFamily")
    @JsonBackReference
    private List<ShoppingList> shoppingLists;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<User> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(Set<User> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    @Override
    public String toString() {
        return "Family{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Family)) return false;
        Family family = (Family) o;
        return getId() == family.getId() &&
                getName().equals(family.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    //
//    public Set<User> getInvitedUsers() {
//        return invitedUsers;
//    }
//
//    public void setInvitedUsers(Set<User> invitedUsers) {
//        this.invitedUsers = invitedUsers;
//    }
}
