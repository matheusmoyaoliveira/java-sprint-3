package br.com.fiap.sprint.repository;

import br.com.fiap.sprint.domain.Paciente;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository {
    Paciente save(Paciente p);
    void update(Paciente p);
    void delete(Long id);
    Optional<Paciente> findById(Long id);
    Optional<Paciente> findByCpf(String cpf);
    List<Paciente> findAll();

}
