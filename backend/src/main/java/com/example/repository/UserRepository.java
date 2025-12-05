package com.example.repository;

import com.example.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends Neo4jRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // UC-5
    @Query("""
            MATCH (f:User {email: $userEmail})
            MATCH (t:User {email: $followedUserEmail})
            MERGE (f)-[:FOLLOWS]->(t)
            """)
    void createFollowsRelationship(String userEmail, String followedUserEmail);

    // UC-6
    @Query("""
            MATCH (f:User {email: $userEmail})-[r:FOLLOWS]->(t:User {email: $followedUserEmail})
            RETURN COUNT(r) > 0;
            """)
    boolean followsByEmailExists(String userEmail, String followedUserEmail);

    @Query("""
            MATCH (f:User {email: $userEmail})-[r:FOLLOWS]->(t:User {email: $followedUserEmail})
            DELETE r;
            """)
    void deleteFollowsRelationship(String userEmail, String followedUserEmail);

    // UC - 7
    // Find all followers of a user by email
    @Query("""
            MATCH (f:User)-[:FOLLOWS]->(u:User {email: $userEmail})
            RETURN f;
            """)
    List<User> findFollowersByEmail(String userEmail);

    // Find all followers of a user by email
    @Query("""
            MATCH (u:User {email: $userEmail})-[:FOLLOWS]->(f:User)
            RETURN f;
            """)
    List<User> findFollowingByEmail(String userEmail);

    @Query("""
            MATCH (u1:User {email: $userEmail})-[:FOLLOWS]->(m:User)<-[:FOLLOWS]-(u2:User {email: $otherUserEmail})
            RETURN DISTINCT m;            
            """)
    List<User> findMutualConnectionsByEmail(String userEmail, String otherUserEmail);
}

