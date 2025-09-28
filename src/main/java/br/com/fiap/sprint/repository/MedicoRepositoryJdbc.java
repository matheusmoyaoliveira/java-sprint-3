package br.com.fiap.sprint.repository;

import br.com.fiap.sprint.domain.Medico;
import br.com.fiap.sprint.util.DbConnection;
import com.sun.source.tree.TryTree;
import oracle.jdbc.proxy.annotation.Pre;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicoRepositoryJdbc implements MedicoRepository {

    private Medico mapRow(ResultSet rs) throws Exception {
        Medico m = new Medico();
        m.setId(rs.getLong("id"));
        m.setNome((rs.getString("nome")));
        m.setCrm(rs.getString("crm"));
        m.setEspecialidade(rs.getString("especialidade"));
        m.setTelefone(rs.getString("telefone"));
        m.setEmail(rs.getString("email"));
        return m;
    }

    @Override
    public Medico save(Medico m) {
        final String sql = "INSERT INTO medico (nome, crm, especialidade, telefone, email) VALUES (?,?,?,?,?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getNome());
            ps.setString(2, m.getCrm());
            ps.setString(3, m.getEspecialidade());
            ps.setString(4, m.getTelefone());
            ps.setString(5, m.getEmail());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys != null && keys.next()) {
                    m.setId(keys.getLong(1));
                } else {
                    findByCrm(m.getCrm()).ifPresent(db -> m.setId(db.getId()));
                }
            } catch (Exception ignore) {
                findByCrm(m.getCrm()).ifPresent(db -> m.setId(db.getId()));
            }
            return m;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar médico", e);
        }
    }

    @Override
    public void update(Medico m) {
        final String sql = """
                UPDATE medico
                  SET nome = ?, crm = ?, especialidade = ?, telefone = ?, email = ?
                WHERE id = ?
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNome());
            ps.setString(2, m.getCrm());
            ps.setString(3, m.getEspecialidade());
            ps.setString(4, m.getTelefone());
            ps.setString(5, m.getEmail());
            ps.setLong(6, m.getId());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar médico", e);
        }
    }

    @Override
    public void delete(Long id) {
        final String sql = "DELETE FROM medico WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir médico", e);
        }
    }

    @Override
    public Optional<Medico> findById(Long id) {
        final String sql = "SELECT id, nome, crm, especialidade, telefone, email FROM medico WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar médico por ID");
        }
    }

    @Override
    public Optional<Medico> findByCrm(String crm) {
        final String sql = "SELECT id, nome, crm, especialidade, telefone, email FROM medico WHERE crm = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, crm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar médico por CRM", e);
        }
    }

    @Override
    public List<Medico> findAll() {
        final String sql = "SELECT id, nome, crm, especialidade, telefone, email FROM medico ORDER BY id";

        List<Medico> lista = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapRow(rs));
            return lista;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar médicos", e);
        }
    }
}
