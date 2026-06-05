package com.chaty.controller;

import com.chaty.dto.UserDto;
import com.chaty.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(new UserDto(user.getId(), user.getName())))
                .orElse(ResponseEntity.notFound().build());
    }
}
