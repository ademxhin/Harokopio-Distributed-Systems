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
            String personType
    ) {}

    private AdminUserView toView(Person p) {
        String email = safe(p.getEmailAddress());
        String fullName = safe(p.getFullName());
        String personType = (p.getPersonType() != null) ? p.getPersonType().name() : null;

        return new AdminUserView(
                p.getId(),
                email,
                fullName,
                personType
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

    @Operation(summary = "Admin dashboard data (stats + users)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard payload returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not ADMIN)")
    })
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

    @Operation(summary = "Kick a user (delete user and log kicked user full name)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User kicked (deleted)"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not ADMIN)")
    })
    @Transactional
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void kickUser(@PathVariable Long id) {

        Person user = personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        adminService.logKickedUser(user.getFullName());
        personRepository.delete(user);
    }

    @Operation(summary = "Get all non-admin users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not ADMIN)")
    })
    @GetMapping("/users")
    public List<AdminUserView> allUsers() {
        return adminService.getAllUsersExcludingAdmins()
                .stream()
                .map(this::toView)
                .toList();
    }

    @Operation(summary = "Get flagged users (as names/identifiers)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flagged users returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not ADMIN)")
    })
    @GetMapping("/flagged")
    public List<String> flaggedUsers() {
        return adminService.getFlaggedUsers();
    }

    @Operation(summary = "Get kicked user names (audit log)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kicked user names returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not ADMIN)")
    })
    @GetMapping("/kicked")
    public List<String> kickedUsers() {
        return adminService.getKickedUserNames();
    }
}