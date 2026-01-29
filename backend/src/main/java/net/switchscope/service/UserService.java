package net.switchscope.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import net.switchscope.error.DataConflictException;
import net.switchscope.mapper.UserMapper;
import net.switchscope.model.User;
import net.switchscope.repository.UserRepository;
import net.switchscope.to.UserTo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public List<User> getAll() {
        return repository.findAll();
    }

    public User getById(UUID id) {
        return repository.getExisted(id);
    }

    public User getByEmail(String email) {
        return repository.getExistedByEmail(email);
    }

    @Transactional
    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        Assert.isNull(user.getId(), "user must be new (id must be null)");
        checkEmailUnique(user);
        return repository.prepareAndSave(user);
    }

    @Transactional
    public User update(UUID id, User user) {
        Assert.notNull(user, "user must not be null");
        User existing = repository.getExisted(id);
        
        // Check email uniqueness if changed
        if (!existing.getEmail().equalsIgnoreCase(user.getEmail())) {
            checkEmailUnique(user);
        }
        
        // Update fields
        existing.setName(user.getName());
        existing.setEmail(user.getEmail().toLowerCase());
        existing.setEnabled(user.isEnabled());
        
        // Update password only if provided (not empty)
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(user.getPassword());
            return repository.prepareAndSave(existing);
        }
        
        return repository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteExisted(id);
    }

    @Transactional
    public void enable(UUID id, boolean enabled) {
        User user = repository.getExisted(id);
        user.setEnabled(enabled);
        repository.save(user);
    }

    /**
     * Update user profile from DTO.
     * Used by ProfileController for self-update.
     *
     * @param user existing user entity (from AuthUser)
     * @param userTo DTO with updated values
     * @return updated user
     */
    @Transactional
    public User updateFromTo(User user, UserTo userTo) {
        Assert.notNull(user, "user must not be null");
        Assert.notNull(userTo, "userTo must not be null");

        // Check email uniqueness if changed
        if (!user.getEmail().equalsIgnoreCase(userTo.getEmail())) {
            checkEmailUnique(userTo.getEmail(), user.getId());
        }

        mapper.updateFromTo(user, userTo);
        return repository.prepareAndSave(user);
    }

    private void checkEmailUnique(User user) {
        repository.findByEmailIgnoreCase(user.getEmail())
                .ifPresent(existing -> {
                    throw new DataConflictException("User with email '" + user.getEmail() + "' already exists");
                });
    }

    private void checkEmailUnique(String email, UUID excludeId) {
        repository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(excludeId))
                .ifPresent(existing -> {
                    throw new DataConflictException("User with email '" + email + "' already exists");
                });
    }
}
