package app.registrationSystem.services;

import app.registrationSystem.dto.PasswordChangeRequest;
import app.registrationSystem.dto.UpdateResponse;
import app.registrationSystem.dto.UserDTO;
import app.registrationSystem.jpa.entities.User;
import app.registrationSystem.jpa.repositories.UserRepository;
import app.registrationSystem.security.Role;
import app.registrationSystem.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationUtils validationUtils;

    @Transactional
    public Optional<User> createUser(UserDTO dto, Role role) {

        if (validationUtils.isUsernameUnavailable(dto.getUsername())) {
            return Optional.empty();
        }

        User user = new User();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(role);

        return Optional.of(userRepository.save(user));
    }

    @Transactional
    public void removeUser(User user) {
        userRepository.delete(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Changes the user's credentials
     * @param username username of the account to have the credentials changed
     * @param userDTO DTO containing new credentials
     * @return ID of the updated account if successful
     */
    @Transactional
    public UpdateResponse changeCredentials(String username, UserDTO userDTO) {
        User user = userRepository.findByUsername(username).get();

        if (Objects.nonNull(userDTO.getFirstName())) { user.setFirstName(userDTO.getFirstName()); }
        if (Objects.nonNull(userDTO.getLastName())) { user.setLastName(userDTO.getLastName()); }
        if (Objects.nonNull(userDTO.getEmail())) { user.setEmail(userDTO.getEmail()); }
        if (Objects.nonNull(userDTO.getPhoneNumber())) { user.setPhoneNumber(userDTO.getPhoneNumber()); }
        if (Objects.nonNull(userDTO.getUsername())) {
            if (validationUtils.isUsernameUnavailable(userDTO.getUsername())) { return new UpdateResponse(false, "Provided username is already taken"); }
            else { user.setUsername(userDTO.getUsername()); }
        }

        userRepository.save(user);

        return new UpdateResponse(true, "Successfully updated credentials");
    }

    @Transactional
    public UpdateResponse changePassword(String username, PasswordChangeRequest passwordChangeRequest) {
        User user = findByUsername(username).get();

        if (!passwordEncoder.matches(passwordChangeRequest.oldPassword(), user.getPassword())) {
            return new UpdateResponse(false, "Provided password doesn't match the current one");
        }

        if (passwordEncoder.matches(passwordChangeRequest.newPassword(), user.getPassword())) {
            return new UpdateResponse(false, "New password cannot be the same as the old one");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeRequest.newPassword()));
        userRepository.save(user);

        return new UpdateResponse(true, "Successfully updated the password");
    }
}
