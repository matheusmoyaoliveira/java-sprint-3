CREATE TABLE paciente (
  id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome       VARCHAR2(120) NOT NULL,
  cpf        VARCHAR2(14)  NOT NULL,
  data_nasc  DATE          NOT NULL,
  telefone   VARCHAR2(20),
  email      VARCHAR2(120),
  CONSTRAINT uq_paciente_cpf UNIQUE (cpf)
);

CREATE TABLE medico (
  id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome          VARCHAR2(120) NOT NULL,
  crm           VARCHAR2(30)  NOT NULL,
  especialidade VARCHAR2(80)  NOT NULL,
  telefone      VARCHAR2(20),
  email         VARCHAR2(120),
  CONSTRAINT uq_medico_crm UNIQUE (crm)
);

CREATE TABLE consulta (
  id           NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  paciente_id  NUMBER NOT NULL,
  medico_id    NUMBER NOT NULL,
  data_hora    TIMESTAMP NOT NULL,
  status       VARCHAR2(15) NOT NULL,
  observacoes  VARCHAR2(255),
  CONSTRAINT fk_consulta_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id),
  CONSTRAINT fk_consulta_medico   FOREIGN KEY (medico_id)   REFERENCES medico(id),
  CONSTRAINT ck_consulta_status   CHECK (status IN ('AGENDADA','CANCELADA','CONCLUIDA'))
);

CREATE INDEX idx_consulta_medico_data ON consulta (medico_id, data_hora);

SELECT table_name FROM user_tables WHERE table_name IN ('PACIENTE','MEDICO','CONSULTA');
DESC paciente;
DESC medico;
DESC consulta;