package pm.s32237.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pm.s32237.model.Lambda;

@Repository
public interface LambdaRepo extends JpaRepository<Lambda, String> {
}
