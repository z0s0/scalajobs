create extension if not exists "uuid-ossp";

create table organizations(
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  name text NOT NULL,
  description text,

  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,

  PRIMARY KEY(id)
);

create table vacancies (
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  organization_id UUID NOT NULL REFERENCES organizations(id),
  description TEXT NOT NULL,
  approved BOOLEAN NOT NULL DEFAULT FALSE,
  salary_from INTEGER NOT NULL,
  salary_to INTEGER NOT NULL,
  currency VARCHAR(10) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  contact_email TEXT,
  link TEXT,
  office_presence TEXT NOT NULL,

  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,

  CHECK(salary_from > 0),
  CHECK(salary_to > 0)
);
