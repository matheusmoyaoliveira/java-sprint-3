package br.com.fiap.sprint.service;

import br.com.fiap.sprint.domain.Consulta;
import br.com.fiap.sprint.domain.StatusConsulta;
import br.com.fiap.sprint.repository.ConsultaRepository;
import br.com.fiap.sprint.repository.MedicoRepository;
import br.com.fiap.sprint.repository.PacienteRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ConsultaService {

    private final ConsultaRepository consultaRepo;
    private final PacienteRepository pacienteRepo;
    private final MedicoRepository medicoRepo;

    public ConsultaService(ConsultaRepository consultaRepo,
                           PacienteRepository pacienteRepo,
                           MedicoRepository medicoRepo) {
        this.consultaRepo = consultaRepo;
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo = medicoRepo;
    }

    private void ensureExists(long id) {
        if (consultaRepo.findById(id).isEmpty())
            throw new IllegalArgumentException("Consulta não encontrada");
    }

    public Consulta agendar(Consulta c) {

        if (pacienteRepo.findById(c.getPacienteId()).isEmpty())
            throw new IllegalArgumentException("Paciente não encontrado");
        if (medicoRepo.findById(c.getMedicoId()).isEmpty())
            throw new IllegalArgumentException("Médico não encontrado");
        if (consultaRepo.existsChoqueAgenda(c.getMedicoId(), c.getDataHora()))
            throw new IllegalArgumentException("Choque de agenda para o médico neste horário");


        c.setStatus(StatusConsulta.AGENDADA);

        return consultaRepo.save(c);
    }

    public void cancelar(long id) {
        ensureExists(id);
        consultaRepo.updateStatus(id, StatusConsulta.CANCELADA);
    }

    public void concluir(long id) {
        ensureExists(id);
        consultaRepo.updateStatus(id, StatusConsulta.REALIZADA);
    }

    public void reagendar(long id, LocalDateTime novoHorario) {
        var opt = consultaRepo.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Consulta não encontrada");
        var c = opt.get();

        if (consultaRepo.existsChoqueAgenda(c.getMedicoId(), novoHorario))
            throw new IllegalArgumentException("Choque de agenda para o médico no novo horário");

        c.setDataHora(novoHorario);
        consultaRepo.update(c);
    }

    public void excluir(long id) {
        ensureExists(id);
        consultaRepo.delete(id);
    }

    public List<Consulta> listar() {
        return consultaRepo.findAll();
    }
}
