DROP TABLE AppEvent;
DROP TABLE AppActionResult;
DROP TABLE AppAction;
DROP TABLE AppSection;
DROP TABLE AppEventType;
DROP TABLE AppEventSeverity;
DROP TABLE auth;

CREATE TABLE auth
(
  authid   SERIAL,
  username VARCHAR(200) NOT NULL UNIQUE,
  hash     VARCHAR(64)  NOT NULL,
  isadmin  BOOLEAN      NOT NULL,
  CONSTRAINT auth_pkey PRIMARY KEY (authid)
);

CREATE TABLE AppEventSeverity
(
  appEventSeverityId SERIAL,
  label              VARCHAR(100) NOT NULL UNIQUE,
  CONSTRAINT app_event_severity_pkey PRIMARY KEY (appEventSeverityId)
);

CREATE TABLE AppEventType
(
  appEventTypeId SERIAL,
  label          VARCHAR(100) NOT NULL UNIQUE,
  CONSTRAINT app_event_type_pkey PRIMARY KEY (appEventTypeId)
);

CREATE TABLE AppSection
(
  appSectionId SERIAL,
  label        VARCHAR(100) NOT NULL UNIQUE,
  CONSTRAINT app_section_pkey PRIMARY KEY (appSectionId)
);

CREATE TABLE AppAction
(
  appActionId SERIAL,
  label       VARCHAR(100) NOT NULL UNIQUE,
  CONSTRAINT app_action_pkey PRIMARY KEY (appActionId)
);

CREATE TABLE AppActionResult
(
  appActionResultId SERIAL,
  label             VARCHAR(100) NOT NULL UNIQUE,
  CONSTRAINT app_action_result_pkey PRIMARY KEY (appActionResultId)
);

CREATE TABLE AppEvent
(
  appEventId   SERIAL,
  timestamp    TIMESTAMP,
  userId       INT,
  category     INT,
  section      INT,
  action       INT,
  actionResult INT,
  severity     INT,
  CONSTRAINT app_event_pkey PRIMARY KEY (appEventId),
  CONSTRAINT app_event_fkey_auth_authid        FOREIGN KEY (userId)       REFERENCES Auth (authid),
  CONSTRAINT app_event_fkey_app_event_type     FOREIGN KEY (category)     REFERENCES AppEventType (appEventTypeId),
  CONSTRAINT app_event_fkey_app_section        FOREIGN KEY (section)      REFERENCES AppSection (appSectionId),
  CONSTRAINT app_event_fkey_app_action         FOREIGN KEY (action)       REFERENCES AppAction (appActionId),
  CONSTRAINT app_event_fkey_app_action_result  FOREIGN KEY (actionResult) REFERENCES AppActionResult (appActionResultId),
  CONSTRAINT app_event_fkey_app_event_severity FOREIGN KEY (severity)     REFERENCES AppEventSeverity (appEventSeverityId)
);

INSERT INTO Auth (username, hash, isadmin) VALUES ('admin', 'e2875c848ce7f34f266dc26da15fea61ba5dd2ca362a646f860cac8595471f12', TRUE);

INSERT INTO AppEventSeverity (label) VALUES ('MINOR');
INSERT INTO AppEventSeverity (label) VALUES ('NORMAL');
INSERT INTO AppEventSeverity (label) VALUES ('MAJOR');

INSERT INTO AppEventType (label) VALUES ('AUTH');
INSERT INTO AppEventType (label) VALUES ('APP');

INSERT INTO AppSection (label) VALUES ('LOGIN');
INSERT INTO AppSection (label) VALUES ('ADMIN');

INSERT INTO AppAction (label) VALUES ('LIST_USERS');
INSERT INTO AppAction (label) VALUES ('USER_LOGIN');
INSERT INTO AppAction (label) VALUES ('USER_LOGOUT');

INSERT INTO AppActionResult (label) VALUES ('ACTION_SUCCESS');
INSERT INTO AppActionResult (label) VALUES ('ACTION_FAILURE');
INSERT INTO AppActionResult (label) VALUES ('ACTION_NORMAL');

INSERT INTO AppEvent(userId, category, section, action, actionResult, severity) VALUES (1, 1, 1, 1, 1, 1);