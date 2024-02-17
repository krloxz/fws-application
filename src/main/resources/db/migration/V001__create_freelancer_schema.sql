CREATE SCHEMA IF NOT EXISTS freelancer;

CREATE TABLE IF NOT EXISTS freelancer.freelancers(
  id UUID NOT NULL PRIMARY KEY,
  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  middle_name TEXT,
  gender TEXT,
  birth_date DATE,
  hourly_wage_value NUMERIC(9,2) NOT NULL,
  hourly_wage_currency TEXT NOT NULL,
  nicknames TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS freelancer.addresses(
  freelancer_id UUID NOT NULL,
  street TEXT NOT NULL,
  apartment TEXT,
  city TEXT NOT NULL,
  state TEXT NOT NULL,
  country TEXT NOT NULL,
  zip_code TEXT NOT NULL,
  
  CONSTRAINT address_freelancer_fk
    FOREIGN KEY (freelancer_id)
    REFERENCES freelancers(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS freelancer.communication_channels(
  freelancer_id UUID NOT NULL,
  value1 TEXT NOT NULL,
  type TEXT NOT NULL,
  
  CONSTRAINT communication_channel_freelancer_fk
    FOREIGN KEY (freelancer_id)
    REFERENCES freelancers(id)
    ON DELETE CASCADE
);
