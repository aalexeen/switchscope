package net.switchscope.repository.security;

import net.switchscope.model.security.RoleEntity;
import net.switchscope.repository.BaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface RoleRepository extends BaseRepository<RoleEntity> {

    Optional<RoleEntity> findByCode(String code);

    @EntityGraph(attributePaths = "permissions")
    @Query("SELECT r FROM RoleEntity r ORDER BY r.sortOrder, r.code")
    List<RoleEntity> findAllWithPermissions();
}
