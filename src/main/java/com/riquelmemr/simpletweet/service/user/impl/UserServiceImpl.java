package com.riquelmemr.simpletweet.service.user.impl;

import com.riquelmemr.simpletweet.dto.request.UpdateUserRequest;
import com.riquelmemr.simpletweet.enums.RoleEnum;
import com.riquelmemr.simpletweet.exceptions.EntityAlreadyExistsException;
import com.riquelmemr.simpletweet.exceptions.EntityNotFoundException;
import com.riquelmemr.simpletweet.exceptions.ResourceNotAllowedException;
import com.riquelmemr.simpletweet.model.Role;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.repository.RoleRepository;
import com.riquelmemr.simpletweet.repository.UserRepository;
import com.riquelmemr.simpletweet.security.JwtUtils;
import com.riquelmemr.simpletweet.service.user.UserService;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public void create(User user) {
        User userAlreadyExists = findByUsername(user.getUsername());
        Role basicRole = roleRepository.findByName(RoleEnum.BASIC.name().toUpperCase());

        if (Objects.nonNull(userAlreadyExists)) {
            log.warn("User [{}] already exists", user.getUsername());
            throw new EntityAlreadyExistsException("User already exists by username.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(basicRole));
        userRepository.save(user);

        log.info("User [{}] created with id [{}]", user.getUsername(), user.getPk());
    }

    @Override
    @Transactional
    public User update(Long ownerId, UpdateUserRequest request, JwtAuthenticationToken token) {
        User requester = extractUserFromToken(token);
        User owner = findById(ownerId);

        boolean isAdmin = requester.getRoles()
                .stream()
                .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN.name()));

        boolean isOwner = owner.getPk().equals(requester.getPk());

        if (!isOwner && !isAdmin) {
            log.warn("User [{}] is not allowed to update user [{}]", requester.getPk(), ownerId);
            throw new ResourceNotAllowedException("You do not have permission to update this user.");
        }

        updateUserData(request, owner);
        userRepository.save(owner);

        log.info("User [{}] updated by user [{}]", ownerId, requester.getPk());

        return owner;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id [" + id + "]"));
    }

    @Override
    public User extractUserFromToken(JwtAuthenticationToken token) {
        String userId = jwtUtils.getIdByToken(token);
        return findById(Long.parseLong(userId));
    }

    private void updateUserData(UpdateUserRequest request, User owner) {
        if (Objects.nonNull(request.name())) {
            owner.setName(request.name());
        }

        if (Objects.nonNull(request.username())) {
            owner.setUsername(request.username());
        }

        if (Objects.nonNull(request.email())) {
            owner.setEmail(request.email());
        }

        if (Objects.nonNull(request.bio())) {
            owner.setBio(request.bio());
        }
    }
}
