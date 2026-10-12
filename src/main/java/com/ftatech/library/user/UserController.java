package com.ftatech.library.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        @Operation(summary = "List users")
        @ApiResponse(responseCode = "200", description = "Users found")
        @GetMapping
        public List<User> findAll() {
                return userService.findAll();
        }

        @Operation(summary = "Find a user by ID")
        @ApiResponse(responseCode = "200", description = "User found")
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        @GetMapping("/{id}")
        public User findById(@PathVariable Long id) {
                return userService.findById(id);
        }

        @Operation(summary = "Create a user")
        @ApiResponse(responseCode = "201", description = "User created")
        @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        @ApiResponse(responseCode = "409", description = "Email already registered", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public User create(@RequestBody User user) {
                return userService.create(user);
        }

        @Operation(summary = "Update a user")
        @ApiResponse(responseCode = "200", description = "User updated")
        @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        @ApiResponse(responseCode = "409", description = "Email already registered", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        @PutMapping("/{id}")
        public User update(@PathVariable Long id, @RequestBody User user) {
                return userService.update(id, user);
        }

        @Operation(summary = "Delete a user")
        @ApiResponse(responseCode = "204", description = "User deleted")
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable Long id) {
                userService.delete(id);
        }
}
