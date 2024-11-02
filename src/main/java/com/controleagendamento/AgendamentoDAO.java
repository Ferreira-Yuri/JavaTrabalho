package com.controleagendamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {
    private final Connection connection;

    public AgendamentoDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Método para inserir um novo agendamento
    public void inserirAgendamento(Agendamento agendamento) {
        if (agendamento == null) {
            throw new IllegalArgumentException("Agendamento não pode ser nulo");
        }

        String sqlAgendamento = "INSERT INTO ControleAgendamento (transportador, data_agendamento, filial, status, horario_agendamento, Fornecedor) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlNotasFiscais = "INSERT INTO NotasFiscais (id_agendamento, numero_nota_fiscal) VALUES (?, ?)";

        try {
            connection.setAutoCommit(false); // Iniciar a transação

            try (PreparedStatement stmtAgendamento = connection.prepareStatement(sqlAgendamento,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmtAgendamento.setString(1, agendamento.getTransportador());
                stmtAgendamento.setDate(2, agendamento.getDataAgendamento());
                stmtAgendamento.setString(3, agendamento.getFilial());
                stmtAgendamento.setString(4, agendamento.getStatus());
                stmtAgendamento.setTime(5, agendamento.getHorarioAgendamento());
                stmtAgendamento.setString(6, agendamento.getFornecedor());
                stmtAgendamento.executeUpdate();

                // Obter o ID gerado do agendamento
                ResultSet rs = stmtAgendamento.getGeneratedKeys();
                if (rs.next()) {
                    int idAgendamento = rs.getInt(1);
                    agendamento.setId(idAgendamento);

                    // Inserir as notas fiscais associadas
                    try (PreparedStatement stmtNotas = connection.prepareStatement(sqlNotasFiscais)) {
                        for (String notaFiscal : agendamento.getNotasFiscais()) {
                            stmtNotas.setInt(1, idAgendamento);
                            stmtNotas.setString(2, notaFiscal);
                            stmtNotas.addBatch(); // Adicionar ao batch
                        }
                        stmtNotas.executeBatch(); // Executar todas as inserções
                    }
                }

                connection.commit(); // Commit da transação

            } catch (SQLException e) {
                connection.rollback(); // Reverter a transação em caso de erro
                throw new RuntimeException("Erro ao inserir agendamento: " + e.getMessage(), e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na transação: " + e.getMessage(), e);
        }
    }

    // Método para listar todos os agendamentos
    public List<Agendamento> listarAgendamentos() {
        List<Agendamento> agendamentos = new ArrayList<>();
        String queryAgendamentos = "SELECT * FROM ControleAgendamento";
        String queryNotasFiscais = "SELECT numero_nota_fiscal FROM NotasFiscais WHERE id_agendamento = ?";

        try (PreparedStatement statementAgendamentos = connection.prepareStatement(queryAgendamentos);
                ResultSet resultSetAgendamentos = statementAgendamentos.executeQuery()) {

            while (resultSetAgendamentos.next()) {
                Agendamento agendamento = new Agendamento();
                agendamento.setId(resultSetAgendamentos.getInt("id"));
                agendamento.setTransportador(resultSetAgendamentos.getString("transportador"));
                agendamento.setDataAgendamento(resultSetAgendamentos.getDate("data_agendamento"));
                agendamento.setFilial(resultSetAgendamentos.getString("filial"));
                agendamento.setStatus(resultSetAgendamentos.getString("status"));
                agendamento.setFornecedor(resultSetAgendamentos.getString("Fornecedor"));
                agendamento.setHorarioAgendamento(resultSetAgendamentos.getTime("horario_agendamento"));

                // Consultar as notas fiscais associadas a esse agendamento
                try (PreparedStatement stmtNotasFiscais = connection.prepareStatement(queryNotasFiscais)) {
                    stmtNotasFiscais.setInt(1, agendamento.getId());
                    try (ResultSet rsNotas = stmtNotasFiscais.executeQuery()) {
                        List<String> notasFiscais = new ArrayList<>();
                        while (rsNotas.next()) {
                            notasFiscais.add(rsNotas.getString("numero_nota_fiscal"));
                        }
                        agendamento.setNotasFiscais(notasFiscais);
                    }
                }

                agendamentos.add(agendamento);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendamentos: " + e.getMessage(), e);
        }

        return agendamentos;
    }

    public Agendamento buscarAgendamentoPorId(int id) throws SQLException {
        String sql = "SELECT * FROM ControleAgendamento WHERE id = ?";
        String sqlNotasFiscais = "SELECT numero_nota_fiscal FROM NotasFiscais WHERE id_agendamento = ?";
        Agendamento agendamento = null;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                PreparedStatement statementNotas = connection.prepareStatement(sqlNotasFiscais)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                agendamento = new Agendamento();
                agendamento.setId(resultSet.getInt("id"));
                agendamento.setFilial(resultSet.getString("filial"));
                agendamento.setDataAgendamento(resultSet.getDate("data_agendamento"));
                agendamento.setFornecedor(resultSet.getString("fornecedor"));
                agendamento.setTransportador(resultSet.getString("transportador"));
                agendamento.setHorarioAgendamento(resultSet.getTime("horario_agendamento"));
                agendamento.setStatus(resultSet.getString("status"));
                // agendamento.setVolumes(resultSet.getInt("volumes"));

                // Busca as notas fiscais relacionadas
                statementNotas.setInt(1, id);
                ResultSet resultSetNotas = statementNotas.executeQuery();
                List<String> notasFiscais = new ArrayList<>();
                while (resultSetNotas.next()) {
                    notasFiscais.add(resultSetNotas.getString("numero_nota_fiscal"));
                }
                agendamento.setNotasFiscais(notasFiscais);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Adicionado para depuração
            throw new SQLException("Erro ao buscar agendamento por ID", e);
        }

        return agendamento;
    }

    // Método para atualizar um agendamento
    public void atualizarAgendamento(Agendamento agendamento) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ControleAgendamento SET ");
        List<Object> parametros = new ArrayList<>();
    
        // Atualiza os campos do agendamento, se eles estiverem preenchidos
        if (agendamento.getTransportador() != null && !agendamento.getTransportador().isEmpty()) {
            sql.append("transportador = ?, ");
            parametros.add(agendamento.getTransportador());
        }
        if (agendamento.getFornecedor() != null && !agendamento.getFornecedor().isEmpty()) {
            sql.append("fornecedor = ?, ");
            parametros.add(agendamento.getFornecedor());
        }
        if (agendamento.getDataAgendamento() != null) {
            sql.append("data_agendamento = ?, ");
            parametros.add(agendamento.getDataAgendamento());
        }
        if (agendamento.getFilial() != null && !agendamento.getFilial().isEmpty()) {
            sql.append("filial = ?, ");
            parametros.add(agendamento.getFilial());
        }
        if (agendamento.getStatus() != null && !agendamento.getStatus().isEmpty()) {
            sql.append("status = ?, ");
            parametros.add(agendamento.getStatus());
        }
        if (agendamento.getHorarioAgendamento() != null) {
            sql.append("horario_agendamento = ?, ");
            parametros.add(agendamento.getHorarioAgendamento());
        }
    
        // Remove a última vírgula e espaço da string SQL
        if (sql.toString().endsWith(", ")) {
            sql.setLength(sql.length() - 2);
        }
    
        // Adiciona a condição WHERE (precisamos do ID para atualizar o registro correto)
        sql.append(" WHERE id = ?");
        parametros.add(agendamento.getId());
    
        // Prepara e executa a consulta de atualização do agendamento
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
    
            // Configura os parâmetros na ordem correta
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
    
            // Executa o UPDATE
            stmt.executeUpdate();
        }
    
        // Atualiza as notas fiscais relacionadas, se necessário
        atualizarNotasFiscais(agendamento);
    }
    
    private void atualizarNotasFiscais(Agendamento agendamento) throws SQLException {
        // Primeiro, removemos todas as notas fiscais associadas
        String sqlDelete = "DELETE FROM NotasFiscais WHERE id_agendamento = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
            stmt.setInt(1, agendamento.getId());
            stmt.executeUpdate();
        }
    
        // Em seguida, inserimos as novas notas fiscais, se houver
        if (agendamento.getNotasFiscais() != null && !agendamento.getNotasFiscais().isEmpty()) {
            String sqlInsert = "INSERT INTO NotasFiscais (id_agendamento, numero_nota_fiscal) VALUES (?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
    
                for (String notaFiscal : agendamento.getNotasFiscais()) {
                    stmt.setInt(1, agendamento.getId());
                    stmt.setString(2, notaFiscal);
                    stmt.executeUpdate();
                }
            }
        }
    }
    

    // Método para excluir um agendamento
    public boolean excluirAgendamento(int id) {
        String sql = "DELETE FROM ControleAgendamento WHERE id=?";
        boolean sucesso = false;
    
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            
            // Se pelo menos uma linha foi afetada, a exclusão foi bem-sucedida
            sucesso = (linhasAfetadas > 0);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir agendamento: " + e.getMessage(), e);
        }
    
        return sucesso;
    }
    
}

 
