package br.com.fiap.sprint.app;

import br.com.fiap.sprint.domain.Consulta;
import br.com.fiap.sprint.domain.Medico;
import br.com.fiap.sprint.domain.Paciente;
import br.com.fiap.sprint.domain.StatusConsulta;
import br.com.fiap.sprint.repository.ConsultaRepositoryJdbc;
import br.com.fiap.sprint.repository.MedicoRepositoryJdbc;
import br.com.fiap.sprint.repository.PacienteRepositoryJdbc;
import br.com.fiap.sprint.service.ConsultaService;
import br.com.fiap.sprint.service.MedicoService;
import br.com.fiap.sprint.service.PacienteService;
import br.com.fiap.sprint.util.DbConnection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static br.com.fiap.sprint.util.Formatters.DATETIME;

public class CrudSmokeTest {

    private static final PacienteRepositoryJdbc pRepo = new PacienteRepositoryJdbc();
    private static final MedicoRepositoryJdbc mRepo = new MedicoRepositoryJdbc();
    private static final ConsultaRepositoryJdbc cRepo = new ConsultaRepositoryJdbc();

    private static final PacienteService pService = new PacienteService(pRepo);
    private static final MedicoService mService = new MedicoService(mRepo);
    private static final ConsultaService cService = new ConsultaService(cRepo, pRepo, mRepo);

    public static void main(String[] args) {
        System.out.println("Ping DB: " + DbConnection.ping());

        Paciente p = new Paciente();
        p.setNome("Paciente Teste");
        p.setCpf("900.856.232-21");
        p.setDataNascimento(LocalDate.of(1990, 1, 10));
        p.setTelefone("(11)90000-0000");
        p.setEmail("paciente.teste@exemplo.com");

        try {
            pService.create(p);
            ok("PACIENTE CREATE", p.getId() > 0, "-> " + p);
        } catch (Exception e) {
            fail("PACIENTE CREATE", "-> " + e.getMessage());
            return;
        }

        ok("PACIENTE READ (by CPF)", pRepo.findByCpf(p.getCpf()).isPresent(), null);

        p.setTelefone("(11)91111-1111");
        p.setEmail("paciente.up@exemplo.com");
        try {
            pService.update(p);
            var pr = pRepo.findById(p.getId()).orElseThrow();
            boolean ok = pr.getTelefone().equals(p.getTelefone())
                    && pr.getEmail().equals(p.getEmail());
            ok("PACIENTE UPDATE", ok, "esperado=" + p + " | lido=" + pr);
        } catch (Exception e) {
            fail("PACIENTE UPDATE", "-> " + e.getMessage());
        }


        Medico m = new Medico();
        m.setNome("Medico Teste");
        m.setCrm("CRM-SP " + (500000 + (int)(System.currentTimeMillis() % 999))); // evitar UNIQUE
        m.setEspecialidade("Clínico Geral");
        m.setTelefone("(11)98888-2222");
        m.setEmail("medico.teste@exemplo.com");

        try {
            mService.create(m);
            ok("MEDICO CREATE", m.getId() > 0, "-> " + m);
        } catch (Exception e) {
            fail("MEDICO CREATE", "-> " + e.getMessage());
            return;
        }

        ok("MEDICO READ (by CRM)", mRepo.findByCrm(m.getCrm()).isPresent(), null);

        m.setTelefone("(11)97777-7777");
        try {
            mService.update(m);
            var mr = mRepo.findById(m.getId()).orElseThrow();
            ok("MEDICO UPDATE", mr.getTelefone().equals(m.getTelefone()),
                    "esperado=" + m + " | lido=" + mr);
        } catch (Exception e) {
            fail("MEDICO UPDATE", "-> " + e.getMessage());
        }


        Consulta c1 = new Consulta();
        c1.setPacienteId(p.getId());
        c1.setMedicoId(m.getId());
        c1.setDataHora(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0));
        c1.setObservacoes("Consulta de teste");

        try {
            cService.agendar(c1);
            ok("CONSULTA CREATE", c1.getId() > 0, "-> " + c1);
        } catch (Exception e) {
            fail("CONSULTA CREATE", "-> " + e.getMessage());
        }

        var lista = cService.listar();
        ok("CONSULTA READ (list)", lista.size() >= 1, "total=" + lista.size());

        try {

            var nova = c1.getDataHora().plusHours(1).withSecond(0).withNano(0);
            cService.reagendar(c1.getId(), nova);

            var cr = cRepo.findById(c1.getId()).orElseThrow();

            var esperado = nova.truncatedTo(ChronoUnit.MINUTES);
            var lido = cr.getDataHora().truncatedTo(ChronoUnit.MINUTES);

            ok("CONSULTA UPDATE (reagendar)", esperado.equals(lido),
                    "esperado=" + DATETIME.format(esperado) + " | lido=" + DATETIME.format(lido));
        } catch (Exception e) {
            fail("CONSULTA UPDATE (reagendar)", "-> " + e.getMessage());
        }

        try {
            cService.cancelar(c1.getId());
            var cr = cRepo.findById(c1.getId()).orElseThrow();
            ok("CONSULTA CANCELAR", cr.getStatus() == StatusConsulta.CANCELADA,
                    "status=" + cr.getStatus());
        } catch (Exception e) {
            fail("CONSULTA CANCELAR", "-> " + e.getMessage());
        }


        Consulta c2 = new Consulta();
        c2.setPacienteId(p.getId());
        c2.setMedicoId(m.getId());
        c2.setDataHora(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0));
        c2.setObservacoes("Consulta para concluir");

        try {
            cService.agendar(c2);
            ok("CONSULTA CREATE (c2)", c2.getId() > 0, "-> " + c2);
        } catch (Exception e) {
            fail("CONSULTA CREATE (c2)", "-> " + e.getMessage());
        }

        try {
            cService.concluir(c2.getId());
            var cr2 = cRepo.findById(c2.getId()).orElseThrow();
            ok("CONSULTA CONCLUIR", cr2.getStatus() == StatusConsulta.CONCLUIDA,
                    "status=" + cr2.getStatus());
        } catch (Exception e) {
            fail("CONSULTA CONCLUIR", "-> " + e.getMessage());
        }


        try {
            cService.excluir(c1.getId());
            ok("CONSULTA DELETE (c1)", cRepo.findById(c1.getId()).isEmpty(), null);
        } catch (Exception e) {
            fail("CONSULTA DELETE (c1)", "-> " + e.getMessage());
        }

        try {
            cService.excluir(c2.getId());
            ok("CONSULTA DELETE (c2)", cRepo.findById(c2.getId()).isEmpty(), null);
        } catch (Exception e) {
            fail("CONSULTA DELETE (c2)", "-> " + e.getMessage());
        }

        try {
            mService.delete(m.getId());
            ok("MEDICO DELETE", mRepo.findById(m.getId()).isEmpty(), null);
        } catch (Exception e) {
            fail("MEDICO DELETE", "-> " + e.getMessage());
        }

        try {
            pService.delete(p.getId());
            ok("PACIENTE DELETE", pRepo.findById(p.getId()).isEmpty(), null);
        } catch (Exception e) {
            fail("PACIENTE DELETE", "-> " + e.getMessage());
        }
    }


    private static void ok(String label, boolean condition, String detail) {
        System.out.println(label + ": " + (condition ? "PASS" : "FAIL")
                + (detail != null ? " -> " + detail : ""));
    }

    private static void fail(String label, String detail) {
        System.out.println(label + ": FAIL " + (detail != null ? detail : ""));
    }
}
