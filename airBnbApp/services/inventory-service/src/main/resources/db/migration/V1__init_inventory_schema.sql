CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    date DATE NOT NULL,
    booked_count INT NOT NULL DEFAULT 0,
    reserved_count INT NOT NULL DEFAULT 0,
    total_count INT NOT NULL,
    surge_factor DECIMAL(5, 2) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    city VARCHAR(255) NOT NULL,
    closed BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_property_room_date UNIQUE (property_id, room_id, date)
);

CREATE TABLE IF NOT EXISTS property_min_price (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    date DATE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
