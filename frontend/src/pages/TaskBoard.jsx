import { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../context/AuthContext';
import apiClient from '../services/api';
import TaskCard from '../components/TaskCard';
import CreateTaskModal from '../components/CreateTaskModal';
import './TaskBoard.css';

const COLUMNS = [
  { id: 'TODO', label: 'To Do', icon: '📋' },
  { id: 'IN_PROGRESS', label: 'In Progress', icon: '⚡' },
  { id: 'DONE', label: 'Done', icon: '✅' },
];

/**
 * Kanban-style task board with drag-and-drop between columns.
 */
export default function TaskBoard() {
  const { user, logout } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [draggedTask, setDraggedTask] = useState(null);
  const [error, setError] = useState('');

  // Fetch tasks on mount
  useEffect(() => {
    fetchTasks();
  }, []);

  const fetchTasks = async () => {
    try {
      setLoading(true);
      const data = await apiClient.getTasks();
      setTasks(data);
    } catch (err) {
      setError('Failed to load tasks');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTask = async (taskData) => {
    try {
      const newTask = await apiClient.createTask(taskData);
      setTasks((prev) => [...prev, newTask]);
      setShowCreateModal(false);
    } catch (err) {
      throw err; // Let the modal handle the error
    }
  };

  const handleUpdateStatus = async (taskId, newStatus) => {
    try {
      const updated = await apiClient.updateTask(taskId, { status: newStatus });
      setTasks((prev) => prev.map((t) => (t.id === taskId ? updated : t)));
    } catch (err) {
      setError('Failed to update task');
      console.error(err);
    }
  };

  const handleDeleteTask = async (taskId) => {
    try {
      await apiClient.deleteTask(taskId);
      setTasks((prev) => prev.filter((t) => t.id !== taskId));
    } catch (err) {
      setError('Failed to delete task');
      console.error(err);
    }
  };

  // Drag and drop handlers
  const handleDragStart = (task) => {
    setDraggedTask(task);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.currentTarget.classList.add('column-drag-over');
  };

  const handleDragLeave = (e) => {
    e.currentTarget.classList.remove('column-drag-over');
  };

  const handleDrop = (e, columnId) => {
    e.preventDefault();
    e.currentTarget.classList.remove('column-drag-over');

    if (draggedTask && draggedTask.status !== columnId) {
      handleUpdateStatus(draggedTask.id, columnId);
    }
    setDraggedTask(null);
  };

  // Memoize column groupings to avoid re-filtering the entire array on every render
  const tasksByColumn = useMemo(() => {
    const grouped = {};
    for (const col of COLUMNS) {
      grouped[col.id] = tasks.filter((t) => t.status === col.id);
    }
    return grouped;
  }, [tasks]);

  const doneCount = tasksByColumn['DONE']?.length ?? 0;

  return (
    <div className="board-page">
      {/* Header */}
      <header className="board-header">
        <div className="board-header-left">
          <div className="board-logo">
            <svg width="32" height="32" viewBox="0 0 40 40" fill="none">
              <rect width="40" height="40" rx="10" fill="url(#logo-grad)" />
              <path d="M12 20L18 26L28 14" stroke="white" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
              <defs>
                <linearGradient id="logo-grad" x1="0" y1="0" x2="40" y2="40">
                  <stop stopColor="#6366f1" />
                  <stop offset="1" stopColor="#8b5cf6" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1 className="board-title">TaskFlow</h1>
        </div>

        <div className="board-header-right">
          <span className="board-user">
            <span className="board-user-avatar">
              {user?.username?.charAt(0).toUpperCase()}
            </span>
            {user?.username}
          </span>
          <button className="board-logout" onClick={logout}>
            Sign Out
          </button>
        </div>
      </header>

      {/* Toolbar */}
      <div className="board-toolbar">
        <div className="board-stats">
          <span className="stat">
            <span className="stat-count">{tasks.length}</span> tasks
          </span>
          <span className="stat">
            <span className="stat-count">{doneCount}</span> completed
          </span>
        </div>
        <button
          className="btn-create-task"
          onClick={() => setShowCreateModal(true)}
        >
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M8 2a.75.75 0 01.75.75v4.5h4.5a.75.75 0 010 1.5h-4.5v4.5a.75.75 0 01-1.5 0v-4.5h-4.5a.75.75 0 010-1.5h4.5v-4.5A.75.75 0 018 2z" />
          </svg>
          New Task
        </button>
      </div>

      {/* Error banner */}
      {error && (
        <div className="board-error">
          {error}
          <button onClick={() => setError('')}>✕</button>
        </div>
      )}

      {/* Board Columns */}
      <div className="board-columns">
        {COLUMNS.map((column) => {
          const columnTasks = tasksByColumn[column.id] || [];
          return (
            <div
              key={column.id}
              className="board-column"
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDrop={(e) => handleDrop(e, column.id)}
            >
              <div className="column-header">
                <span className="column-icon">{column.icon}</span>
                <h2 className="column-title">{column.label}</h2>
                <span className="column-count">{columnTasks.length}</span>
              </div>

              <div className="column-tasks">
                {loading ? (
                  <div className="column-skeleton">
                    <div className="skeleton-card"></div>
                    <div className="skeleton-card"></div>
                  </div>
                ) : columnTasks.length === 0 ? (
                  <div className="column-empty">
                    <p>No tasks yet</p>
                  </div>
                ) : (
                  columnTasks.map((task) => (
                    <TaskCard
                      key={task.id}
                      task={task}
                      onDragStart={() => handleDragStart(task)}
                      onDelete={() => handleDeleteTask(task.id)}
                      onStatusChange={(newStatus) => handleUpdateStatus(task.id, newStatus)}
                    />
                  ))
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Create Task Modal */}
      {showCreateModal && (
        <CreateTaskModal
          onClose={() => setShowCreateModal(false)}
          onCreate={handleCreateTask}
        />
      )}
    </div>
  );
}
