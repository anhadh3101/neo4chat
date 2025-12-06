package com.example.cli;

import com.example.dto.EditProfileRequest;
import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.dto.UserSearchResult;
import com.example.model.User;
import com.example.service.Neo4jDriverService;
import com.example.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Neo4ChatCLI {

    private final UserService userService;
    private final Neo4jDriverService neo4jDriverService;

    private Terminal terminal;
    private LineReader reader;

    private String currentUserId;
    private String currentUsername;
    private String currentEmail;

    @Autowired
    public Neo4ChatCLI(UserService userService, Neo4jDriverService neo4jDriverService) {
        this.userService = userService;
        this.neo4jDriverService = neo4jDriverService;
    }

    public void run() throws Exception {
        terminal = TerminalBuilder.builder()
                .system(true)
                .build();
        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
        mainLoop();
    }

    private void mainLoop() {
        List<String> menuItems = buildMenuItems();

        while (true) {
            println("");
            printHeader();
            printMenu(menuItems);
            try {
                String line = reader.readLine(
                        "\nEnter option number (1-" + menuItems.size() + ") or 'q' to quit: ");
                if (line == null) {
                    continue;
                }
                line = line.trim();
                if (line.equalsIgnoreCase("q")) {
                    println("\nExiting Neo4Chat CLI. Goodbye!");
                    break;
                }
                int choice = Integer.parseInt(line);
                int index = choice - 1;
                if (index < 0 || index >= menuItems.size()) {
                    println("\nInvalid choice. Please enter a number between 1 and " + menuItems.size() + ".");
                    continue;
                }
                handleMenuSelection(index);
            } catch (NumberFormatException e) {
                println("\nInvalid input. Please enter a number.");
            } catch (UserInterruptException | EndOfFileException e) {
                println("\nExiting Neo4Chat CLI. Goodbye!");
                break;
            }
        }
    }

    private List<String> buildMenuItems() {
        List<String> items = new ArrayList<>();
        items.add("UC-1: User Registration");
        items.add("UC-2: User Login");
        items.add("UC-3: View Profile");
        items.add("UC-4: Edit Profile");
        items.add("UC-5: Follow Another User");
        items.add("UC-6: Unfollow a User");
        items.add("UC-7: View Followers / Following");
        items.add("UC-8: Mutual Connections");
        items.add("UC-9: Friend Recommendations");
        items.add("UC-10: Search Users");
        items.add("UC-11: Explore Popular Users");
        items.add("Logout");
        items.add("Exit");
        return items;
    }

    private void printHeader() {
        println("\n=======================================");
        println("  Neo4Chat CLI");
        println("=======================================");
        if (currentUserId != null) {
            println("Logged in as: " + Optional.ofNullable(currentUsername).orElse("(username unknown)")
                    + " [" + currentUserId + "]");
        } else {
            println("Not logged in.");
        }
    }

    private void printMenu(List<String> items) {
        println("\nAvailable commands:");
        for (int i = 0; i < items.size(); i++) {
            println("  " + (i + 1) + ". " + items.get(i));
        }
    }

    private void handleMenuSelection(int index) {
        switch (index) {
            case 0 -> handleRegister();
            case 1 -> handleLogin();
            case 2 -> handleViewProfile();
            case 3 -> handleEditProfile();
            case 4 -> handleFollowUser();
            case 5 -> handleUnfollowUser();
            case 6 -> handleViewConnections();
            case 7 -> handleMutualConnections();
            case 8 -> handleFriendRecommendations();
            case 9 -> handleSearchUsers();
            case 10 -> handlePopularUsers();
            case 11 -> handleLogout();
            case 12 -> {
                println("\nExiting Neo4Chat CLI. Goodbye!");
                System.exit(0);
            }
            default -> println("Unknown option.");
        }
    }

    // --- LOGIN CHECK HELPER ---
    private boolean ensureLoggedIn() {
        if (currentUserId == null) {
            println("\nYou are not logged in. Please login first.");
            return false;
        }
        return true;
    }

    // UC-1: User Registration
    private void handleRegister() {
        println("\n--- UC-1: User Registration ---");
        String name = prompt("Name: ");
        String email = prompt("Email: ");
        String username = prompt("Username: ");
        String password = prompt("Password: ");

        RegisterRequest req = new RegisterRequest();
        req.name = name;
        req.email = email;
        req.username = username;
        req.password = password;

        try {
            User user = userService.register(req);
            println("\nUser registered successfully:");
            printUser(user);
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-2: User Login
    private void handleLogin() {
        println("\n--- UC-2: User Login ---");
        String username = prompt("Username: ");
        String password = prompt("Password: ");

        LoginRequest req = new LoginRequest();
        req.username = username;
        req.password = password;

        try {
            User user = userService.login(req);
            currentUserId = user.getUserId();
            currentUsername = user.getUsername();
            currentEmail = user.getEmail();
            println("\nLogin successful.");
            printUser(user);
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-3: View Profile
    private void handleViewProfile() {
        if (!ensureLoggedIn()) return;

        println("\n--- UC-3: View Profile ---");
        String userId = ensureUserIdOrPrompt();
        if (userId == null) return;

        try {
            User user = userService.viewProfile(userId);
            println("\nProfile:");
            printUser(user);
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-4: Edit Profile
    private void handleEditProfile() {
        if (!ensureLoggedIn()) return;

        println("\n--- UC-4: Edit Profile ---");
        String userId = ensureUserIdOrPrompt();
        if (userId == null) return;

        String name = prompt("New name (leave blank to keep current): ");
        String bio = prompt("New bio (leave blank to keep current): ");

        try {
            User existing = userService.viewProfile(userId);
            EditProfileRequest req = new EditProfileRequest();
            req.name = name == null || name.isBlank() ? existing.getName() : name;
            req.bio = bio == null || bio.isBlank() ? existing.getBio() : bio;

            User updated = userService.editProfile(userId, req);
            println("\nProfile updated:");
            printUser(updated);
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-5: Follow Another User
    private void handleFollowUser() {
        if (!ensureLoggedIn()) return;

        println("\n--- UC-5: Follow Another User ---");
        println("Choose identifier type:");
        println("  1. Email");
        println("  2. User ID");
        String mode = promptWithDefault("Selection", "1");
        boolean useEmail = mode == null || !mode.trim().equals("2");

        try {
            List<User> users;
            if (useEmail) {
                String followerEmail = currentEmail != null
                        ? promptWithDefault("Your email", currentEmail)
                        : prompt("Your email: ");
                String followedEmail = prompt("Email of user to follow: ");

                if (followerEmail == null || followerEmail.isBlank()
                        || followedEmail == null || followedEmail.isBlank()) {
                    println("\nEmail(s) not provided. Aborting.");
                    return;
                }

                users = userService.followUser(followerEmail.trim(), followedEmail.trim());
            } else {
                String followerId = currentUserId != null
                        ? promptWithDefault("Your user ID", currentUserId)
                        : prompt("Your user ID: ");
                String followedId = prompt("User ID of user to follow: ");

                if (followerId == null || followerId.isBlank()
                        || followedId == null || followedId.isBlank()) {
                    println("\nUser ID(s) not provided. Aborting.");
                    return;
                }

                users = userService.followUserByUserId(followerId.trim(), followedId.trim());
            }

            User follower = users.get(0);
            User followed = users.get(1);
            println("\nFollow relationship created:");
            println("Follower:");
            printUser(follower);
            println("\nFollowed:");
            printUser(followed);
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-6: Unfollow a User
    private void handleUnfollowUser() {
        if (!ensureLoggedIn()) return;

        println("\n--- UC-6: Unfollow a User ---");
        println("Choose identifier type:");
        println("  1. Email");
        println("  2. User ID");
        String mode = promptWithDefault("Selection", "1");
        boolean useEmail = mode == null || !mode.trim().equals("2");

        try {
            List<User> users;
            if (useEmail) {
                String followerEmail = currentEmail != null
                        ? promptWithDefault("Your email", currentEmail)
                        : prompt("Your email: ");
                String followedEmail = prompt("Email of user to unfollow: ");

                if (followerEmail == null || followerEmail.isBlank()
                        || followedEmail == null || followedEmail.isBlank()) {
                    println("\nEmail(s) not provided. Aborting.");
                    return;
                }

                users = userService.unfollowUser(followerEmail.trim(), followedEmail.trim());
            } else {
                String followerId = currentUserId != null
                        ? promptWithDefault("Your user ID", currentUserId)
                        : prompt("Your user ID: ");
                String followedId = prompt("User ID of user to unfollow: ");

                if (followerId == null || followerId.isBlank()
                        || followedId == null || followedId.isBlank()) {
                    println("\nUser ID(s) not provided. Aborting.");
                    return;
                }

                users = userService.unfollowUserByUserId(followerId.trim(), followedId.trim());
            }

            User follower = users.get(0);
            User followed = users.get(1);
            println("\nUnfollowed successfully:");
            println("Follower:");
            printUser(follower);
            println("\nUnfollowed:");
            printUser(followed);
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-7: View Followers / Following
    private void handleViewConnections() {
        if (!ensureLoggedIn()) return;

        println("\n--- UC-7: View Followers / Following ---");
        println("Choose identifier type:");
        println("  1. Email");
        println("  2. User ID");
        String mode = promptWithDefault("Selection", "2");
        boolean useEmail = mode != null && mode.trim().equals("1");

        try {
            List<User> followers;
            List<User> following;

            if (useEmail) {
                String email = currentEmail != null
                        ? promptWithDefault("Your email", currentEmail)
                        : prompt("Your email: ");
                if (email == null || email.isBlank()) {
                    println("\nEmail not provided. Aborting.");
                    return;
                }
                String trimmed = email.trim();
                followers = userService.getFollowersByEmail(trimmed);
                following = userService.getFollowingByEmail(trimmed);
            } else {
                String userId = ensureUserIdOrPrompt();
                if (userId == null) return;
                followers = userService.getFollowersByUserId(userId);
                following = userService.getFollowingByUserId(userId);
            }

            println("\nFollowers (" + followers.size() + "):");
            printUserList(followers);

            println("\nFollowing (" + following.size() + "):");
            printUserList(following);
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-8: Mutual Connections
    private void handleMutualConnections() {
        if (!ensureLoggedIn()) return;

        println("\n--- UC-8: Mutual Connections ---");
        println("Choose identifier type:");
        println("  1. Email");
        println("  2. User ID");
        String mode = promptWithDefault("Selection", "2");
        boolean useEmail = mode != null && mode.trim().equals("1");

        try {
            List<User> mutual;

            if (useEmail) {
                String email1 = currentEmail != null
                        ? promptWithDefault("Your email", currentEmail)
                        : prompt("Your email: ");
                String email2 = prompt("Other user's email: ");

                if (email1 == null || email1.isBlank()
                        || email2 == null || email2.isBlank()) {
                    println("\nEmail(s) not provided. Aborting.");
                    return;
                }

                mutual = userService.getMutualConnectionsByEmail(email1.trim(), email2.trim());
            } else {
                String userId = ensureUserIdOrPrompt();
                if (userId == null) return;
                String otherUserId = prompt("Other user's ID: ");
                if (otherUserId == null || otherUserId.isBlank()) {
                    println("\nOther user ID not provided. Aborting.");
                    return;
                }

                mutual = userService.getMutualConnectionsByUserId(userId, otherUserId.trim());
            }

            println("\nMutual connections (" + mutual.size() + "):");
            printUserList(mutual);
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-9: Friend Recommendations
    private void handleFriendRecommendations() {
        if (!ensureLoggedIn()) return;

        println("\n--- UC-9: Friend Recommendations ---");
        String userId = ensureUserIdOrPrompt();
        if (userId == null) return;

        try {
            List<UserSearchResult> recs = neo4jDriverService.getFriendRecommendations(userId);
            println("\nRecommendations (" + recs.size() + "):");
            printSearchResults(recs);
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-10: Search Users
    private void handleSearchUsers() {
        if (!ensureLoggedIn()) return;

        println("\n--- UC-10: Search Users ---");
        String term = prompt("Search term (name or username): ");
        String limitStr = promptWithDefault("Limit", "10");
        String offsetStr = promptWithDefault("Offset", "0");

        try {
            int limit = Integer.parseInt(limitStr.trim());
            int offset = Integer.parseInt(offsetStr.trim());
            List<UserSearchResult> results = neo4jDriverService.searchUsers(term, limit, offset);
            println("\nSearch results (" + results.size() + "):");
            printSearchResults(results);
        } catch (NumberFormatException e) {
            println("\nError: Limit and offset must be integers.");
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-11: Explore Popular Users
    private void handlePopularUsers() {
        if (!ensureLoggedIn()) return;

        println("\n--- UC-11: Explore Popular Users ---");
        String userId = ensureUserIdOrPrompt();
        if (userId == null) return;
        String limitStr = promptWithDefault("Limit", "10");

        try {
            int limit = Integer.parseInt(limitStr.trim());
            List<UserSearchResult> results = neo4jDriverService.getPopularUsers(userId, limit);
            println("\nPopular users (" + results.size() + "):");
            printSearchResults(results);
        } catch (NumberFormatException e) {
            println("\nError: Limit must be an integer.");
        } catch (RuntimeException e) {
            println("\nError: " + e.getMessage());
        }
    }

    // UC-12: Logout
    private void handleLogout() {
        if (!ensureLoggedIn()) {
            println("\nYou are not logged in.");
            return;
        }
        currentUserId = null;
        currentUsername = null;
        currentEmail = null;
        println("\nLogged out.");
    }

    // --- HELPER METHODS ---
    private String ensureUserIdOrPrompt() {
        if (currentUserId != null) {
            String useCurrent = promptWithDefault("Use current logged-in user ID", currentUserId);
            if (useCurrent != null && !useCurrent.isBlank() && useCurrent.equals(currentUserId)) {
                return currentUserId;
            }
        }
        String id = prompt("Enter user ID: ");
        if (id == null || id.isBlank()) {
            println("No user ID provided.");
            return null;
        }
        return id.trim();
    }

    private void printUser(User user) {
        if (user == null) {
            println("(null user)");
            return;
        }
        println("---------------------------------------");
        println("UserId   : " + user.getUserId());
        println("Name     : " + valueOrDash(user.getName()));
        println("Username : " + valueOrDash(user.getUsername()));
        println("Email    : " + valueOrDash(user.getEmail()));
        println("Bio      : " + valueOrDash(user.getBio()));
        println("City     : " + valueOrDash(user.getCity()));
        println("Country  : " + valueOrDash(user.getCountry()));
        println("Gender   : " + valueOrDash(user.getGender()));
        println("Interest : " + valueOrDash(user.getInterest()));
        println("FollowCt : " + (user.getFollowCount() == null ? "-" : user.getFollowCount()));
        println("---------------------------------------");
    }

    private void printUserList(List<User> users) {
        if (users == null || users.isEmpty()) {
            println("(none)");
            return;
        }
        int index = 1;
        for (User u : users) {
            println(index++ + ") " + formatUserSummary(u));
        }
    }

    private String formatUserSummary(User user) {
        return "[" + valueOrDash(user.getUserId()) + "] "
                + valueOrDash(user.getName())
                + " (@" + valueOrDash(user.getUsername()) + ")"
                + " - " + valueOrDash(user.getBio());
    }

    private void printSearchResults(List<UserSearchResult> results) {
        if (results == null || results.isEmpty()) {
            println("(no results)");
            return;
        }
        println(String.format("%-5s %-36s %-20s %-20s %-5s", "#", "UserId", "Name", "Username", "Follows"));
        println("--------------------------------------------------------------------------");
        int idx = 1;
        for (UserSearchResult r : results) {
            String id = r.getUserId() == null ? "-" : r.getUserId();
            String name = r.getName() == null ? "-" : r.getName();
            String username = r.getUsername() == null ? "-" : r.getUsername();
            String follows = r.getFollowCount() == null ? "-" : r.getFollowCount().toString();
            println(String.format("%-5d %-36s %-20s %-20s %-5s", idx++, id, name, username, follows));
            if (r.getBio() != null && !r.getBio().isBlank()) {
                println("      Bio: " + r.getBio());
            }
        }
    }

    private String prompt(String label) {
        try {
            return reader.readLine(label);
        } catch (UserInterruptException | EndOfFileException e) {
            return null;
        }
    }

    private String promptWithDefault(String label, String defaultValue) {
        String line = prompt(label + " [" + defaultValue + "]: ");
        if (line == null || line.isBlank()) {
            return defaultValue;
        }
        return line;
    }

    private void println(String s) {
        try {
            terminal.writer().println(s);
            terminal.writer().flush();
        } catch (Exception ignored) {
        }
    }

    private String valueOrDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
