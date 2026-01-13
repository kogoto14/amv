CREATE TABLE codebase (
  id CHAR(22) PRIMARY KEY,
  name VARCHAR(255),
  url VARCHAR(1023),
  site VARCHAR(1023),
  token VARCHAR(255),
  commit_hash VARCHAR(64),
  branch VARCHAR(255),
  analyzed_at TIMESTAMP,
  analyzed_in BIGINT,
  --{commonColumns}
);


CREATE TABLE project (
  id CHAR(22) PRIMARY KEY,
  codebase_id CHAR(22) REFERENCES codebase ON DELETE CASCADE,
  name VARCHAR(255),
  path VARCHAR(255),
  source_dirs TEXT,
  language_version VARCHAR(10),
  --{commonColumns}
);


CREATE TABLE source_file (
  id CHAR(22) PRIMARY KEY,
  project_id CHAR(22) REFERENCES project ON DELETE CASCADE,
  name VARCHAR(255),
  path VARCHAR(255),
  namespace VARCHAR(511),
  content TEXT,
  --{commonColumns}
);


CREATE TABLE reference (
  source_file_id CHAR(22) REFERENCES source_file ON DELETE CASCADE,
  seq_no INT,
  reference VARCHAR(1023) NOT NULL,
  PRIMARY KEY (source_file_id, seq_no),
  --{commonColumns}
);


CREATE TABLE type (
  id CHAR(22) PRIMARY KEY,
  source_file_id CHAR(22) REFERENCES source_file ON DELETE CASCADE,
  name VARCHAR(511) NOT NULL,
  qualified_name VARCHAR(1023) UNIQUE,
  kind CHAR(1),
  annotations TEXT,
  data_kind CHAR(1),
  data_name VARCHAR(255),
  method_cnt INT DEFAULT 0,
  unsolved_cnt INT DEFAULT 0,
  -- TODO: Change to FLOAT4 after support of jpa-entity-generator.
  -- unsolved_rate FLOAT4 DEFAULT 0,
  unsolved_rate NUMERIC DEFAULT 0,
  --{commonColumns}
);


CREATE TABLE method (
  type_id CHAR(22) REFERENCES type ON DELETE CASCADE,
  seq_no INT,
  name VARCHAR(255) NOT NULL,
  qualified_signature VARCHAR(1023) UNIQUE,
  fallback_signature VARCHAR(1023),
  interface_signature VARCHAR(1023),
  interface_signature_cnt INT,
  unsolved_reason TEXT,
  line_no INT,
  return_type VARCHAR(1023),
  annotations TEXT,
  method_call_cnt INT DEFAULT 0,
  unsolved_method_call_cnt INT DEFAULT 0,
  PRIMARY KEY (type_id, seq_no),
  --{commonColumns}
);


CREATE TABLE method_param (
  type_id CHAR(22),
  method_seq_no INT,
  seq_no INT,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(1023),
  PRIMARY KEY (type_id, method_seq_no, seq_no),
  FOREIGN KEY (type_id, method_seq_no) REFERENCES method (type_id, seq_no) ON DELETE CASCADE,
  --{commonColumns}
);


CREATE TABLE field (
  type_id CHAR(22) REFERENCES type ON DELETE CASCADE,
  seq_no INT,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(1023),
  PRIMARY KEY (type_id, seq_no),
  --{commonColumns}
);


CREATE TABLE flow_statement (
  id CHAR(22) PRIMARY KEY,
  parent_id CHAR(22) REFERENCES flow_statement ON DELETE CASCADE,
  type_id CHAR(22),
  method_seq_no INT,
  kind CHAR(2),
  content TEXT,
  line_no INT,
  FOREIGN KEY (type_id, method_seq_no) REFERENCES method (type_id, seq_no) ON DELETE CASCADE,
  --{commonColumns}
);


CREATE TABLE method_call (
  type_id CHAR(22),
  method_seq_no INT,
  seq_no INT,
  qualified_signature VARCHAR(1023),
  fallback_signature VARCHAR(1023),
  interface_signature VARCHAR(1023),
  unsolved_reason TEXT,
  argument_types TEXT,
  line_no INT,
  callee_type_id CHAR(22),
  callee_seq_no INT,
  caller_seq_no INT,
  flow_statement_id CHAR(22) REFERENCES flow_statement ON DELETE CASCADE,
  PRIMARY KEY (type_id, method_seq_no, seq_no),
  FOREIGN KEY (type_id, method_seq_no) REFERENCES method (type_id, seq_no) ON DELETE CASCADE,
  FOREIGN KEY (callee_type_id, callee_seq_no) REFERENCES method (type_id, seq_no) ON DELETE CASCADE,
  -- TODO: Enable this foreign key after fixing jpa-entity-generator to suppress generation of OntToMany relation.
  -- FOREIGN KEY (
  --   type_id,
  --   method_seq_no,
  --   caller_seq_no
  -- ) REFERENCES method_call (type_id, method_seq_no, seq_no) ON DELETE CASCADE,
  --{commonColumns}
);


CREATE TABLE entry_point (
  type_id CHAR(22),
  method_seq_no INT,
  path VARCHAR(1023),
  http_method VARCHAR(10),
  PRIMARY KEY (type_id, method_seq_no),
  FOREIGN KEY (type_id, method_seq_no) REFERENCES method (type_id, seq_no) ON DELETE CASCADE,
  --{commonColumns}
);


CREATE TABLE crud_point (
  type_id CHAR(22),
  method_seq_no INT,
  seq_no INT,
  kind CHAR(1),
  data_name VARCHAR(255),
  type VARCHAR(1023),
  crud CHAR(1),
  PRIMARY KEY (type_id, method_seq_no, seq_no),
  FOREIGN KEY (type_id, method_seq_no) REFERENCES method (type_id, seq_no) ON DELETE CASCADE,
  --{commonColumns}
);