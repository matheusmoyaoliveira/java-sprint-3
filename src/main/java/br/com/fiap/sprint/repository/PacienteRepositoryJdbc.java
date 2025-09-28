package br.com.fiap.sprint.repository;

import br.com.fiap.sprint.domain.Paciente;
import br.com.fiap.sprint.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacienteRepositoryJdbc implements PacienteRepository{

    private Paciente mapRow(ResultSet rs) throws Exception {
        Paciente p = new Paciente();
        p.setId(rs.getLong("id"));
        p.setNome(rs.getString("nome"));
        p.setCpf(rs.getString("cpf"));

        java.sql.Date dn = rs.getDate("data_nasc");
        if (dn != null) p.setDataNascimento(dn.toLocalDate());

        p.setTelefone(rs.getString("telefone"));
        p.setEmail(rs.getString("email"));
        return p;
    }

    @Override
    public Paciente save(Paciente p) {
        final String sql = "INSERT INTO paciente (nome, cpf, data_nasc, telefone, email) VALUES (?,?,?,?,?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getCpf());
            ps.setDate(3, (p.getDataNascimento() != null) ? java.sql.Date.valueOf(p.getDataNascimento()) : null);
            ps.setString(4, p.getTelefone());
            ps.setString(5, p.getEmail());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys != null && keys.next()) {
                    p.setId(keys.getLong(1));
                } else {
                    findByCpf(p.getCpf()).ifPresent(db -> p.setId(db.getId()));
                }
            } catch ( Exception ignore) {
                findByCpf(p.getCpf()).ifPresent(db -> p.setId(db.getId()));
            }
            return p;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar paciente", e);
        }
    }

    @Override
    public void update(Paciente p) {
        final String sql = """
                UPDATE paciente
                  SET nome = ?, cpf = ?, data_nasc = ?, telefone = ?, email = ?
                 WHERE id = ?
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getCpf());
            ps.setDate(3, (p.getDataNascimento()!=null) ? java.sql.Date.valueOf(p.getDataNascimento()) : null);
            ps.setString(4, p.getTelefone());
            ps.setString(5, p.getEmail());
            ps.setLong(6, p.getId());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar paciente", e);
        }

    }

    @Override
    public void delete(Long id) {
        final String sql = "DELETE FROM paciente WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir paciente", e);
        }

    }

    @Override
    public Optional<Paciente> findById(Long id) {
        final String sql = "SELECT id, nome, cpf, data_nasc, telefone, email FROM paciente WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar paciente por ID", e);
        }
    }

    @Override
    public Optional<Paciente> findByCpf(String cpf) {
        final String sql = "SELECT id, nome, cpf, data_nasc, telefone, email FROM paciente WHERE cpf = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar paciente por CPF", e);
        }

    }

    @Override
    public List<Paciente> findAll() {
        final String sql = "SELECT id, nome, cpf, data_nasc, telefone, email FROM paciente ORDER BY id";
        List<Paciente> lista = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar pacientes", e);
        }
    }
}
