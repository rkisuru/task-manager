import { AuthProvider, useAuth } from './context/AuthContext';
import AuthPage from './pages/AuthPage';
import TaskBoard from './pages/TaskBoard';
import './index.css';

function AppContent() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="app-loading">
        <div className="app-loading-spinner"></div>
        <p>Loading TaskFlow...</p>
      </div>
    );
  }

  return isAuthenticated ? <TaskBoard /> : <AuthPage />;
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
