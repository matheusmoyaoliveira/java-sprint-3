# HC – Health Clinic (Console) | Java + JDBC + Oracle

## Objetivo & Escopo
Aplicação de console para **gestão de pacientes, médicos e consultas**.  
Abrange CRUD completo (Create/Read/Update/Delete) de *Paciente* e *Médico* e o agendamento de *Consulta* com regras de negócio:
- CPF e CRM **únicos**.
- **Choque de agenda** não permitido para o **mesmo médico** no **mesmo dia/hora** enquanto a consulta estiver **AGENDADA**.
- Fluxo de status da consulta: `AGENDADA → CANCELADA/CONCLUIDA`.  
- Formatação e validação básica de entradas (CPF, telefone, datas).

Arquitetura em camadas:
- **domain**: entidades (`Paciente`, `Medico`, `Consulta`, `StatusConsulta`).
- **repository (DAO)**: interfaces + implementações JDBC (`PacienteRepositoryJdbc` etc.).
- **service**: regras de negócio (`PacienteService`, `MedicoService`, `ConsultaService`).
- **util**: formatação e I/O de console (`Formatters`, `ConsoleIO`), conexão (`DbConnection`).
- **app**: `Main` (menu de console) e `CrudSmokeTest` (teste automático/smoke).

---

## Requisitos
- **Java JDK 23** (ou compatível com o `pom.xml` do projeto).
- **Maven** (para build/execução).
- **Oracle Database** (ambiente acadêmico FIAP) e **Oracle JDBC (ojdbc11)**.
- Acesso ao host acadêmico: `oracle.fiap.com.br:1521` (SID `orcl`) – ajuste conforme seu ambiente.

---

## Configuração do Banco (DDL)
Há scripts em `src/main/resources/sql/`:
- `apaga_sprint3.sql` – derruba as tabelas (com `CASCADE CONSTRAINTS`).
- `cria_sprint3.sql` – recria as tabelas e índices.
- **Opcional**: `mer.sql` (abaixo) reúne o DDL do MER para documentação.

> **Atenção**: a coluna `status` da tabela `consulta` deve aceitar os **mesmos valores** do enum `StatusConsulta`:  
> `AGENDADA`, `CANCELADA`, `CONCLUIDA` (não use `REALIZADA`).

Execute os scripts no SQL Developer/Worksheet **nesta ordem**: `apaga_sprint3.sql` → `cria_sprint3.sql`.

---

## Configuração de Conexão
Arquivo `src/main/resources/db.properties` (já no projeto):
```
# Exemplo – ajuste usuário e senha
db.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl
db.user=rmXXXXX
db.password=SUASENHA
db.driver=oracle.jdbc.OracleDriver
```
`DbConnection` carrega essas propriedades e valida a conexão com `ping()`.

---

## Como executar

### 1) Pelo **teste automático (smoke)**
- Classe: `br.com.fiap.sprint.app.CrudSmokeTest`
- Resultado esperado (exemplo): linhas `PASS` para cada operação básica e regras de negócio (conflito de agenda, etc.).  
Isso gera rapidamente **evidências** de funcionamento para a apresentação.

### 2) Pelo **menu de console**
- Classe: `br.com.fiap.sprint.app.Main`
- Menus:
  - **Pacientes**: cadastrar, listar, buscar por CPF, atualizar, excluir.
  - **Médicos**: cadastrar, listar, buscar por CRM, atualizar, excluir.
  - **Consultas**: agendar, listar, cancelar, concluir, reagendar, excluir.
- **Formatação de entradas** (já tratada por `Formatters`/`ConsoleIO`):
  - CPF: aceita com/sem máscara; é normalizado para `###.###.###-##`.
  - Telefone: normalizado para `(11)90000-0000` (ou `(11)93333-3333`).
  - Data de nascimento: `dd/MM/yyyy`.
  - Data/hora de consulta: `dd/MM/yyyy HH:mm`.
- **Dica**: cadastre ao menos **1 paciente** e **1 médico** antes de agendar consultas.

---

## Regras de Negócio implementadas
1. **Unicidade** de CPF (paciente) e CRM (médico).  
2. **Choque de agenda**: não permite `AGENDADA` com mesmo `medico_id` e `data_hora`.  
3. **Transições** de status: `AGENDADA → CANCELADA` ou `AGENDADA → CONCLUIDA`.  
4. **Atualização**/reagendamento apenas quando existir a consulta; mensagens claras em caso de erro.

---

## Estrutura de Pastas (resumo)
```
src/
 └─ main/
    ├─ java/br/com/fiap/sprint/
    │  ├─ app/        (Main, CrudSmokeTest)
    │  ├─ domain/     (entidades e enum)
    │  ├─ repository/ (DAO + JDBC)
    │  ├─ service/    (regras de negócio)
    │  └─ util/       (DbConnection, ConsoleIO, Formatters)
    └─ resources/
       ├─ db.properties
       └─ sql/ (cria_sprint3.sql, apaga_sprint3.sql, mer.sql)
```

---

## Problemas comuns & Dicas
- **ORA-12514/ORA-12154**: confira `db.url` (SID vs ServiceName). Para SID use `@host:port:SID`.  
  Para *service name*: `@//host:port/service`.
- **ORA-28000/28001**: usuário/senha inválidos/expirados.
- **ORA-00942 ao apagar**: esperado caso as tabelas ainda não existam; script ignora esse erro.
- **Driver**: confirme `ojdbc11` no `pom.xml` e `db.driver=oracle.jdbc.OracleDriver`.

---

## Créditos / Equipe
- Desenvolvimento do código (DDD simplificado, JDBC/DAO, regras) e documentação técnica da sprint.
- Este README serve como **guia de execução** e **evidências** de como rodar o projeto.
