CREATE TYPE priority_status AS ENUM ('HIGH', 'MEDIUM', 'LOW', 'NONE');
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE task_status AS ENUM (
    'TODO',
    'UPCOMING',
    'IN_PROGRESS',
    'COMPLETE',
    'OVERDUE'
    );

CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    user_status VARCHAR(36) NOT NULL,
    role user_role DEFAULT 'USER',
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    task_title varchar(255) not null,
    task_description varchar(255) not null,
    due_date date not null,
    priority priority_status default 'NONE' not null,
    status task_status default 'TODO' not null,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS subtasks (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    task_id BIGINT NOT NULL,
    subtask_title VARCHAR(255) NOT NULL,
    subtask_description VARCHAR(255) NOT NULL,
    status task_status DEFAULT 'TODO' NOT NULL,
    priority priority_status DEFAULT 'NONE' NOT NULL,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS recurring_tasks (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    task_title VARCHAR(255) NOT NULL,
    task_description VARCHAR(255) NOT NULL,
    recurrence_pattern VARCHAR(50) NOT NULL,
    recurrence_freq INT NULL,
    recurrence_end_date DATE NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    tag_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

-- Junction table for tagging tasks
CREATE TABLE IF NOT EXISTS task_tags (
    task_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (task_id, tag_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_tasks_progress (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    completed_tasks INT NOT NULL,
    total_tasks INT NOT NULL,
    completion_percentage DECIMAL(5,2),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_task_milestones (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    milestone_event VARCHAR(255) NOT NULL, -- Description of the milestone event
    milestone_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- Date of the milestone
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS user_task_completion_trends (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    time_interval VARCHAR(50) NOT NULL,
    completion_rate DECIMAL(5,2),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
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
    account_id BIGINT NOT NULL,
    time_interval VARCHAR(50) NOT NULL,
    completion_rate DECIMAL(5, 2),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE IF NOT EXISTS user_activity_summary (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id BIGINT NOT NULL,
    date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tasks_created INT NOT NULL,
    tasks_completed INT NOT NULL,
    tasks_pending INT NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE IF NOT EXISTS tag_usage_summary (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tag_id BIGINT NOT NULL,
    tag_name VARCHAR(255) NOT NULL,
    task_count INT NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);
