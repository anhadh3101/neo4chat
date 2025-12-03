package com.example.service;

import com.example.dto.UserSearchResult;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Example service demonstrating how to use the Neo4j Driver
 * for custom Cypher queries throughout your application.
 */
@Service
public class Neo4jDriverService {

    private final Driver driver;

    @Autowired
    public Neo4jDriverService(Driver driver) {
        this.driver = driver;
    }

    /**
     * Example method: Execute a simple read query
     */
    public long countNodes(String label) {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (n:" + label + ") RETURN count(n) as count");
            return result.single().get("count").asLong();
        }
    }

    /**
     * Example method: Fetch the first 10 Users from the database
     * Returns all properties of User nodes (userId, name, username, email,
     * password, bio,
     * city, country, dob, gender, interest, followCount)
     */
    public java.util.List<java.util.Map<String, Object>> getFirst10Users() {
        try (Session session = driver.session()) {
            // Use properties() to get all properties dynamically, supporting both schemas
            Result result = session.run(
                    "MATCH (u:User) RETURN properties(u) as userProps LIMIT 10");

            java.util.List<java.util.Map<String, Object>> users = new java.util.ArrayList<>();
            while (result.hasNext()) {
                var record = result.next();
                // Get all properties as a map from Neo4j
                var neo4jValue = record.get("userProps");
                java.util.Map<String, Object> user = new java.util.HashMap<>();

                if (!neo4jValue.isNull()) {
                    // Convert Neo4j Value map to Java Map, handling all property types
                    var neo4jMap = neo4jValue.asMap(org.neo4j.driver.Value::asObject);
                    user.putAll(neo4jMap);
                }
                users.add(user);
            }
            return users;
        }
    }

    /**
     * Example method: Execute a custom query with parameters
     */
    public Result executeQuery(String cypher, Map<String, Object> parameters) {
        Session session = driver.session();
        return session.run(cypher, parameters);
    }

    /**
     * Example method: Execute a write transaction
     */
    public void executeWriteTransaction(String cypher, Map<String, Object> parameters) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run(cypher, parameters);
                return null;
            });
        }
    }

    /**
     * Search for users by name or username (case-insensitive partial match)
     * 
     * @param searchTerm The search term to match against name or username
     * @return List of users matching the search term (userId, name, username, and
     *         bio)
     */
    public java.util.List<UserSearchResult> searchUsers(String searchTerm) {
        try (Session session = driver.session()) {
            String cypher = "MATCH (u:User) " +
                    "WHERE (u.name IS NOT NULL AND toLower(u.name) CONTAINS toLower($searchTerm)) " +
                    "OR (u.username IS NOT NULL AND toLower(u.username) CONTAINS toLower($searchTerm)) " +
                    "RETURN toString(u.userId) as userId, u.name as name, u.username as username, u.bio as bio";

            Result result = session.run(cypher, Map.of("searchTerm", searchTerm));

            java.util.List<UserSearchResult> users = new java.util.ArrayList<>();
            while (result.hasNext()) {
                var record = result.next();
                UserSearchResult user = new UserSearchResult();

                if (!record.get("userId").isNull()) {
                    user.setUserId(record.get("userId").asString());
                }
                if (!record.get("name").isNull()) {
                    user.setName(record.get("name").asString());
                }
                if (!record.get("username").isNull()) {
                    user.setUsername(record.get("username").asString());
                }
                if (!record.get("bio").isNull()) {
                    user.setBio(record.get("bio").asString());
                }
                users.add(user);
            }
            return users;
        }
    }

    /**
     * Get the most-followed users ordered by follower count
     * 
     * @param limit Maximum number of users to return (default: 10)
     * @return List of users ordered by followCount descending (userId, name,
     *         username, bio, followCount)
     */
    public java.util.List<UserSearchResult> getPopularUsers(int limit) {
        try (Session session = driver.session()) {
            String cypher = "MATCH (u:User) " +
                    "WHERE u.followCount IS NOT NULL AND u.followCount > 0 " +
                    "RETURN toString(u.userId) as userId, u.name as name, u.username as username, u.bio as bio, toInteger(u.followCount) as followCount "
                    +
                    "ORDER BY u.followCount DESC " +
                    "LIMIT $limit";

            Result result = session.run(cypher, Map.of("limit", limit));

            java.util.List<UserSearchResult> users = new java.util.ArrayList<>();
            while (result.hasNext()) {
                var record = result.next();
                UserSearchResult user = new UserSearchResult();

                if (!record.get("userId").isNull()) {
                    user.setUserId(record.get("userId").asString());
                }
                if (!record.get("name").isNull()) {
                    user.setName(record.get("name").asString());
                }
                if (!record.get("username").isNull()) {
                    user.setUsername(record.get("username").asString());
                }
                if (!record.get("bio").isNull()) {
                    user.setBio(record.get("bio").asString());
                }
                if (!record.get("followCount").isNull()) {
                    user.setFollowCount(record.get("followCount").asInt());
                }
                users.add(user);
            }
            return users;
        }
    }
}
