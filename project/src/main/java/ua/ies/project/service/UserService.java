package ua.ies.project.service;

import ua.ies.project.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}
