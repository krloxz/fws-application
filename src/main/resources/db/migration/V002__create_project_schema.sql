CREATE SCHEMA IF NOT EXISTS project;

CREATE TABLE IF NOT EXISTS project.projects(
  id UUID PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  required_hours INT NOT NULL
);

CREATE TABLE IF NOT EXISTS project.freelancers(
  id UUID PRIMARY KEY,
  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  middle_name TEXT,
  allocated_hours INT NOT NULL,
  project_id UUID NOT NULL,
  
  CONSTRAINT freelancer_project_fk
    FOREIGN KEY (project_id)
    REFERENCES projects(id)
    ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS freelancer_project_fk_idx
  ON project.freelancers(project_id);
