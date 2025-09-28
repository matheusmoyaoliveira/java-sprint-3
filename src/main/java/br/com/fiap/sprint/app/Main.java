package br.com.fiap.sprint.app;

import br.com.fiap.sprint.domain.Paciente;
import br.com.fiap.sprint.domain.Medico;
import br.com.fiap.sprint.domain.Consulta;
import br.com.fiap.sprint.repository.*;
import br.com.fiap.sprint.service.*;
import br.com.fiap.sprint.util.DbConnection;

import static br.com.fiap.sprint.util.ConsoleIO.*;

public class Main {

    private static final PacienteRepository pRepo = new PacienteRepositoryJdbc();
    private static final MedicoRepository   mRepo = new MedicoRepositoryJdbc();
    private static final ConsultaRepository cRepo = new ConsultaRepositoryJdbc();

    private static final PacienteService pService = new PacienteService(pRepo);
    private static final MedicoService   mService = new MedicoService(mRepo);
    private static final ConsultaService cService = new ConsultaService(cRepo, pRepo, mRepo);

    public static void main(String[] args) {
        System.out.println("Ping DB: " + DbConnection.ping());
        menuPrincipal();
    }

    private static void menuPrincipal() {
        while (true) {
            System.out.println("\n=== HC - MENU PRINCIPAL ===");
            System.out.println("1) Pacientes");
            System.out.println("2) Médicos");
            System.out.println("3) Consultas");
            System.out.println("0) Sair");
            String op = line("> ");
            switch (op) {
                case "1" -> menuPacientes();
                case "2" -> menuMedicos();
                case "3" -> menuConsultas();
                case "0" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void menuPacientes() {
        while (true) {
            System.out.println("\n[Pacientes]");
            System.out.println("1) Cadastrar");
            System.out.println("2) Listar");
            System.out.println("3) Buscar por CPF");
            System.out.println("4) Atualizar");
            System.out.println("5) Excluir");
            System.out.println("0) Voltar");
            String op = line("> ");
            try {
                switch (op) {
                    case "1" -> {
                        Paciente p = new Paciente();
                        p.setNome(line("Nome: "));
                        p.setCpf(line("CPF: "));
                        p.setDataNascimento(
                                java.time.LocalDate.parse(
                                        line("Data nasc (dd/MM/yyyy): "),
                                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                )
                        );
                        p.setTelefone(line("Telefone: "));
                        p.setEmail(line("Email: "));
                        pService.create(p);
                        System.out.println("Criado: " + p);
                        pause();
                    }
                    case "2" -> { pService.listAll().forEach(System.out::println); pause(); }
                    case "3" -> { System.out.println(pRepo.findByCpf(line("CPF: ")).orElse(null)); pause(); }
                    case "4" -> {
                        long id = long_("ID do paciente: ");
                        var pOpt = pRepo.findById(id);
                        if (pOpt.isEmpty()) { System.out.println("Não encontrado."); pause(); break; }
                        var p = pOpt.get();
                        var nome = line("Nome [" + p.getNome() + "]: ");         if (!nome.isBlank()) p.setNome(nome);
                        var cpf  = line("CPF [" + p.getCpf() + "]: ");           if (!cpf.isBlank())  p.setCpf(cpf);
                        var tel  = line("Telefone [" + p.getTelefone() + "]: "); if (!tel.isBlank())  p.setTelefone(tel);
                        var mail = line("Email [" + p.getEmail() + "]: ");       if (!mail.isBlank()) p.setEmail(mail);
                        pService.update(p);
                        System.out.println("Atualizado.");
                        pause();
                    }
                    case "5" -> { pService.delete(long_("ID: ")); System.out.println("Excluído."); pause(); }
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
                pause();
            }
        }
    }

    private static void menuMedicos() {
        while (true) {
            System.out.println("\n[Médicos]");
            System.out.println("1) Cadastrar");
            System.out.println("2) Listar");
            System.out.println("3) Buscar por CRM");
            System.out.println("4) Atualizar");
            System.out.println("5) Excluir");
            System.out.println("0) Voltar");
            String op = line("> ");
            try {
                switch (op) {
                    case "1" -> {
                        Medico m = new Medico();
                        m.setNome(line("Nome: "));
                        m.setCrm(line("CRM: "));
                        m.setEspecialidade(line("Especialidade: "));
                        m.setTelefone(line("Telefone: "));
                        m.setEmail(line("Email: "));
                        mService.create(m);
                        System.out.println("Criado: " + m);
                        pause();
                    }
                    case "2" -> { mService.listAll().forEach(System.out::println); pause(); }
                    case "3" -> { System.out.println(mRepo.findByCrm(line("CRM: ")).orElse(null)); pause(); }
                    case "4" -> {
                        long id = long_("ID do médico: ");
                        var mOpt = mRepo.findById(id);
                        if (mOpt.isEmpty()) { System.out.println("Não encontrado."); pause(); break; }
                        var m = mOpt.get();
                        var nome = line("Nome [" + m.getNome() + "]: ");         if (!nome.isBlank()) m.setNome(nome);
                        var tel  = line("Telefone [" + m.getTelefone() + "]: "); if (!tel.isBlank())  m.setTelefone(tel);
                        var mail = line("Email [" + m.getEmail() + "]: ");       if (!mail.isBlank()) m.setEmail(mail);
                        var esp  = line("Especialidade [" + m.getEspecialidade() + "]: "); if (!esp.isBlank()) m.setEspecialidade(esp);
                        mService.update(m);
                        System.out.println("Atualizado.");
                        pause();
                    }
                    case "5" -> { mService.delete(long_("ID: ")); System.out.println("Excluído."); pause(); }
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); pause(); }
        }
    }

    private static void menuConsultas() {
        while (true) {
            System.out.println("\n[Consultas]");
            System.out.println("1) Agendar");
            System.out.println("2) Listar");
            System.out.println("3) Cancelar");
            System.out.println("4) Concluir");
            System.out.println("5) Reagendar");
            System.out.println("6) Excluir");
            System.out.println("0) Voltar");
            String op = line("> ");
            try {
                switch (op) {
                    case "1" -> {
                        long pacienteId = long_("Paciente ID: ");
                        long medicoId   = long_("Médico ID: ");
                        var dh  = dateTime("Data/hora (dd/MM/yyyy HH:mm): ");
                        var obs = line("Observações: ");
                        Consulta c = new Consulta();
                        c.setPacienteId(pacienteId);
                        c.setMedicoId(medicoId);
                        c.setDataHora(dh);
                        c.setObservacoes(obs);
                        cService.agendar(c);
                        System.out.println("Agendada: " + c);
                        pause();
                    }
                    case "2" -> { cService.listar().forEach(System.out::println); pause(); }
                    case "3" -> { cService.cancelar(long_("ID da consulta: ")); System.out.println("Cancelada."); pause(); }
                    case "4" -> { cService.concluir(long_("ID da consulta: ")); System.out.println("Concluída."); pause(); }
                    case "5" -> {
                        long id = long_("ID da consulta: ");
                        var nova = dateTime("Nova data/hora (dd/MM/yyyy HH:mm): ");
                        cService.reagendar(id, nova);
                        System.out.println("Reagendada.");
                        pause();
                    }
                    case "6" -> { cService.excluir(long_("ID da consulta: ")); System.out.println("Excluída."); pause(); }
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); pause(); }
        }
    }
}
