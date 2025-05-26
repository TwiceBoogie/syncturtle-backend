CREATE TYPE priority_status AS ENUM ('HIGH', 'MEDIUM', 'LOW', 'NONE');
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE task_status AS ENUM (
    'TODO',
    'UPCOMING',
    'IN_PROGRESS',
    'COMPLETE',
    'OVERDUE'
);
CREATE TYPE dependency_type AS ENUM (
    'FINISH_TO_START',
    'START_TO_START',
    'FINISH_TO_FINISH',
    'START_TO_FINISH'
);
CREATE TYPE task_constraint AS ENUM ('MANDATORY', 'OPTIONAL');
CREATE TYPE user_status AS ENUM ('SUSPENDED', 'INACTIVE', 'ACTIVE', 'ARCHIVED');
CREATE TYPE time_period AS ENUM ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY');
CREATE TABLE IF NOT EXISTS users (
    id uuid NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    user_status user_status DEFAULT 'ACTIVE' NOT NULL,
    role user_role DEFAULT 'USER' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS workspaces (
    id uuid NOT NULL,
    name VARCHAR(80) NOT NULL,
    logo VARCHAR(200),
    slug VARCHAR(48) NOT NULL,
    owner_id uuid NOT NULL,
    created_by_id uuid,
    updated_by_id uuid,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
-- Users in a workspace, supports different roles in a workspace
-- CREATE TABLE IF NOT EXISTS workspace_users (
--     id UUID NOT NULL PRIMARY KEY,
--     workspace_id UUID NOT NULL,
--     user_id UUID NOT NULL,
--     role user_role DEFAULT 'USER',
--     joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE,
--     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
-- );
-- Projects
CREATE TABLE IF NOT EXISTS projects (
    id UUID NOT NULL,
    name varchar(255) not null,
    description varchar(255) not null,
    identifier VARCHAR(12) NOT NULL UNIQUE,
    start_date TIMESTAMP,
    target_date TIMESTAMP,
    cover_image VARCHAR(800),
    completed_date TIMESTAMP,
    archive_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    workspace_id uuid NOT NULL,
    created_by_id UUID,
    updated_by_id UUID,
    PRIMARY KEY (id)
);
-- Issues
CREATE TABLE IF NOT EXISTS tasks (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    priority priority_status DEFAULT 'NONE' NOT NULL,
    start_date TIMESTAMP,
    target_date TIMESTAMP,
    parent_id UUID,
    -- Recursive relationship for parent-child task
    project_id UUID,
    -- Links task to a project
    module_id UUID,
    -- Links task to a module (optional if tasks can be outside of modules)
    workspace_id uuid NOT NULL,
    completed_at TIMESTAMP,
    status task_status DEFAULT 'TODO' NOT NULL,
    archived_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    PRIMARY KEY (id)
);
-- Advanced task dependency table
-- CREATE TABLE IF NOT EXISTS task_dependencies (
--     id UUID NOT NULL PRIMARY KEY,
--     task_id UUID NOT NULL,
--     depends_on_task_id UUID NOT NULL,
--     dependency_type dependency_type NOT NULL, -- Type of dependency (finish-to-start, etc.)
--     constraint_type task_constraint DEFAULT 'MANDATORY', -- Can be mandatory or optional
--     priority INT DEFAULT 0, -- Priority of the dependency
--     status VARCHAR(50) DEFAULT 'PENDING', -- Dependency status
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
--     resolved_at TIMESTAMP, -- When dependency is resolved
--     due_date TIMESTAMP, -- Expected resolution due date
--     created_by UUID NOT NULL, -- Who created the dependency
--     updated_by UUID,
--     archived_at TIMESTAMP,
--     archived_by UUID,
--     FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
--     FOREIGN KEY (depends_on_task_id) REFERENCES tasks(id) ON DELETE CASCADE,
--     FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
--     FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE CASCADE,
--     FOREIGN KEY (archived_by) REFERENCES users(id) ON DELETE CASCADE
-- );
CREATE TABLE IF NOT EXISTS modules (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    start_date TIMESTAMP,
    target_date TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    archived_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS module_tasks (
    id UUID NOT NULL,
    task_id UUID NOT NULL,
    module_id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by_id UUID,
    updated_by_id UUID,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (module_id) REFERENCES modules(id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS task_attachments (
    id UUID NOT NULL,
    task_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    tag_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);
-- Junction table for tagging projects
CREATE TABLE IF NOT EXISTS task_tags (
    task_id uuid NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (task_id, tag_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS user_tasks_progress (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id uuid NOT NULL,
    completed_tasks INT NOT NULL,
    total_tasks INT NOT NULL,
    completion_percentage DECIMAL(5, 2),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS user_task_milestones (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id uuid NOT NULL,
    milestone_event VARCHAR(255) NOT NULL,
    -- Description of the milestone event
    milestone_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    -- Date of the milestone
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS user_task_completion_trends (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id uuid NOT NULL,
    time_interval VARCHAR(50) NOT NULL,
    completion_rate DECIMAL(5, 2),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS task_status_summary (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    date DATE NOT NULL,
    todo_count INT NOT NULL,
    in_progress_count INT NOT NULL,
    completed_count INT NOT NULL,
    todo_percentage DECIMAL(5, 2),
    in_progress_percentage DECIMAL(5, 2),
    completed_percentage DECIMAL(5, 2),
    task_completion_rate DECIMAL(5, 2),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS task_completion_trends (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id uuid NOT NULL,
    time_interval VARCHAR(50) NOT NULL,
    completion_rate DECIMAL(5, 2),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS user_activity_summary (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id uuid NOT NULL,
    date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tasks_created INT NOT NULL,
    tasks_completed INT NOT NULL,
    tasks_pending INT NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS tag_usage_summary (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tag_id BIGINT NOT NULL,
    tag_name VARCHAR(255) NOT NULL,
    task_count INT NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);
CREATE TABLE IF NOT EXISTS task_audit (
    audit_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    task_id uuid NOT NULL,
    changed_by UUID NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    -- e.g., 'CREATE', 'UPDATE', 'DELETE'
    change_details TEXT NOT NULL,
    -- JSON detailing what was changed
    change_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (changed_by) REFERENCES users(id)
);