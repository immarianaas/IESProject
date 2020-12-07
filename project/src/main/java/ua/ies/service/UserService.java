package ua.ies.service;

import ua.ies.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}
