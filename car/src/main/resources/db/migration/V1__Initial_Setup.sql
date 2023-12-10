CREATE TABLE car
(
    id          BIGSERIAL PRIMARY KEY,
    make        TEXT NOT NULL,
    model       TEXT NOT NULL,
    version      TEXT NOT NULL,
    door INT NOT NULL,
    gross_price     NUMERIC NOT NULL,
    nett_price       NUMERIC NOT NULL,
    horsepower INT  NOT NULL
);