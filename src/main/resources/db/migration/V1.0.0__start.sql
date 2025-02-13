CREATE TABLE user_roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(10) NOT NULL,
    description varchar(50)  NOT NULL,
    date_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    public_id UUID NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password TEXT NOT NULL,
    role_id int NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    last_access TIMESTAMP DEFAULT NULL,
    date_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_users_user_roles FOREIGN KEY (role_id) REFERENCES user_roles(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE sessions (
    id SERIAL PRIMARY KEY,
    user_id int NOT NULL,
    token TEXT NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    user_agent TEXT NOT NULL,
    ip_address VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    date_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_sessions_users FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE RESTRICT ON DELETE RESTRICT
);

INSERT INTO user_roles (name, description)
VALUES ('ADMIN', 'Administrator'),
       ('USER', 'Usuario do sistema');


INSERT INTO users (public_id, username, email, password, role_id, active)
VALUES ('d290f1ee-6c54-4b01-90e6-d701748f0851', 'admin', 'admin@login.com',
        '$2a$12$WrMQ4ucFzRH6NEbFxidzuOur/7ctYkrJdabpm6mgOhHRiRaAp6vX2',
        1,
        TRUE);