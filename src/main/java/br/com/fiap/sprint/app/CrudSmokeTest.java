package br.com.fiap.sprint.app;

import br.com.fiap.sprint.domain.Consulta;
import br.com.fiap.sprint.domain.Medico;
import br.com.fiap.sprint.domain.Paciente;
import br.com.fiap.sprint.repository.*;
import br.com.fiap.sprint.service.ConsultaService;
import br.com.fiap.sprint.service.MedicoService;
import br.com.fiap.sprint.service.PacienteService;
import br.com.fiap.sprint.util.DbConnection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class CrudSmokeTest {

    private static final Random R = new Random();

    private static String randomCpf() {
        return String.format("%03d.%03d.%03d-%02d",
                R.nextInt(1000), R.nextInt(1000), R.nextInt(1000), R.nextInt(100));
    }
    private static String randomCrm() {
        return "CRM-SP " + (100000 + R.nextInt(900000));
    }

    public static void main(String[] args) {
        System.out.println("Ping DB: " + DbConnection.ping());

        PacienteRepository pRepo = new PacienteRepositoryJdbc();
        MedicoRepository   mRepo = new MedicoRepositoryJdbc();
        ConsultaRepository cRepo = new ConsultaRepositoryJdbc();

        PacienteService pService = new PacienteService(pRepo);
        MedicoService   mService = new MedicoService(mRepo);
        ConsultaService cService = new ConsultaService(cRepo, pRepo, mRepo);

        Paciente p = new Paciente();
        p.setNome("Paciente Teste");
        p.setCpf(randomCpf());
        p.setDataNascimento(LocalDate.of(1990, 1, 10));
        p.setTelefone("(11)90000-0000");
        p.setEmail("paciente.teste@exemplo.com");
        pService.create(p);
        System.out.println("PACIENTE CREATE: " + (p.getId() > 0 ? "PASS" : "FAIL") + " -> " + p);

        var pByCpf = pRepo.findByCpf(p.getCpf());
        System.out.println("PACIENTE READ (by CPF): " + (pByCpf.isPresent() ? "PASS" : "FAIL"));

        p.setTelefone("(11)91111-1111");
        pRepo.update(p);
        var pReload = pRepo.findById(p.getId());
        System.out.println("PACIENTE UPDATE: " +
                (pReload.isPresent() && "(11)91111-1111".equals(pReload.get().getTelefone()) ? "PASS" : "FAIL"));

        Medico m = new Medico();
        m.setNome("Medico Teste");
        m.setCrm(randomCrm());
        m.setEspecialidade("Clínico Geral");
        m.setTelefone("(11)98888-2222");
        m.setEmail("medico.teste@exemplo.com");
        mService.create(m);
        System.out.println("MEDICO CREATE: " + (m.getId() > 0 ? "PASS" : "FAIL") + " -> " + m);

        var mByCrm = mRepo.findByCrm(m.getCrm());
        System.out.println("MEDICO READ (by CRM): " + (mByCrm.isPresent() ? "PASS" : "FAIL"));

        m.setEspecialidade("Cardiologia");
        mRepo.update(m);
        var mReload = mRepo.findById(m.getId());
        System.out.println("MEDICO UPDATE: " +
                (mReload.isPresent() && "Cardiologia".equals(mReload.get().getEspecialidade()) ? "PASS" : "FAIL"));

        LocalDateTime dh = LocalDateTime.now().plusDays(1)
                .withHour(14).withMinute(0).withSecond(0).withNano(0);
        while (cRepo.existsChoqueAgenda(m.getId(), dh)) dh = dh.plusHours(1);

        Consulta c = new Consulta();
        c.setPacienteId(p.getId());
        c.setMedicoId(m.getId());
        c.setDataHora(dh);
        c.setObservacoes("Consulta de teste");
        try {
            cService.agendar(c);
            System.out.println("CONSULTA CREATE: " + (c.getId() > 0 ? "PASS" : "FAIL") + " -> " + c);
        } catch (Exception e) {
            System.out.println("CONSULTA CREATE: FAIL -> " + e.getMessage());
        }

        var totalAntes = cService.listar().size();
        System.out.println("CONSULTA READ (list): " + (totalAntes > 0 ? "PASS" : "FAIL") + " -> total=" + totalAntes);

        try {
            cService.reagendar(c.getId(), c.getDataHora().plusHours(1));
            var cReload = cRepo.findById(c.getId()).orElseThrow();
            boolean ok = cReload.getDataHora().equals(c.getDataHora().plusHours(1));
            System.out.println("CONSULTA UPDATE (reagendar): " + (ok ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("CONSULTA UPDATE (reagendar): FAIL -> " + e.getMessage());
        }

        try {
            cService.cancelar(c.getId());
            System.out.println("CONSULTA CANCELAR: PASS");
        } catch (Exception e) {
            System.out.println("CONSULTA CANCELAR: FAIL -> " + e.getMessage());
        }

        try {
            cService.excluir(c.getId());
            var existe = cRepo.findById(c.getId()).isPresent();
            System.out.println("CONSULTA DELETE: " + (!existe ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("CONSULTA DELETE: FAIL -> " + e.getMessage());
        }

        try {
            mRepo.delete(m.getId());
            var mGone = mRepo.findById(m.getId()).isEmpty();
            System.out.println("MEDICO DELETE: " + (mGone ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("MEDICO DELETE: FAIL -> " + e.getMessage());
        }

        try {
            pRepo.delete(p.getId());
            var pGone = pRepo.findById(p.getId()).isEmpty();
            System.out.println("PACIENTE DELETE: " + (pGone ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("PACIENTE DELETE: FAIL -> " + e.getMessage());
        }

        System.out.println("\nSmoke test finalizado.");
    }
}
