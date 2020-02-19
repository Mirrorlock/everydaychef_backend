package everydaychef.api.repository;

import everydaychef.api.model.Family;
import everydaychef.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Integer> {
}
