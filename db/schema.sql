CREATE SCHEMA IF NOT EXISTS expense_splitter;
SET search_path TO expense_splitter, public;

-- =========================================================
-- 1) TRIP
-- =========================================================
CREATE TABLE trip (
                      trip_id          UUID PRIMARY KEY,
                      trip_name        VARCHAR(255) NOT NULL,
                      created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                      updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =========================================================
-- 2) TRIP PARTICIPANTS
-- trip-scoped participants
-- participant_name is unique within a trip because your
-- current Trip aggregate treats duplicate names as duplicates
-- =========================================================
CREATE TABLE trip_participant (
                                  trip_id              UUID NOT NULL,
                                  participant_id       VARCHAR(64) NOT NULL,
                                  participant_name     VARCHAR(255) NOT NULL,
                                  is_active            BOOLEAN NOT NULL DEFAULT TRUE,
                                  participant_order    BIGINT GENERATED ALWAYS AS IDENTITY,
                                  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                  removed_at           TIMESTAMPTZ NULL,

                                  PRIMARY KEY (trip_id, participant_id),

                                  CONSTRAINT fk_trip_participant_trip
                                      FOREIGN KEY (trip_id)
                                          REFERENCES trip(trip_id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT uq_trip_participant_name
                                      UNIQUE (trip_id, participant_name)
);

CREATE INDEX idx_trip_participant_trip_active
    ON trip_participant (trip_id, is_active, participant_order);

CREATE INDEX idx_trip_participant_trip_name
    ON trip_participant (trip_id, participant_name);

-- =========================================================
-- 3) TRIP TRANSACTIONS
-- one row per transaction
-- tx_order preserves insertion order from the app
-- =========================================================
CREATE TABLE trip_transaction (
                                  trip_id                  UUID NOT NULL,
                                  transaction_id           UUID NOT NULL,
                                  tx_order                 BIGINT GENERATED ALWAYS AS IDENTITY,
                                  spent_amount             NUMERIC(14,2) NOT NULL CHECK (spent_amount > 0),
                                  spent_by_participant_id  VARCHAR(64) NOT NULL,
                                  spent_on                 VARCHAR(32) NOT NULL,
                                  share_type               VARCHAR(32) NOT NULL,
                                  spent_date               DATE NOT NULL,
                                  created_at               TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                  PRIMARY KEY (trip_id, transaction_id),

                                  CONSTRAINT fk_trip_transaction_trip
                                      FOREIGN KEY (trip_id)
                                          REFERENCES trip(trip_id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_trip_transaction_spent_by
                                      FOREIGN KEY (trip_id, spent_by_participant_id)
                                          REFERENCES trip_participant(trip_id, participant_id)
                                          ON DELETE RESTRICT,

                                  CONSTRAINT chk_trip_transaction_spent_on
                                      CHECK (spent_on IN ('FOOD', 'TRANSPORT', 'STAY', 'OTHERS')),

                                  CONSTRAINT chk_trip_transaction_share_type
                                      CHECK (share_type IN ('EQUAL', 'SPONSORED'))
);

CREATE UNIQUE INDEX uq_trip_transaction_order
    ON trip_transaction (trip_id, tx_order);

CREATE INDEX idx_trip_transaction_trip
    ON trip_transaction (trip_id, tx_order);

CREATE INDEX idx_trip_transaction_spent_by
    ON trip_transaction (trip_id, spent_by_participant_id);

-- =========================================================
-- 4) TRANSACTION BENEFICIARIES
-- beneficiary_order is critical because your equal split logic
-- distributes extra cents by list order
-- =========================================================
CREATE TABLE trip_transaction_beneficiary (
                                              trip_id                     UUID NOT NULL,
                                              transaction_id              UUID NOT NULL,
                                              beneficiary_order           INT NOT NULL CHECK (beneficiary_order >= 0),
                                              beneficiary_participant_id  VARCHAR(64) NOT NULL,

                                              PRIMARY KEY (trip_id, transaction_id, beneficiary_order),

                                              CONSTRAINT fk_ttb_transaction
                                                  FOREIGN KEY (trip_id, transaction_id)
                                                      REFERENCES trip_transaction(trip_id, transaction_id)
                                                      ON DELETE CASCADE,

                                              CONSTRAINT fk_ttb_participant
                                                  FOREIGN KEY (trip_id, beneficiary_participant_id)
                                                      REFERENCES trip_participant(trip_id, participant_id)
                                                      ON DELETE RESTRICT,

                                              CONSTRAINT uq_ttb_participant_once_per_transaction
                                                  UNIQUE (trip_id, transaction_id, beneficiary_participant_id)
);

CREATE INDEX idx_ttb_transaction
    ON trip_transaction_beneficiary (trip_id, transaction_id, beneficiary_order);

CREATE INDEX idx_ttb_participant
    ON trip_transaction_beneficiary (trip_id, beneficiary_participant_id);

-- =========================================================
-- 5) CURRENT PERSISTED SETTLEMENT SNAPSHOT
-- replace-all semantics per trip, matching your current
-- persistsettlement() behavior
-- =========================================================
CREATE TABLE trip_current_settlement (
                                         trip_id               UUID NOT NULL,
                                         settlement_order      INT NOT NULL CHECK (settlement_order >= 0),
                                         from_participant_id   VARCHAR(64) NOT NULL,
                                         to_participant_id     VARCHAR(64) NOT NULL,
                                         amount                NUMERIC(14,2) NOT NULL CHECK (amount > 0),
                                         created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                         PRIMARY KEY (trip_id, settlement_order),

                                         CONSTRAINT fk_tcs_trip
                                             FOREIGN KEY (trip_id)
                                                 REFERENCES trip(trip_id)
                                                 ON DELETE CASCADE,

                                         CONSTRAINT fk_tcs_from_participant
                                             FOREIGN KEY (trip_id, from_participant_id)
                                                 REFERENCES trip_participant(trip_id, participant_id)
                                                 ON DELETE RESTRICT,

                                         CONSTRAINT fk_tcs_to_participant
                                             FOREIGN KEY (trip_id, to_participant_id)
                                                 REFERENCES trip_participant(trip_id, participant_id)
                                                 ON DELETE RESTRICT,

                                         CONSTRAINT chk_tcs_from_to_different
                                             CHECK (from_participant_id <> to_participant_id)
);

CREATE INDEX idx_tcs_trip
    ON trip_current_settlement (trip_id, settlement_order);

-- =========================================================
-- 6) updated_at trigger for trip
-- =========================================================
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_trip_set_updated_at ON trip;

CREATE TRIGGER trg_trip_set_updated_at
    BEFORE UPDATE ON trip
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();