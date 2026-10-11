package com.ftatech.library.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() { return userRepository.findAll(); }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User create(User user) {
        validate(user);
        ensureEmailIsAvailable(user.getEmail());
        return userRepository.save(user);
    }

    public User update(Long id, User input) {
        validate(input);
        User user = findById(id);
        ensureEmailIsAvailable(input.getEmail(), id);
        user.update(input.getName(), input.getEmail(), input.getType());
        return userRepository.save(user);
    }

    public void delete(Long id) { userRepository.delete(findById(id)); }

    private void validate(User user) {
        if (user == null || user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank() || user.getType() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name, email and type are required");
        }
    }

    private void ensureEmailIsAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }
    }

    private void ensureEmailIsAvailable(String email, Long id) {
        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }
    }
}
