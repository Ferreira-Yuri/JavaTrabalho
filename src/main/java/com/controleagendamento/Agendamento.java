package com.controleagendamento;



import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Agendamento {
    private int id;  // Identificador do agendamento
    private String transportador;
    private Date dataAgendamento;
    private String filial;
    private String status;
    private Time horarioAgendamento;
    private String fornecedor; 
    private int volumes;
    private List<String> notasFiscais;

    // Construtor vazio
    public Agendamento() {
        this.notasFiscais = new ArrayList<>(); // Inicializa a lista
    }

    // Construtor com todos os campos
    public Agendamento(int id, List<String> notasFiscais, String transportador, Date dataAgendamento, String filial, String status, Time horarioAgendamento, String fornecedor, int volumes) {
        this.id = id;
        this.transportador = transportador;
        this.dataAgendamento = dataAgendamento;
        this.filial = filial;
        this.status = status;
        this.horarioAgendamento = horarioAgendamento;
        this.fornecedor = fornecedor;
        this.volumes = volumes;
        this.notasFiscais = notasFiscais != null ? notasFiscais : new ArrayList<>(); // Evita NullPointerException
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getNotasFiscais() {
        return notasFiscais;
    }

    public void setNotasFiscais(List<String> notasFiscais) { // Mudança aqui
        this.notasFiscais = notasFiscais != null ? notasFiscais : new ArrayList<>(); // Evita NullPointerException
    }
    
    public String getTransportador() {
        return transportador;
    }

    public void setTransportador(String transportador) {
        this.transportador = transportador;
    }

    public Date getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(Date dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public String getFilial() {
        return filial;
    }

    public void setFilial(String filial) {
        this.filial = filial;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Time getHorarioAgendamento() {
        return horarioAgendamento;
    }

    public void setHorarioAgendamento(Time horarioAgendamento) {
        this.horarioAgendamento = horarioAgendamento;
    }
    
    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public int getVolumes() {
        return volumes;
    }

    public void setVolumes(int volumes) {
        this.volumes = volumes;
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", volumes=" + volumes +
                ", notasFiscais=" + notasFiscais + 
                ", transportador='" + transportador + '\'' +
                ", dataAgendamento=" + dataAgendamento +
                ", filial='" + filial + '\'' +
                ", status='" + status + '\'' +
                ", fornecedor='" + fornecedor + '\'' +
                ", horarioAgendamento=" + horarioAgendamento +
                '}';
    }
}
