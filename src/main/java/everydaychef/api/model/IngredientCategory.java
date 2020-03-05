package everydaychef.api.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="ingredient_categories")
public class IngredientCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String name;
    private String pictureUrl;



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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredientCategory that = (IngredientCategory) o;
        return Id == that.Id &&
                Objects.equals(name, that.name) &&
                Objects.equals(pictureUrl, that.pictureUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, name, pictureUrl);
    }

    @Override
    public String toString() {
        return "IngredientCategory{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                '}';
    }
}
