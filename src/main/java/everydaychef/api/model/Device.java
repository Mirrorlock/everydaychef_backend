package everydaychef.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String firebaseToken;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;

    public Device() {
    }

    public Device(User newUser, String token){
        user = newUser;
        firebaseToken = token;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Id == device.Id &&
                Objects.equals(firebaseToken, device.firebaseToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, firebaseToken);
    }

    @Override
    public String toString() {
        return "Device{" +
                "Id=" + Id +
                ", firebaseToken='" + firebaseToken + '\'' +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
