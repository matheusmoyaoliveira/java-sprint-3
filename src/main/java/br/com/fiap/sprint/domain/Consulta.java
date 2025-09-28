package br.com.fiap.sprint.domain;

import br.com.fiap.sprint.util.Formatters;

import java.time.LocalDateTime;

public class Consulta {
    private long id;
    private long pacienteId;
    private long medicoId;
    private LocalDateTime dataHora;
    private StatusConsulta status;
    private String observacoes;

    public Consulta(long id, long pacienteId, long medicoId, LocalDateTime dataHora, String observacoes) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataHora = dataHora;
        this.observacoes = observacoes;
    }

    public Consulta() {
    }

    @Override
    public String toString() {

        String dh = (dataHora != null) ? Formatters.DATETIME.format(dataHora) : null;

        return "Consulta{"
                + "id=" + id
                + ", pacienteId=" + pacienteId
                + ", medicoId=" + medicoId
                + ", dataHora=" + dh
                + ", status=" + status
                + ", obs='" + observacoes + '\''
                + '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(long medicoId) {
        this.medicoId = medicoId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
