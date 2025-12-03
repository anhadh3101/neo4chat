// Mock userId storage utility
// In a real app, this would be handled by authentication

const USER_ID_KEY = "neo4chat_userId";

export function getCurrentUserId() {
  let userId = localStorage.getItem(USER_ID_KEY);

  // If no userId exists, create a mock one
  if (!userId) {
    // Temp mock userId for testing
    userId = "19351";
    localStorage.setItem(USER_ID_KEY, userId);
  }

  return userId;
}

export function setCurrentUserId(userId) {
  localStorage.setItem(USER_ID_KEY, userId);
}

export function clearCurrentUserId() {
  localStorage.removeItem(USER_ID_KEY);
}
