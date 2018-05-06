CREATE TABLE users (
  id bigserial NOT NULL,
  color character varying(255) NOT NULL,
  group_id bigint NOT NULL,
  name character varying(255) NOT NULL,
  surname character varying(255) NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE currency (
  id bigserial NOT NULL,
  name character varying(255) NOT NULL,
  CONSTRAINT currency_pkey PRIMARY KEY (id)
);

CREATE TABLE rates (
  id bigserial NOT NULL,
  rate numeric(19,2) NOT NULL,
  updated date,
  version integer NOT NULL,
  currency_id bigint,
  CONSTRAINT rates_pkey PRIMARY KEY (id),
  CONSTRAINT fkoyncnn4efl38rtjlq08fj9efd FOREIGN KEY (currency_id)
      REFERENCES currency (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE accounts
(
  id bigserial NOT NULL,
  amount numeric(19,2) NOT NULL,
  amount_base numeric(19,2) NOT NULL,
  amount_base_change numeric(19,2),
  amount_change numeric(19,2),
  bank character varying(255) NOT NULL,
  previous_id bigint NOT NULL,
  updated date,
  version integer NOT NULL,
  currency_id bigint,
  rate_id bigint,
  user_id bigint,
  CONSTRAINT accounts_pkey PRIMARY KEY (id),
  CONSTRAINT fkks5v3om3pymdtf0wuslv1exjb FOREIGN KEY (currency_id)
      REFERENCES currency (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fknjuop33mo69pd79ctplkck40n FOREIGN KEY (user_id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkraaal8fe657pjtbymm1ex61i7 FOREIGN KEY (rate_id)
      REFERENCES rates (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
