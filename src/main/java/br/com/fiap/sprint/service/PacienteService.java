package br.com.fiap.sprint.service;

import br.com.fiap.sprint.domain.Paciente;
import br.com.fiap.sprint.repository.PacienteRepository;

import java.util.List;
import java.util.Optional;

public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    private static String digits(String s) {
        return s == null ? null : s.replaceAll("\\D", "");
    }

    private static void normalize(Paciente p) {
        p.setCpf(digits(p.getCpf()));
        p.setTelefone(digits(p.getTelefone()));
    }

    public Paciente create(Paciente p) {
        normalize(p);
        if (repository.findByCpf(p.getCpf()).isPresent())
            throw new IllegalArgumentException("CPF já cadastrado");
        return repository.save(p);
    }

    public void update(Paciente p) {
        normalize(p);
        repository.update(p);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public Optional<Paciente> findByCpf(String cpf) {
        return repository.findByCpf(digits(cpf));
    }

    public Optional<Paciente> findById(Long id) { return repository.findById(id); }

    public List<Paciente> listAll() { return repository.findAll(); }
}
