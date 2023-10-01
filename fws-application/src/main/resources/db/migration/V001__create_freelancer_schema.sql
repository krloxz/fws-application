CREATE SCHEMA IF NOT EXISTS freelancer;

CREATE TABLE IF NOT EXISTS freelancer.freelancers(
  id UUID NOT NULL PRIMARY KEY,
  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  middle_name TEXT,
  gender TEXT,
  hourly_wage_value NUMERIC(5,2) NOT NULL,
  hourly_wage_currency TEXT NOT NULL,
  nicknames TEXT NOT NULL,
  version BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS freelancer.addresses(
  freelancer_id UUID NOT NULL,
  street TEXT NOT NULL,
  apartment TEXT,
  city TEXT NOT NULL,
  state TEXT NOT NULL,
  country TEXT NOT NULL,
  zip_code TEXT NOT NULL,
  version BIGINT NOT NULL,
  
  CONSTRAINT address_freelancer_fk
    FOREIGN KEY (freelancer_id)
    REFERENCES freelancers(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS freelancer.communication_channels(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  freelancer_id UUID NOT NULL,
  value1 TEXT NOT NULL,
  type TEXT NOT NULL,
  version BIGINT NOT NULL,
  
  CONSTRAINT communication_channel_freelancer_fk
    FOREIGN KEY (freelancer_id)
    REFERENCES freelancers(id)
    ON DELETE CASCADE
);
