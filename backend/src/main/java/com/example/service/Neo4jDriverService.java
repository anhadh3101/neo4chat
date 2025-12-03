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
     * @param limit      Maximum number of results to return (default: 10)
     * @param offset     Number of results to skip for pagination (default: 0)
     * @return List of users matching the search term (userId, name, username, and
     *         bio)
     */
    public java.util.List<UserSearchResult> searchUsers(String searchTerm, int limit, int offset) {
        try (Session session = driver.session()) {
            String cypher = "MATCH (u:User) " +
                    "WHERE (u.name IS NOT NULL AND toLower(u.name) CONTAINS toLower($searchTerm)) " +
                    "OR (u.username IS NOT NULL AND toLower(u.username) CONTAINS toLower($searchTerm)) " +
                    "RETURN toString(u.userId) as userId, u.name as name, u.username as username, u.bio as bio " +
                    "SKIP $offset LIMIT $limit";

            Result result = session.run(cypher, Map.of(
                    "searchTerm", searchTerm,
                    "limit", limit,
                    "offset", offset));

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
     * Excludes users that the current user is already following
     * 
     * @param userId The userId of the current user (as string)
     * @param limit  Maximum number of users to return (default: 10)
     * @return List of users ordered by followCount descending (userId, name,
     *         username, bio, followCount)
     */
    public java.util.List<UserSearchResult> getPopularUsers(String userId, int limit) {
        try (Session session = driver.session()) {
            String cypher = "MATCH (current:User), (u:User) " +
                    "WHERE toString(current.userId) = $userId " +
                    "AND current <> u " +
                    "AND u.followCount IS NOT NULL AND u.followCount > 0 " +
                    "AND NOT (current)-[:FOLLOWS]->(u) " +
                    "RETURN toString(u.userId) as userId, u.name as name, u.username as username, u.bio as bio, toInteger(u.followCount) as followCount "
                    +
                    "ORDER BY u.followCount DESC " +
                    "LIMIT $limit";

            Result result = session.run(cypher, Map.of(
                    "userId", userId,
                    "limit", limit));

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

    /**
     * Get friend recommendations based on common connections
     * Finds users who are followed by people that the current user follows
     * Falls back to popular users if no common connections are found
     * 
     * @param userId The userId of the user requesting recommendations (as string)
     * @return List of up to 10 recommended users (userId, name, username, bio)
     */
    public java.util.List<UserSearchResult> getFriendRecommendations(String userId) {
        try (Session session = driver.session()) {
            // First, try to find recommendations based on common connections
            String cypher = "MATCH (current:User)-[:FOLLOWS]->(friend:User)-[:FOLLOWS]->(recommended:User) " +
                    "WHERE toString(current.userId) = $userId " +
                    "AND current <> recommended " +
                    "AND NOT (current)-[:FOLLOWS]->(recommended) " +
                    "RETURN toString(recommended.userId) as userId, recommended.name as name, recommended.username as username, recommended.bio as bio, toInteger(recommended.followCount) as followCount, count(friend) as commonConnections "
                    +
                    "ORDER BY commonConnections DESC " +
                    "LIMIT 10";

            Result result = session.run(cypher, Map.of("userId", userId));

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

            // If no recommendations found based on common connections, fall back to popular
            // users
            if (users.isEmpty()) {
                String fallbackCypher = "MATCH (current:User), (recommended:User) " +
                        "WHERE toString(current.userId) = $userId " +
                        "AND current <> recommended " +
                        "AND NOT (current)-[:FOLLOWS]->(recommended) " +
                        "AND recommended.followCount IS NOT NULL AND recommended.followCount > 0 " +
                        "RETURN toString(recommended.userId) as userId, recommended.name as name, recommended.username as username, recommended.bio as bio, toInteger(recommended.followCount) as followCount "
                        +
                        "ORDER BY recommended.followCount DESC " +
                        "LIMIT 10";

                Result fallbackResult = session.run(fallbackCypher, Map.of("userId", userId));
                while (fallbackResult.hasNext()) {
                    var record = fallbackResult.next();
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
            }

            return users;
        }
    }

    /**
     * Diagnostic method to check user relationships and graph structure
     * 
     * @param userId The userId to diagnose (as string)
     * @return Map containing diagnostic information about the user's relationships
     */
    public java.util.Map<String, Object> diagnoseUserRelationships(String userId) {
        try (Session session = driver.session()) {
            java.util.Map<String, Object> diagnostics = new java.util.HashMap<>();

            // Check if user exists
            String userExistsCypher = "MATCH (u:User) WHERE toString(u.userId) = $userId RETURN toString(u.userId) as userId, u.name as name, u.username as username";
            Result userResult = session.run(userExistsCypher, Map.of("userId", userId));
            if (!userResult.hasNext()) {
                diagnostics.put("error", "User not found");
                return diagnostics;
            }
            var userRecord = userResult.next();
            diagnostics.put("user", Map.of(
                    "userId", userRecord.get("userId").asString(),
                    "name", userRecord.get("name").isNull() ? "null" : userRecord.get("name").asString(),
                    "username", userRecord.get("username").isNull() ? "null" : userRecord.get("username").asString()));

            // Get all outgoing relationships from this user
            String outgoingCypher = "MATCH (u:User)-[r]->(other:User) " +
                    "WHERE toString(u.userId) = $userId " +
                    "RETURN type(r) as relationshipType, toString(other.userId) as otherUserId, other.name as otherName, other.username as otherUsername "
                    +
                    "ORDER BY type(r), other.userId";
            Result outgoingResult = session.run(outgoingCypher, Map.of("userId", userId));
            java.util.List<java.util.Map<String, Object>> outgoing = new java.util.ArrayList<>();
            while (outgoingResult.hasNext()) {
                var record = outgoingResult.next();
                java.util.Map<String, Object> rel = new java.util.HashMap<>();
                rel.put("relationshipType", record.get("relationshipType").asString());
                rel.put("otherUserId", record.get("otherUserId").asString());
                rel.put("otherName", record.get("otherName").isNull() ? null : record.get("otherName").asString());
                rel.put("otherUsername",
                        record.get("otherUsername").isNull() ? null : record.get("otherUsername").asString());
                outgoing.add(rel);
            }
            diagnostics.put("outgoingRelationships", outgoing);
            diagnostics.put("outgoingCount", outgoing.size());

            // Get all incoming relationships to this user
            String incomingCypher = "MATCH (other:User)-[r]->(u:User) " +
                    "WHERE toString(u.userId) = $userId " +
                    "RETURN type(r) as relationshipType, toString(other.userId) as otherUserId, other.name as otherName, other.username as otherUsername "
                    +
                    "ORDER BY type(r), other.userId";
            Result incomingResult = session.run(incomingCypher, Map.of("userId", userId));
            java.util.List<java.util.Map<String, Object>> incoming = new java.util.ArrayList<>();
            while (incomingResult.hasNext()) {
                var record = incomingResult.next();
                java.util.Map<String, Object> rel = new java.util.HashMap<>();
                rel.put("relationshipType", record.get("relationshipType").asString());
                rel.put("otherUserId", record.get("otherUserId").asString());
                rel.put("otherName", record.get("otherName").isNull() ? null : record.get("otherName").asString());
                rel.put("otherUsername",
                        record.get("otherUsername").isNull() ? null : record.get("otherUsername").asString());
                incoming.add(rel);
            }
            diagnostics.put("incomingRelationships", incoming);
            diagnostics.put("incomingCount", incoming.size());

            // Check what relationship types exist in the graph (sample)
            String relationshipTypesCypher = "MATCH ()-[r]->() RETURN DISTINCT type(r) as relationshipType, count(*) as count ORDER BY count DESC LIMIT 10";
            Result typesResult = session.run(relationshipTypesCypher);
            java.util.List<java.util.Map<String, Object>> relationshipTypes = new java.util.ArrayList<>();
            while (typesResult.hasNext()) {
                var record = typesResult.next();
                java.util.Map<String, Object> typeInfo = new java.util.HashMap<>();
                typeInfo.put("relationshipType", record.get("relationshipType").asString());
                typeInfo.put("count", record.get("count").asLong());
                relationshipTypes.add(typeInfo);
            }
            diagnostics.put("relationshipTypesInGraph", relationshipTypes);

            // Check if FOLLOWS relationships exist from this user's friends
            String friendsFollowCypher = "MATCH (current:User)-[:FOLLOWS]->(friend:User)-[r]->(other:User) " +
                    "WHERE toString(current.userId) = $userId " +
                    "RETURN toString(friend.userId) as friendUserId, friend.username as friendUsername, type(r) as relationshipType, toString(other.userId) as otherUserId, other.username as otherUsername "
                    +
                    "LIMIT 20";
            Result friendsFollowResult = session.run(friendsFollowCypher, Map.of("userId", userId));
            java.util.List<java.util.Map<String, Object>> friendsFollow = new java.util.ArrayList<>();
            while (friendsFollowResult.hasNext()) {
                var record = friendsFollowResult.next();
                java.util.Map<String, Object> followInfo = new java.util.HashMap<>();
                followInfo.put("friendUserId", record.get("friendUserId").asString());
                followInfo.put("friendUsername",
                        record.get("friendUsername").isNull() ? null : record.get("friendUsername").asString());
                followInfo.put("relationshipType", record.get("relationshipType").asString());
                followInfo.put("otherUserId", record.get("otherUserId").asString());
                followInfo.put("otherUsername",
                        record.get("otherUsername").isNull() ? null : record.get("otherUsername").asString());
                friendsFollow.add(followInfo);
            }
            diagnostics.put("friendsRelationships", friendsFollow);

            return diagnostics;
        }
    }
}
