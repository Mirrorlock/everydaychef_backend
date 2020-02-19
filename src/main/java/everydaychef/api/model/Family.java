package everydaychef.api.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "families")
public class Family {

    public Family() {
    }

    public Family(String name){
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "family")
    private Set<User> users;

//    public Set<User> getInvitedUsers() {
//        return invitedUsers;
//    }
//
//    public void setInvitedUsers(Set<User> invitedUsers) {
//        this.invitedUsers = invitedUsers;
//    }
//
//    @ManyToMany(mappedBy = "familyInviters")
//    @JsonBackReference
//    private Set<User> invitedUsers;

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

    //
//    public Set<User> getInvitedUsers() {
//        return invitedUsers;
//    }
//
//    public void setInvitedUsers(Set<User> invitedUsers) {
//        this.invitedUsers = invitedUsers;
//    }
}
