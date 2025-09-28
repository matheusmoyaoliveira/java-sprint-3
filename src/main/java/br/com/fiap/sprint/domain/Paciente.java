package br.com.fiap.sprint.domain;

import br.com.fiap.sprint.util.Formatters;
import java.time.LocalDate;

public class Paciente {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;

    public Paciente() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        String nascFmt = (dataNascimento != null) ? Formatters.DATE.format(dataNascimento) : null;
        String cpfFmt  = Formatters.cpf(cpf);
        String foneFmt = Formatters.phoneBR(telefone);

        return "Paciente{"
                + "id=" + id
                + ", nome='" + nome + '\''
                + ", cpf='" + cpfFmt + '\''
                + ", dataNascimento=" + nascFmt
                + ", telefone='" + foneFmt + '\''
                + ", email='" + email + '\''
                + '}';
    }
}
