package net.switchscope.repository.security;

import net.switchscope.model.security.PermissionEntity;
import net.switchscope.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface PermissionRepository extends BaseRepository<PermissionEntity> {

    Optional<PermissionEntity> findByCode(String code);

    @Query("SELECT p.code FROM PermissionEntity p")
    List<String> findAllCodes();
}
