package everydaychef.api.repository;

import everydaychef.api.model.Family;
import everydaychef.api.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Integer> {
    Optional<ShoppingList> findAllByCreatorFamily(Family creatorFamily);
}
