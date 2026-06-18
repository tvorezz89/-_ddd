--liquibase formatted sql

--changeset durachenko:804ef6bc-88ca-42d2-b7c6-5ca9a93c455a
--comment Created ORDERS table
CREATE TABLE ORDERS (
        ID                          UUID,
        LOCATION_X                  INTEGER           NOT NULL,
        LOCATION_Y                  INTEGER           NOT NULL,
        VOLUME                      NUMERIC           NOT NULL,
        STATUS                      VARCHAR           NOT NULL,
        PRIMARY KEY (ID)
);
--rollback DROP TABLE ORDERS;

--changeset durachenko:50433942-bb0a-40f0-966c-da7b3f034ee5
--comment Created COURIER table
CREATE TABLE COURIER (
        ID                          UUID,
        NAME                        VARCHAR           NOT NULL,
        LOCATION_X                  INTEGER           NOT NULL,
        LOCATION_Y                  INTEGER           NOT NULL,
        PRIMARY KEY (ID)
);

CREATE TABLE ASSIGNMENT (
        ID                          UUID,
        ORDER_ID                    UUID              NOT NULL   REFERENCES ORDERS(ID),
        COURIER_ID                  UUID                         REFERENCES COURIER(ID),
        LOCATION_X                  INTEGER           NOT NULL,
        LOCATION_Y                  INTEGER           NOT NULL,
        VOLUME                      NUMERIC           NOT NULL,
        STATUS                      VARCHAR           NOT NULL,
        PRIMARY KEY (ID)
);
--rollback DROP TABLE ASSIGNMENT;
--rollback DROP TABLE COURIER;
