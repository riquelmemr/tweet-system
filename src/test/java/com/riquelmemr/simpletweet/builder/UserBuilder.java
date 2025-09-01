package com.riquelmemr.simpletweet.builder;

import com.riquelmemr.simpletweet.model.Role;
import com.riquelmemr.simpletweet.model.User;

import java.util.Set;

public class UserBuilder {
    private final User user;

    public UserBuilder() {
        user = new User();
    }

    public UserBuilder withPk(Long pk) {
        user.setPk(pk);
        return this;
    }

    public UserBuilder withUsername(String username) {
        user.setUsername(username);
        return this;
    }

    public UserBuilder withEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder withName(String name) {
        user.setName(name);
        return this;
    }

    public UserBuilder withPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public UserBuilder withRoles(Set<Role> roles) {
        user.setRoles(roles);
        return this;
    }

    public UserBuilder random() {
        return this.withPk(1L)
                .withEmail("test@test.com.br")
                .withUsername("test")
                .withName("Test Name")
                .withPassword("password-test");
    }

    public User build() {
        return user;
    }
}
