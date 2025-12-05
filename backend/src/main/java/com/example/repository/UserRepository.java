package com.example.repository;

import com.example.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

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
}

