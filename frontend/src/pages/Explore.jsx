import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  searchUsers,
  getFriendRecommendations,
  getPopularUsers,
} from "@/lib/api";
import { getCurrentUserId } from "@/lib/userStorage";

function UserCard({ user }) {
  return (
    <div className="mb-4 p-4 rounded-lg border bg-card text-card-foreground shadow-sm">
      <div className="flex flex-col gap-2">
        <div>
          <h3 className="font-semibold text-lg">{user.name || "Unknown"}</h3>
          <p className="text-sm text-muted-foreground">
            @{user.username || "unknown"}
          </p>
        </div>
        {user.bio && (
          <p className="text-sm text-muted-foreground">{user.bio}</p>
        )}
        {user.followCount !== undefined && (
          <p className="text-xs text-muted-foreground">
            {user.followCount}{" "}
            {user.followCount === 1 ? "follower" : "followers"}
          </p>
        )}
      </div>
    </div>
  );
}

function SearchColumn() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [offset, setOffset] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [searched, setSearched] = useState(false);

  const handleSearch = async (resetOffset = true) => {
    if (!query.trim()) {
      setResults([]);
      setSearched(false);
      return;
    }

    setLoading(true);
    try {
      const currentOffset = resetOffset ? 0 : offset;
      const data = await searchUsers(query, 10, currentOffset);

      if (resetOffset) {
        setResults(data);
        setOffset(10);
      } else {
        setResults((prev) => [...prev, ...data]);
        setOffset((prev) => prev + 10);
      }

      setHasMore(data.length === 10);
      setSearched(true);
    } catch (error) {
      console.error("Error searching users:", error);
      setResults([]);
      setHasMore(false);
    } finally {
      setLoading(false);
    }
  };

  const handleLoadMore = () => {
    handleSearch(false);
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      handleSearch(true);
    }
  };

  return (
    <div className="flex flex-col h-full">
      <CardHeader className="flex-shrink-0">
        <CardTitle>Search Users</CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-4 flex-1 min-h-0">
        <div className="flex gap-2 flex-shrink-0">
          <Input
            placeholder="Search by name or username..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyPress={handleKeyPress}
          />
          <Button onClick={() => handleSearch(true)} disabled={loading}>
            {loading ? "..." : "Search"}
          </Button>
        </div>

        <div className="flex-1 overflow-y-auto min-h-0 pr-2">
          {loading && offset === 0 && (
            <p className="text-sm text-muted-foreground">Searching...</p>
          )}

          {!loading && searched && results.length === 0 && (
            <p className="text-sm text-muted-foreground">No users found.</p>
          )}

          {results.map((user) => (
            <UserCard key={user.userId} user={user} />
          ))}

          {hasMore && results.length > 0 && (
            <Button
              variant="outline"
              onClick={handleLoadMore}
              disabled={loading}
              className="w-full mt-2"
            >
              {loading ? "Loading..." : "Load more"}
            </Button>
          )}
        </div>
      </CardContent>
    </div>
  );
}

function RecommendationsColumn() {
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const userId = getCurrentUserId();

  useEffect(() => {
    const fetchRecommendations = async () => {
      try {
        const data = await getFriendRecommendations(userId);
        setRecommendations(data || []);
      } catch (error) {
        console.error("Error fetching recommendations:", error);
        setRecommendations([]);
      } finally {
        setLoading(false);
      }
    };

    fetchRecommendations();
  }, [userId]);

  return (
    <div className="flex flex-col h-full">
      <CardHeader className="flex-shrink-0">
        <CardTitle>Friend Recommendations</CardTitle>
      </CardHeader>
      <CardContent className="flex-1 overflow-y-auto min-h-0 pr-2">
        {loading ? (
          <p className="text-sm text-muted-foreground">
            Loading recommendations...
          </p>
        ) : recommendations.length === 0 ? (
          <p className="text-sm text-muted-foreground">
            No recommendations available.
          </p>
        ) : (
          recommendations.map((user) => (
            <UserCard key={user.userId} user={user} />
          ))
        )}
      </CardContent>
    </div>
  );
}

function PopularColumn() {
  const [popularUsers, setPopularUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const userId = getCurrentUserId();

  useEffect(() => {
    const fetchPopularUsers = async () => {
      try {
        const data = await getPopularUsers(userId, 10);
        setPopularUsers(data || []);
      } catch (error) {
        console.error("Error fetching popular users:", error);
        setPopularUsers([]);
      } finally {
        setLoading(false);
      }
    };

    fetchPopularUsers();
  }, [userId]);

  return (
    <div className="flex flex-col h-full">
      <CardHeader className="flex-shrink-0">
        <CardTitle>Popular Users</CardTitle>
      </CardHeader>
      <CardContent className="flex-1 overflow-y-auto min-h-0 pr-2">
        {loading ? (
          <p className="text-sm text-muted-foreground">
            Loading popular users...
          </p>
        ) : popularUsers.length === 0 ? (
          <p className="text-sm text-muted-foreground">
            No popular users available.
          </p>
        ) : (
          popularUsers.map((user) => <UserCard key={user.userId} user={user} />)
        )}
      </CardContent>
    </div>
  );
}

export default function Explore() {
  return (
    <div className="container mx-auto p-6 max-w-7xl h-[calc(100vh-4rem)] flex flex-col">
      <h1 className="text-3xl font-bold mb-6 flex-shrink-0">Explore Users</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 flex-1 min-h-0">
        {/* Left Column - Search */}
        <Card className="flex flex-col h-full min-h-0">
          <SearchColumn />
        </Card>

        {/* Middle Column - Recommendations */}
        <Card className="flex flex-col h-full min-h-0">
          <RecommendationsColumn />
        </Card>

        {/* Right Column - Popular */}
        <Card className="flex flex-col h-full min-h-0">
          <PopularColumn />
        </Card>
      </div>
    </div>
  );
}
