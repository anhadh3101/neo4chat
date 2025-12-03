// API service functions for user endpoints
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Search for users by name or username
 * @param {string} query - Search query
 * @param {number} limit - Number of results to return (default: 10)
 * @param {number} offset - Offset for pagination (default: 0)
 * @returns {Promise<Array>} Array of UserSearchResult objects
 */
export async function searchUsers(query, limit = 10, offset = 0) {
  try {
    const response = await apiClient.get('/users/search', {
      params: {
        q: query,
        limit,
        offset,
      },
    });
    return response.data;
  } catch (error) {
    throw new Error(`Failed to search users: ${error.message}`);
  }
}

/**
 * Get friend recommendations for a user
 * @param {string} userId - User ID
 * @returns {Promise<Array>} Array of UserSearchResult objects
 */
export async function getFriendRecommendations(userId) {
  try {
    const response = await apiClient.get(`/users/${userId}/recommendations`);
    return response.data;
  } catch (error) {
    throw new Error(`Failed to get recommendations: ${error.message}`);
  }
}

/**
 * Get popular users for a user
 * @param {string} userId - User ID
 * @param {number} limit - Number of results to return (default: 10)
 * @returns {Promise<Array>} Array of UserSearchResult objects
 */
export async function getPopularUsers(userId, limit = 10) {
  try {
    const response = await apiClient.get('/users/popular', {
      params: {
        userId,
        limit,
      },
    });
    return response.data;
  } catch (error) {
    throw new Error(`Failed to get popular users: ${error.message}`);
  }
}
