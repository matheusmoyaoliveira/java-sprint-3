package br.com.fiap.sprint.service;

import br.com.fiap.sprint.domain.Medico;
import br.com.fiap.sprint.repository.MedicoRepository;

import java.util.List;
import java.util.Optional;

public class MedicoService {

    private final MedicoRepository repository;

    public MedicoService(MedicoRepository repository) {
        this.repository = repository;
    }

    private static String digits(String s) {
        return s == null ? null : s.replaceAll("\\D", "");
    }

    private static void normalize(Medico m) {
        m.setTelefone(digits(m.getTelefone()));
    }

    public Medico create(Medico m) {
        normalize(m);
        if (repository.findByCrm(m.getCrm()).isPresent())
            throw new IllegalArgumentException("CRM já cadastrado");
        return repository.save(m);
    }

    public void update(Medico m) {
        normalize(m);
        repository.update(m);
    }

    public void delete(Long id) { repository.delete(id); }

    public Optional<Medico> findByCrm(String crm) {
        return repository.findByCrm(crm);
    }

    public Optional<Medico> findById(Long id) {
        return repository.findById(id);
    }

    public List<Medico> listAll() {
        return repository.findAll();
    }
}
