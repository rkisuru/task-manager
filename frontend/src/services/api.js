const API_BASE_URL = import.meta.env.VITE_API_URL || '';

/**
 * API client — all HTTP calls to the TaskFlow gateway go through here.
 * Automatically attaches JWT token from localStorage.
 */
class ApiClient {
  constructor() {
    this.baseUrl = API_BASE_URL;
  }

  getToken() {
    return localStorage.getItem('taskflow_token');
  }

  setToken(token) {
    localStorage.setItem('taskflow_token', token);
  }

  clearToken() {
    localStorage.removeItem('taskflow_token');
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseUrl}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
      ...options,
      headers,
    });

    // Handle 401 — token expired or invalid
    if (response.status === 401) {
      this.clearToken();
      window.location.href = '/';
      throw new Error('Session expired. Please log in again.');
    }

    const data = await response.json().catch(() => null);

    if (!response.ok) {
      const message = data?.message || data?.error || `Request failed (${response.status})`;
      throw new Error(message);
    }

    return data;
  }

  // ---- Auth ----

  async register(username, email, password) {
    const data = await this.request('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, email, password }),
    });
    this.setToken(data.token);
    return data;
  }

  async login(username, password) {
    const data = await this.request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
    this.setToken(data.token);
    return data;
  }

  logout() {
    this.clearToken();
  }

  // ---- Users ----

  async getCurrentUser() {
    return this.request('/api/users/me');
  }

  // ---- Tasks ----

  async getTasks(status) {
    const query = status ? `?status=${status}` : '';
    return this.request(`/api/tasks${query}`);
  }

  async getTask(id) {
    return this.request(`/api/tasks/${id}`);
  }

  async createTask(task) {
    return this.request('/api/tasks', {
      method: 'POST',
      body: JSON.stringify(task),
    });
  }

  async updateTask(id, updates) {
    return this.request(`/api/tasks/${id}`, {
      method: 'PUT',
      body: JSON.stringify(updates),
    });
  }

  async deleteTask(id) {
    return this.request(`/api/tasks/${id}`, {
      method: 'DELETE',
    });
  }
}

const apiClient = new ApiClient();
export default apiClient;
