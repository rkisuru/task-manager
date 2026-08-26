import './TaskCard.css';

const PRIORITY_CONFIG = {
  HIGH: { label: 'High', color: '#ef4444', bg: 'rgba(239, 68, 68, 0.1)' },
  MEDIUM: { label: 'Medium', color: '#f59e0b', bg: 'rgba(245, 158, 11, 0.1)' },
  LOW: { label: 'Low', color: '#22c55e', bg: 'rgba(34, 197, 94, 0.1)' },
};

/**
 * Individual task card — draggable, with priority badge and action buttons.
 */
export default function TaskCard({ task, onDragStart, onDelete, onStatusChange }) {
  const priority = PRIORITY_CONFIG[task.priority] || PRIORITY_CONFIG.MEDIUM;

  const formatDate = (dateStr) => {
    if (!dateStr) return null;
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  const isOverdue = () => {
    if (!task.dueDate || task.status === 'DONE') return false;
    return new Date(task.dueDate) < new Date();
  };

  return (
    <div
      className="task-card"
      draggable
      onDragStart={onDragStart}
    >
      {/* Priority badge */}
      <div className="task-card-top">
        <span
          className="task-priority"
          style={{ color: priority.color, background: priority.bg }}
        >
          {priority.label}
        </span>
        <button
          className="task-delete"
          onClick={(e) => {
            e.stopPropagation();
            if (confirm('Delete this task?')) onDelete();
          }}
          title="Delete task"
        >
          <svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor">
            <path d="M5.5 5.5A.5.5 0 016 6v6a.5.5 0 01-1 0V6a.5.5 0 01.5-.5zm2.5 0a.5.5 0 01.5.5v6a.5.5 0 01-1 0V6a.5.5 0 01.5-.5zm3 .5a.5.5 0 00-1 0v6a.5.5 0 001 0V6z"/>
            <path fillRule="evenodd" d="M14.5 3a1 1 0 01-1 1H13v9a2 2 0 01-2 2H5a2 2 0 01-2-2V4h-.5a1 1 0 01-1-1V2a1 1 0 011-1H5.5l1-1h3l1 1H14a1 1 0 011 1v1zM4.118 4L4 4.059V13a1 1 0 001 1h6a1 1 0 001-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/>
          </svg>
        </button>
      </div>

      {/* Title and description */}
      <h3 className="task-title">{task.title}</h3>
      {task.description && (
        <p className="task-description">{task.description}</p>
      )}

      {/* Footer */}
      <div className="task-footer">
        {task.dueDate && (
          <span className={`task-due ${isOverdue() ? 'overdue' : ''}`}>
            <svg width="12" height="12" viewBox="0 0 16 16" fill="currentColor">
              <path d="M8 3.5a.5.5 0 00-1 0V8a.5.5 0 00.252.434l3.5 2a.5.5 0 00.496-.868L8 7.71V3.5z"/>
              <path d="M8 16A8 8 0 108 0a8 8 0 000 16zm7-8A7 7 0 111 8a7 7 0 0114 0z"/>
            </svg>
            {formatDate(task.dueDate)}
          </span>
        )}

        {/* Quick status change buttons */}
        <div className="task-actions">
          {task.status !== 'TODO' && (
            <button
              className="task-action"
              onClick={() => onStatusChange('TODO')}
              title="Move to To Do"
            >
              📋
            </button>
          )}
          {task.status !== 'IN_PROGRESS' && (
            <button
              className="task-action"
              onClick={() => onStatusChange('IN_PROGRESS')}
              title="Move to In Progress"
            >
              ⚡
            </button>
          )}
          {task.status !== 'DONE' && (
            <button
              className="task-action"
              onClick={() => onStatusChange('DONE')}
              title="Mark as Done"
            >
              ✅
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
