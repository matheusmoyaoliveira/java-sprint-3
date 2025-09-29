package br.com.fiap.sprint.app;

import br.com.fiap.sprint.domain.*;
import br.com.fiap.sprint.repository.*;
import br.com.fiap.sprint.repository.ConsultaRepositoryJdbc;
import br.com.fiap.sprint.repository.MedicoRepositoryJdbc;
import br.com.fiap.sprint.repository.PacienteRepositoryJdbc;
import br.com.fiap.sprint.service.ConsultaService;
import br.com.fiap.sprint.service.MedicoService;
import br.com.fiap.sprint.service.PacienteService;
import br.com.fiap.sprint.util.ConsoleIO;
import br.com.fiap.sprint.util.DbConnection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Main {


    private static final PacienteRepository pRepo = new PacienteRepositoryJdbc();
    private static final MedicoRepository mRepo = new MedicoRepositoryJdbc();
    private static final ConsultaRepository cRepo = new ConsultaRepositoryJdbc();


    private static final PacienteService pService = new PacienteService(pRepo);
    private static final MedicoService mService = new MedicoService(mRepo);
    private static final ConsultaService cService = new ConsultaService(cRepo, pRepo, mRepo);

    public static void main(String[] args) {
        System.out.println("Ping DB: " + DbConnection.ping());
        menuPrincipal();
    }


    private static void menuPrincipal() {
        while (true) {
            System.out.println("=== HC - MENU PRINCIPAL ===");
            System.out.println("1) Pacientes");
            System.out.println("2) Médicos");
            System.out.println("3) Consultas");
            System.out.println("0) Sair");
            String op = ConsoleIO.line("> ");

            switch (op) {
                case "1" -> menuPacientes();
                case "2" -> menuMedicos();
                case "3" -> menuConsultas();
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Opção inválida.");
                    ConsoleIO.pause();
                }
            }
        }
    }


    private static void menuPacientes() {
        while (true) {
            System.out.println("[Pacientes]");
            System.out.println("1) Cadastrar");
            System.out.println("2) Listar");
            System.out.println("3) Buscar por CPF");
            System.out.println("4) Atualizar");
            System.out.println("5) Excluir");
            System.out.println("0) Voltar");
            String op = ConsoleIO.line("> ");

            switch (op) {
                case "1" -> { // Cadastrar
                    Paciente p = new Paciente();
                    p.setNome(ConsoleIO.line("Nome: "));
                    p.setCpf(ConsoleIO.line("CPF: "));
                    LocalDate dn = ConsoleIO.date("Data nasc (dd/MM/yyyy): ");
                    p.setDataNascimento(dn);
                    p.setTelefone(ConsoleIO.line("Telefone: "));
                    p.setEmail(ConsoleIO.line("Email: "));
                    try {
                        pService.create(p);
                        System.out.println("Criado: " + p);
                    } catch (Exception e) {
                        System.out.println("Erro ao salvar paciente: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "2" -> {
                    List<Paciente> lista = pService.listAll();
                    if (lista.isEmpty()) {
                        System.out.println("Nenhum paciente cadastrado.");
                    } else {
                        lista.forEach(System.out::println);
                    }
                    ConsoleIO.pause();
                }
                case "3" -> {
                    String cpf = ConsoleIO.line("CPF: ");
                    Optional<Paciente> opt = pRepo.findByCpf(cpf);
                    System.out.println(opt.orElse(null));
                    ConsoleIO.pause();
                }
                case "4" -> {
                    long id = ConsoleIO.long_("ID do paciente: ");
                    Optional<Paciente> pOpt = pRepo.findById(id);
                    if (pOpt.isEmpty()) {
                        System.out.println("Não encontrado.");
                        ConsoleIO.pause();
                        break;
                    }
                    Paciente p = pOpt.get();
                    String nome = ConsoleIO.line("Nome [" + p.getNome() + "]: ");
                    if (!nome.isBlank()) p.setNome(nome);

                    String cpf = ConsoleIO.line("CPF [" + p.getCpf() + "]: ");
                    if (!cpf.isBlank()) p.setCpf(cpf);

                    String tel = ConsoleIO.line("Telefone [" + p.getTelefone() + "]: ");
                    if (!tel.isBlank()) p.setTelefone(tel);

                    String email = ConsoleIO.line("Email [" + p.getEmail() + "]: ");
                    if (!email.isBlank()) p.setEmail(email);

                    try {
                        pService.update(p);
                        System.out.println("Atualizado.");
                    } catch (Exception e) {
                        System.out.println("Erro ao atualizar paciente: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "5" -> { // Excluir
                    long id = ConsoleIO.long_("ID: ");
                    try {
                        pService.delete(id);
                        System.out.println("Excluído.");
                    } catch (Exception e) {
                        System.out.println("Erro ao excluir paciente: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Opção inválida.");
                    ConsoleIO.pause();
                }
            }
        }
    }


    private static void menuMedicos() {
        while (true) {
            System.out.println("[Médicos]");
            System.out.println("1) Cadastrar");
            System.out.println("2) Listar");
            System.out.println("3) Buscar por CRM");
            System.out.println("4) Atualizar");
            System.out.println("5) Excluir");
            System.out.println("0) Voltar");
            String op = ConsoleIO.line("> ");

            switch (op) {
                case "1" -> {
                    Medico m = new Medico();
                    m.setNome(ConsoleIO.line("Nome: "));
                    m.setCrm(ConsoleIO.line("CRM: "));
                    m.setEspecialidade(ConsoleIO.line("Especialidade: "));
                    m.setTelefone(ConsoleIO.line("Telefone: "));
                    m.setEmail(ConsoleIO.line("Email: "));
                    try {
                        mService.create(m);
                        System.out.println("Criado: " + m);
                    } catch (Exception e) {
                        System.out.println("Erro ao salvar médico: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "2" -> {
                    List<Medico> lista = mService.listAll();
                    if (lista.isEmpty()) {
                        System.out.println("Nenhum médico cadastrado.");
                    } else {
                        lista.forEach(System.out::println);
                    }
                    ConsoleIO.pause();
                }
                case "3" -> {
                    String crm = ConsoleIO.line("CRM: ");
                    System.out.println(mRepo.findByCrm(crm).orElse(null));
                    ConsoleIO.pause();
                }
                case "4" -> { // Atualizar
                    long id = ConsoleIO.long_("ID do médico: ");
                    Optional<Medico> mOpt = mRepo.findById(id);
                    if (mOpt.isEmpty()) {
                        System.out.println("Não encontrado.");
                        ConsoleIO.pause();
                        break;
                    }
                    Medico m = mOpt.get();

                    String nome = ConsoleIO.line("Nome [" + m.getNome() + "]: ");
                    if (!nome.isBlank()) m.setNome(nome);

                    String crm = ConsoleIO.line("CRM [" + m.getCrm() + "]: ");
                    if (!crm.isBlank()) m.setCrm(crm);

                    String esp = ConsoleIO.line("Especialidade [" + m.getEspecialidade() + "]: ");
                    if (!esp.isBlank()) m.setEspecialidade(esp);

                    String tel = ConsoleIO.line("Telefone [" + m.getTelefone() + "]: ");
                    if (!tel.isBlank()) m.setTelefone(tel);

                    String email = ConsoleIO.line("Email [" + m.getEmail() + "]: ");
                    if (!email.isBlank()) m.setEmail(email);

                    try {
                        mService.update(m);
                        System.out.println("Atualizado.");
                    } catch (Exception e) {
                        System.out.println("Erro ao atualizar médico: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "5" -> {
                    long id = ConsoleIO.long_("ID: ");
                    try {
                        mService.delete(id);
                        System.out.println("Excluído.");
                    } catch (Exception e) {
                        System.out.println("Erro ao excluir médico: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Opção inválida.");
                    ConsoleIO.pause();
                }
            }
        }
    }


    private static void menuConsultas() {
        while (true) {
            System.out.println("[Consultas]");
            System.out.println("1) Agendar");
            System.out.println("2) Listar");
            System.out.println("3) Cancelar");
            System.out.println("4) Concluir");
            System.out.println("5) Reagendar");
            System.out.println("6) Excluir");
            System.out.println("0) Voltar");
            String op = ConsoleIO.line("> ");

            switch (op) {
                case "1" -> {
                    long pacienteId = ConsoleIO.long_("Paciente ID: ");
                    long medicoId = ConsoleIO.long_("Médico ID: ");
                    LocalDateTime dh = ConsoleIO.dateTime("Data/hora (dd/MM/yyyy HH:mm): ");
                    String obs = ConsoleIO.line("Observações: ");

                    Consulta c = new Consulta();
                    c.setPacienteId(pacienteId);
                    c.setMedicoId(medicoId);
                    c.setDataHora(dh);
                    c.setObservacoes(obs);

                    try {
                        cService.agendar(c);
                        System.out.println("Agendada: " + c);
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "2" -> {
                    List<Consulta> lista = cService.listar();
                    if (lista.isEmpty()) {
                        System.out.println("Nenhuma consulta cadastrada.");
                    } else {
                        lista.forEach(System.out::println);
                    }
                    ConsoleIO.pause();
                }
                case "3" -> {
                    long id = ConsoleIO.long_("ID da consulta: ");
                    try {
                        cService.cancelar(id);
                        System.out.println("Cancelada.");
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "4" -> {
                    long id = ConsoleIO.long_("ID da consulta: ");
                    try {
                        cService.concluir(id);
                        System.out.println("Concluída.");
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "5" -> {
                    long id = ConsoleIO.long_("ID da consulta: ");
                    LocalDateTime nova = ConsoleIO.dateTime("Nova data/hora (dd/MM/yyyy HH:mm): ");
                    try {
                        cService.reagendar(id, nova);
                        System.out.println("Reagendada.");
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "6" -> {
                    long id = ConsoleIO.long_("ID da consulta: ");
                    try {
                        cService.excluir(id);
                        System.out.println("Excluída.");
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    ConsoleIO.pause();
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Opção inválida.");
                    ConsoleIO.pause();
                }
            }
        }
    }
}