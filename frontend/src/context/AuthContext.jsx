import { createContext, useContext, useState, useEffect } from 'react';
import apiClient from '../services/api';

const AuthContext = createContext(null);

/**
 * Auth context provider — manages JWT token, user state, and auth operations.
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Check for existing token on mount
  useEffect(() => {
    const token = apiClient.getToken();
    if (token) {
      apiClient.getCurrentUser()
        .then(userData => setUser(userData))
        .catch(() => {
          apiClient.clearToken();
          setUser(null);
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (username, password) => {
    const data = await apiClient.login(username, password);
    setUser({ username: data.username, email: data.email, role: data.role });
    return data;
  };

  const register = async (username, email, password) => {
    const data = await apiClient.register(username, email, password);
    setUser({ username: data.username, email: data.email, role: data.role });
    return data;
  };

  const logout = () => {
    apiClient.logout();
    setUser(null);
  };

  const value = {
    user,
    loading,
    login,
    register,
    logout,
    isAuthenticated: !!user,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
