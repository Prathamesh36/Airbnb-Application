CREATE TABLE booking (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rooms_count INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    booking_status VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_session_id VARCHAR(255) UNIQUE
);

CREATE TABLE guest (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(50),
    age INT
);

CREATE TABLE booking_guest (
    booking_id BIGINT NOT NULL,
    guest_id BIGINT NOT NULL,
    PRIMARY KEY (booking_id, guest_id),
    CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES booking(id),
    CONSTRAINT fk_guest FOREIGN KEY (guest_id) REFERENCES guest(id)
);


