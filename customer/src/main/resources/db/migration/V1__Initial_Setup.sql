CREATE TABLE customer
(
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    email       TEXT NOT NULL,
    street      TEXT NOT NULL,
    housenumber TEXT NOT NULL,
    zipcode     TEXT NOT NULL,
    place       TEXT NOT NULL,
    phonenumber INT  NOT NULL
);