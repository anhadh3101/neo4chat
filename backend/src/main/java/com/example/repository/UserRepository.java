package com.example.repository;

import com.example.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends Neo4jRepository<User, String> {
        Optional<User> findByUsername(String username);

        boolean existsByUsername(String username);

        boolean existsByEmail(String email);

        @Query("MATCH (u:User {userId: $userId}) RETURN COUNT(u) > 0")
        boolean existsByUserId(String userId);

        @Query("MATCH (u:User {email: $email}) RETURN u")
        Optional<User> findByEmail(String email);

        @Query("MATCH (u:User {userId: $userId}) RETURN u")
        Optional<User> findByUserId(String userId);

        // UC-5
        @Query("""
                        MATCH (f:User {email: $userEmail})
                        MATCH (t:User {email: $followedUserEmail})
                        MERGE (f)-[:FOLLOWS]->(t)
                        """)
        void createFollowsRelationshipByEmail(String userEmail, String followedUserEmail);

        @Query("""
                        MATCH (f:User {userId: $userId})
                        MATCH (t:User {userId: $followedUserId})
                        MERGE (f)-[:FOLLOWS]->(t)
                        """)
        void createFollowsRelationshipByUserId(String userId, String followedUserId);

        // UC-6
        @Query("""
                        MATCH (f:User {email: $userEmail})-[r:FOLLOWS]->(t:User {email: $followedUserEmail})
                        RETURN COUNT(r) > 0;
                        """)
        boolean followsByEmailExists(String userEmail, String followedUserEmail);

        @Query("""
                        MATCH (f:User {userId: $userId})-[r:FOLLOWS]->(t:User {userId: $followedUserId})
                        RETURN COUNT(r) > 0;
                        """)
        boolean followsByUserIdExists(String userId, String followedUserId);

        @Query("""
                        MATCH (f:User {email: $userEmail})-[r:FOLLOWS]->(t:User {email: $followedUserEmail})
                        DELETE r
                        """)
        void deleteFollowsRelationshipByEmail(String userEmail, String followedUserEmail);

        @Query("""
                        MATCH (f:User {userId: $userId})-[r:FOLLOWS]->(t:User {userId: $followedUserId})
                        DELETE r
                        """)
        void deleteFollowsRelationshipByUserId(String userId, String followedUserId);

        // UC - 7
        // Find all followers of a user by email
        @Query("""
                        MATCH (f:User)-[:FOLLOWS]->(u:User {email: $userEmail})
                        RETURN f;
                        """)
        List<User> findFollowersByEmail(String userEmail);

        // Find all followers of a user by userId
        @Query("""
                        MATCH (f:User)-[:FOLLOWS]->(u:User {userId: $userId})
                        RETURN f;
                        """)
        List<User> findFollowersByUserId(String userId);

        // Find all users that a user is following by email
        @Query("""
                        MATCH (u:User {email: $userEmail})-[:FOLLOWS]->(f:User)
                        RETURN f;
                        """)
        List<User> findFollowingByEmail(String userEmail);

        // Find all users that a user is following by userId
        @Query("""
                        MATCH (u:User {userId: $userId})-[:FOLLOWS]->(f:User)
                        RETURN f;
                        """)
        List<User> findFollowingByUserId(String userId);

        @Query("""
                        MATCH (u1:User {email: $userEmail})-[:FOLLOWS]->(m:User)<-[:FOLLOWS]-(u2:User {email: $otherUserEmail})
                        RETURN DISTINCT m;
                        """)
        List<User> findMutualConnectionsByEmail(String userEmail, String otherUserEmail);

        @Query("""
                        MATCH (u1:User {userId: $userId})-[:FOLLOWS]->(m:User)<-[:FOLLOWS]-(u2:User {userId: $otherUserId})
                        RETURN DISTINCT m;
                        """)
        List<User> findMutualConnectionsByUserId(String userId, String otherUserId);
}
