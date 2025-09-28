package br.com.fiap.sprint.repository;

import br.com.fiap.sprint.domain.Consulta;
import br.com.fiap.sprint.domain.StatusConsulta;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;


public interface ConsultaRepository {
    Consulta save(Consulta c);
    void update(Consulta c);
    void updateStatus(long id, StatusConsulta s);
    void delete(long id);
    Optional<Consulta> findById(long id);
    List<Consulta> findAll();
    boolean existsChoqueAgenda(long medicoId, LocalDateTime dataHora);
}
