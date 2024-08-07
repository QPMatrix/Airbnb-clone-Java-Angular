package tech.qpmatrix.airbnbclone.user.application;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qpmatrix.airbnbclone.infrastructure.config.SecurityUtils;
import tech.qpmatrix.airbnbclone.user.application.dto.ReadUserDTO;
import tech.qpmatrix.airbnbclone.user.domain.User;
import tech.qpmatrix.airbnbclone.user.mapper.UserMapper;
import tech.qpmatrix.airbnbclone.user.repository.UserRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private static final String UPDATE_AT_KEY = "updated_at";
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public ReadUserDTO getAuthenticatedUser() {
        OAuth2User principal = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = SecurityUtils.mapOauth2AttributesToUser(principal.getAttributes());
        return getByEmail(user.getEmail()).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Optional<ReadUserDTO> getByEmail(String email) {
        Optional<User> oneByEmail = userRepository.findByEmail(email);
        return oneByEmail.map(userMapper::readUserDTOToUser);
    }

    public void syncWithIdp(OAuth2User oAuth2User, boolean forceReSync) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        User user = SecurityUtils.mapOauth2AttributesToUser(attributes);
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            if (attributes.get(UPDATE_AT_KEY) != null) {
                Instant lastModifiedDate = existingUser.orElseThrow().getLastModifiedDate();
                Instant idpModifiedDate;
                if (attributes.get(UPDATE_AT_KEY) instanceof Instant instant) {
                    idpModifiedDate = instant;
                } else {
                    idpModifiedDate = Instant.ofEpochSecond((Integer) attributes.get(UPDATE_AT_KEY));
                }
                if (idpModifiedDate.isAfter(lastModifiedDate) || forceReSync) {
                    updateUser(user);
                }
            }
        } else {
            userRepository.save(user);
        }
    }

    private void updateUser(User user) {
        Optional<User> userTopUpdateOpt = userRepository.findByEmail(user.getEmail());
        if (userTopUpdateOpt.isPresent()) {
            User userToUpdate = userTopUpdateOpt.get();
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setAuthorities(user.getAuthorities());
            userToUpdate.setImageUrl(user.getImageUrl());
            userRepository.saveAndFlush(userToUpdate);
        }
    }

    public Optional<ReadUserDTO> getByPublicId(UUID publicId) {
        Optional<User> userByPublicId = userRepository.findByPublicId(publicId);
        return userByPublicId.map(userMapper::readUserDTOToUser);
    }
}
