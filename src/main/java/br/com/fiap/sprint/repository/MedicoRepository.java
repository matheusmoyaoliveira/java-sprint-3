package br.com.fiap.sprint.repository;

import br.com.fiap.sprint.domain.Medico;
import java.util.List;
import java.util.Optional;

public interface MedicoRepository {
    Medico save(Medico m);
    void update(Medico m);
    void delete(Long id);
    Optional<Medico> findById(Long id);
    Optional<Medico> findByCrm(String crm);
    List<Medico> findAll();
}
