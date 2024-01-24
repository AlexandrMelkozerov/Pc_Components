package ru.melkozerovau.service;



import ru.melkozerovau.dto.UserDto;
import ru.melkozerovau.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByUsername(String username);

    List<UserDto> findAllUsers();

    void addRoleToUser(String username, String roleName);

}
