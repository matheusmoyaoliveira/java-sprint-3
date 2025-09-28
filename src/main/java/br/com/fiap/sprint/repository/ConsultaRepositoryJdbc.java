package br.com.fiap.sprint.repository;

import br.com.fiap.sprint.domain.Consulta;
import br.com.fiap.sprint.domain.StatusConsulta;
import br.com.fiap.sprint.util.DbConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultaRepositoryJdbc implements ConsultaRepository {

    private Consulta mapRow(ResultSet rs) throws Exception {
        Consulta c = new Consulta();
        c.setId(rs.getLong("id"));
        c.setPacienteId(rs.getLong("paciente_id"));
        c.setMedicoId(rs.getLong("medico_id"));

        Timestamp ts = rs.getTimestamp("data_hora");
        c.setDataHora(ts != null ? ts.toLocalDateTime() : null);
        c.setStatus(StatusConsulta.valueOf(rs.getString("status")));
        c.setObservacoes(rs.getString("observacoes"));

        return c;
    }

    @Override
    public Consulta save(Consulta c) {
        final String sql = """
                INSERT INTO consulta (paciente_id, medico_id, data_hora, status, observacoes)
                VALUES (?,?,?,?,?)
                """;
        try (Connection conn = DbConnection.getConnection();

             PreparedStatement ps = conn.prepareStatement(sql, new String[] {"ID"})) {

            ps.setLong(1, c.getPacienteId());
            ps.setLong(2, c.getMedicoId());
            ps.setTimestamp(3, Timestamp.valueOf(c.getDataHora())); // garante TIMESTAMP
            ps.setString(4, c.getStatus().name());
            ps.setString(5, c.getObservacoes());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys != null && keys.next()) c.setId(keys.getLong(1));
            }
            return c;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar consulta", e);
        }
    }

    @Override
    public void update(Consulta c) {
        final String sql = """
        UPDATE consulta
           SET paciente_id = ?, medico_id = ?, data_hora = ?, status = ?, observacoes = ?
         WHERE id = ?
        """;
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, c.getPacienteId());
            ps.setLong(2, c.getMedicoId());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(c.getDataHora()));
            ps.setString(4, c.getStatus().name());
            ps.setString(5, c.getObservacoes());
            ps.setLong(6, c.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar consulta: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        final String sql = "DELETE FROM consulta WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir consulta: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateStatus(long id, StatusConsulta status) {
        final String sql = "UPDATE consulta SET status = ? WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setLong(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status da consulta", e);
        }
    }

    @Override
    public Optional<Consulta> findById(long id) {
        final String sql = "SELECT id, paciente_id, medico_id, data_hora, status, observacoes FROM consulta WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar consulta por ID", e);
        }
    }

    @Override
    public List<Consulta> findAll() {
        final String sql = "SELECT id, paciente_id, medico_id, data_hora, status, observacoes FROM consulta ORDER BY data_hora";

        List<Consulta> lista = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapRow(rs));
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar consultas", e);
        }
    }

    @Override
    public boolean existsChoqueAgenda(long medicoId, LocalDateTime dataHora) {
        final String sql = "SELECT 1 FROM consulta WHERE medico_id = ? AND data_hora = ? AND status = 'AGENDADA'";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, medicoId);
            ps.setTimestamp(2, Timestamp.valueOf(dataHora));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar choque de agenda", e);
        }
    }
}
