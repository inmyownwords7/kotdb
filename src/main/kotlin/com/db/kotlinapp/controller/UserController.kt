package com.db.kotlinapp.controller

import com.db.kotlinapp.dto.UserDTO
import com.db.kotlinapp.enums.Role
import com.db.kotlinapp.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class AdminController(private val userService: UserService) {

    @PutMapping("/change-role/{username}")
    @PreAuthorize("hasRole('ADMIN')") // ✅ Only admins can change roles
    fun changeUserRole(
        @PathVariable username: String,
        @RequestParam newRole: Role
    ): UserDTO {
        return userService.changeUserRole(username, newRole)
    }
}
