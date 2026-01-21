package dit.hua.gr.greenride.web.rest.api;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.AdminService;
import dit.hua.gr.greenride.web.ui.AdminStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Admin", description = "Admin dashboard and moderation endpoints")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {

    private final AdminService adminService;
    private final PersonRepository personRepository;

    public AdminRestController(AdminService adminService, PersonRepository personRepository) {
        this.adminService = adminService;
        this.personRepository = personRepository;
    }

    public record AdminUserView(
            Long id,
            String email,
            String fullName,
            String personType,
            boolean banned
    ) {}

    private AdminUserView toView(Person p) {
        if (p == null) return null;
        return new AdminUserView(
                p.getId(),
                safe(p.getEmailAddress()),
                safe(p.getFullName()),
                p.getPersonType() != null ? p.getPersonType().name() : null,
                p.isBanned()
        );
    }

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    public record AdminDashboardResponse(
            AdminStats stats,
            List<String> flaggedUsers,
            List<AdminUserView> allUsers,
            List<String> kickedUserNames
    ) {}

    @GetMapping("/dashboard")
    public AdminDashboardResponse dashboard() {
        AdminStats stats = adminService.getSystemStatistics();
        List<String> flaggedUsers = adminService.getFlaggedUsers();

        List<AdminUserView> allUsers = adminService.getAllUsersExcludingAdmins()
                .stream()
                .map(this::toView)
                .toList();

        List<String> kickedUserNames = adminService.getKickedUserNames();

        return new AdminDashboardResponse(stats, flaggedUsers, allUsers, kickedUserNames);
    }

    @Operation(summary = "Ban a user (soft-kick)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User banned"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Transactional
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void kickUser(@PathVariable Long id) {

        Person user = personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        user.setBanned(true);
        adminService.logKickedUser(user.getFullName());
        personRepository.save(user);
    }

    @GetMapping("/users")
    public List<AdminUserView> allUsers() {
        return adminService.getAllUsersExcludingAdmins()
                .stream()
                .map(this::toView)
                .toList();
    }

    @GetMapping("/flagged")
    public List<String> flaggedUsers() {
        return adminService.getFlaggedUsers();
    }

    @GetMapping("/kicked")
    public List<String> kickedUsers() {
        return adminService.getKickedUserNames();
    }
}