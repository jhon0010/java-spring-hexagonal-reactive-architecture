CREATE TABLE leads (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    birthdate DATE NOT NULL,
    state VARCHAR(20) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    document_type VARCHAR(50) NOT NULL,
    document_number INTEGER NOT NULL
);