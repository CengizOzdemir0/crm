-- Initial Schema for Enterprise CRM
-- Version: 1.0
-- Description: Creates all tables, indexes, and constraints

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    mobile VARCHAR(20),
    job_title VARCHAR(100),
    department VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    profile_image VARCHAR(500),
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(45),
    failed_login_attempts INTEGER DEFAULT 0,
    account_locked_until TIMESTAMP,
    password_changed_at TIMESTAMP,
    must_change_password BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_status ON users(status);

-- Permissions table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    category VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- User-Permission mapping
CREATE TABLE user_permissions (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, permission_id)
);

-- Customers table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(200) NOT NULL,
    customer_type VARCHAR(20) NOT NULL,
    industry VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    website VARCHAR(200),
    tax_number VARCHAR(50),
    tax_office VARCHAR(100),
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    employee_count INTEGER,
    annual_revenue DECIMAL(15,2),
    customer_since DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    account_manager_id BIGINT REFERENCES users(id),
    rating INTEGER,
    notes TEXT,
    tags VARCHAR(500),
    linkedin_url VARCHAR(200),
    twitter_handle VARCHAR(100),
    facebook_url VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_customer_email ON customers(email);
CREATE INDEX idx_customer_company ON customers(company_name);
CREATE INDEX idx_customer_status ON customers(status);
CREATE INDEX idx_customer_type ON customers(customer_type);

-- Contacts table
CREATE TABLE contacts (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    job_title VARCHAR(100),
    department VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    mobile VARCHAR(20),
    is_primary BOOLEAN DEFAULT FALSE,
    is_decision_maker BOOLEAN DEFAULT FALSE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    notes TEXT,
    linkedin_url VARCHAR(200),
    twitter_handle VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_contact_email ON contacts(email);
CREATE INDEX idx_contact_customer ON contacts(customer_id);

-- Leads table
CREATE TABLE leads (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    company_name VARCHAR(200),
    job_title VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    mobile VARCHAR(20),
    website VARCHAR(200),
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    source VARCHAR(50),
    rating INTEGER,
    estimated_value DECIMAL(15,2),
    expected_close_date DATE,
    assigned_to_id BIGINT REFERENCES users(id),
    notes TEXT,
    tags VARCHAR(500),
    is_converted BOOLEAN DEFAULT FALSE,
    converted_at DATE,
    converted_customer_id BIGINT REFERENCES customers(id),
    converted_opportunity_id BIGINT,
    linkedin_url VARCHAR(200),
    twitter_handle VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_lead_email ON leads(email);
CREATE INDEX idx_lead_status ON leads(status);
CREATE INDEX idx_lead_source ON leads(source);
CREATE INDEX idx_lead_assigned ON leads(assigned_to_id);

-- Products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(100),
    unit_price DECIMAL(15,2) NOT NULL,
    cost_price DECIMAL(15,2),
    unit VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    tax_rate DECIMAL(5,2),
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_product_code ON products(product_code);
CREATE INDEX idx_product_active ON products(is_active);

-- Opportunities table
CREATE TABLE opportunities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    value DECIMAL(15,2) NOT NULL,
    stage VARCHAR(30) NOT NULL DEFAULT 'PROSPECTING',
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    probability INTEGER,
    expected_close_date DATE,
    actual_close_date DATE,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    description TEXT,
    next_step VARCHAR(500),
    tags VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_opportunity_customer ON opportunities(customer_id);
CREATE INDEX idx_opportunity_stage ON opportunities(stage);
CREATE INDEX idx_opportunity_status ON opportunities(status);
CREATE INDEX idx_opportunity_owner ON opportunities(owner_id);

-- Update leads foreign key
ALTER TABLE leads ADD CONSTRAINT fk_lead_converted_opportunity 
    FOREIGN KEY (converted_opportunity_id) REFERENCES opportunities(id);

-- Opportunity Products table
CREATE TABLE opportunity_products (
    id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    discount_percentage DECIMAL(5,2) DEFAULT 0,
    tax_rate DECIMAL(5,2) DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_opp_product_opportunity ON opportunity_products(opportunity_id);
CREATE INDEX idx_opp_product_product ON opportunity_products(product_id);

-- Activities table
CREATE TABLE activities (
    id BIGSERIAL PRIMARY KEY,
    subject VARCHAR(200) NOT NULL,
    activity_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    due_date TIMESTAMP,
    completed_date TIMESTAMP,
    duration_minutes INTEGER,
    assigned_to_id BIGINT NOT NULL REFERENCES users(id),
    customer_id BIGINT REFERENCES customers(id),
    opportunity_id BIGINT REFERENCES opportunities(id),
    contact_id BIGINT REFERENCES contacts(id),
    description TEXT,
    location VARCHAR(200),
    outcome TEXT,
    is_reminder_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_activity_type ON activities(activity_type);
CREATE INDEX idx_activity_status ON activities(status);
CREATE INDEX idx_activity_assigned ON activities(assigned_to_id);
CREATE INDEX idx_activity_due_date ON activities(due_date);

-- Insert default admin user (password: Admin@123)
INSERT INTO users (first_name, last_name, email, password, role, status, created_by)
VALUES ('Admin', 'User', 'admin@crm.com', 
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg0sBnmi4Gu',
        'ADMIN', 'ACTIVE', 'system');

-- Insert sample permissions
INSERT INTO permissions (name, code, description, category, created_by) VALUES
('View Customers', 'VIEW_CUSTOMERS', 'Can view customer records', 'CUSTOMER', 'system'),
('Create Customers', 'CREATE_CUSTOMERS', 'Can create new customers', 'CUSTOMER', 'system'),
('Edit Customers', 'EDIT_CUSTOMERS', 'Can edit customer records', 'CUSTOMER', 'system'),
('Delete Customers', 'DELETE_CUSTOMERS', 'Can delete customers', 'CUSTOMER', 'system'),
('View Leads', 'VIEW_LEADS', 'Can view leads', 'LEAD', 'system'),
('Create Leads', 'CREATE_LEADS', 'Can create new leads', 'LEAD', 'system'),
('Edit Leads', 'EDIT_LEADS', 'Can edit leads', 'LEAD', 'system'),
('Convert Leads', 'CONVERT_LEADS', 'Can convert leads to customers', 'LEAD', 'system'),
('View Opportunities', 'VIEW_OPPORTUNITIES', 'Can view opportunities', 'OPPORTUNITY', 'system'),
('Create Opportunities', 'CREATE_OPPORTUNITIES', 'Can create opportunities', 'OPPORTUNITY', 'system'),
('Edit Opportunities', 'EDIT_OPPORTUNITIES', 'Can edit opportunities', 'OPPORTUNITY', 'system'),
('Manage Users', 'MANAGE_USERS', 'Can manage system users', 'ADMIN', 'system'),
('View Reports', 'VIEW_REPORTS', 'Can view reports', 'REPORT', 'system');
