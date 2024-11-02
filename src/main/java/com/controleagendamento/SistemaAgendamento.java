package com.controleagendamento;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class SistemaAgendamento {

    private AutenticacaoDAO autenticacaoDAO;
    private JTextArea textArea;
    private JFrame frame;

    // Construtor principal
    public SistemaAgendamento() {
        this.autenticacaoDAO = new AutenticacaoDAO();
    }

    // Construtor para a tela principal
    public SistemaAgendamento(JTextArea textArea, JFrame frame) {
        this.textArea = textArea;
        this.frame = frame;
        this.autenticacaoDAO = new AutenticacaoDAO();
    }

    // Método para mostrar a tela de login
    public void mostrarTelaLogin() {
        JFrame frame = new JFrame("Login do Sistema");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel userLabel = new JLabel("Nome de Usuário:");
        JTextField userTextField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Senha:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Criar Usuário");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeUsuario = userTextField.getText();
                String senha = new String(passwordField.getPassword());

                // Chama o método de autenticação no AutenticacaoDAO
                if (autenticacaoDAO.validarUsuario(nomeUsuario, senha)) {
                    JOptionPane.showMessageDialog(frame, "Login bem-sucedido!");
                    frame.dispose();
                    criarTelaPrincipal(); // Chama a tela principal após o login bem-sucedido
                } else {
                    JOptionPane.showMessageDialog(frame, "Falha no login. Verifique suas credenciais.");
                }
            }
        });

        // Adiciona ação para o botão de criar usuário
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarTelaCadastroUsuario();
            }
        });

        frame.add(userLabel);
        frame.add(userTextField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(new JLabel());
        frame.add(loginButton);
        frame.add(new JLabel());
        frame.add(registerButton);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Método para mostrar a tela de cadastro de usuário
    public void mostrarTelaCadastroUsuario() {
        JFrame frameCadastro = new JFrame("Cadastro de Novo Usuário");
        frameCadastro.setSize(400, 250);
        frameCadastro.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel userLabel = new JLabel("Nome de Usuário:");
        JTextField userTextField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Senha:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton createUserButton = new JButton("Criar Usuário");

        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeUsuario = userTextField.getText();
                String senha = new String(passwordField.getPassword());

                // Chama o método para criar um novo usuário no AutenticacaoDAO
                if (autenticacaoDAO.criarUsuario(nomeUsuario, senha)) {
                    JOptionPane.showMessageDialog(frameCadastro, "Usuário criado com sucesso!");
                    frameCadastro.dispose();
                } else {
                    JOptionPane.showMessageDialog(frameCadastro,
                            "Erro ao criar usuário. Tente um nome de usuário diferente.");
                }
            }
        });

        frameCadastro.add(userLabel);
        frameCadastro.add(userTextField);
        frameCadastro.add(passwordLabel);
        frameCadastro.add(passwordField);
        frameCadastro.add(new JLabel());
        frameCadastro.add(createUserButton);
        frameCadastro.setLocationRelativeTo(null);
        frameCadastro.setVisible(true);
    }

    // Tela Principal do Sistema
    public static void criarTelaPrincipal() {
        JFrame frame = new JFrame("Sistema de Controle de Agendamentos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane textScrollPane = new JScrollPane(textArea);

        SistemaAgendamento sistema = new SistemaAgendamento(textArea, frame);

        JPanel panel = new JPanel();
        JButton listarButton = new JButton("Listar Agendamentos");
        JButton inserirButton = new JButton("Inserir Agendamento");
        JButton atualizarButton = new JButton("Editar Agendamento");
        JButton deletarButton = new JButton("Excluir Agendamento");
        JButton sairButton = new JButton("Sair");

        listarButton.addActionListener(e -> sistema.listarAgendamentos());
        inserirButton.addActionListener(e -> sistema.inserirAgendamento());
        atualizarButton.addActionListener(e -> {
            try {
                sistema.atualizarAgendamento();
            } catch (SQLException ex) {
                Logger.getLogger(SistemaAgendamento.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        deletarButton.addActionListener(e -> sistema.deletarAgendamento());
        sairButton.addActionListener(e -> {
            textArea.setText("Saindo...\n");
            frame.dispose();
        });

        panel.add(listarButton);
        panel.add(inserirButton);
        panel.add(atualizarButton);
        panel.add(deletarButton);
        panel.add(sairButton);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(textScrollPane, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void listarAgendamentos() {
        String[] colunas = { "ID", "Filial", "Data", "Fornecedor", "Transportador", "Nota Fiscal", "Horário",
                "Status" };
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
        List<Agendamento> agendamentos = agendamentoDAO.listarAgendamentos();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Agendamento agendamento : agendamentos) {
            Object[] linha = {
                    agendamento.getId(),
                    agendamento.getFilial(),
                    sdf.format(agendamento.getDataAgendamento()),
                    agendamento.getFornecedor(),
                    agendamento.getTransportador(),
                    agendamento.getNotasFiscais(),
                    agendamento.getHorarioAgendamento(),
                    agendamento.getStatus()
            };
            model.addRow(linha);
        }

        JTable tabela = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabela);

        BorderLayout layout = (BorderLayout) frame.getContentPane().getLayout();
        if (layout.getLayoutComponent(BorderLayout.CENTER) != null) {
            frame.getContentPane().remove(layout.getLayoutComponent(BorderLayout.CENTER));
        }

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    public void inserirAgendamento() {
        JPanel panel = criarPainelAgendamento();

        int result = JOptionPane.showConfirmDialog(null, panel, "Inserir Agendamento", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Agendamento agendamento = obterAgendamentoDoPanel(panel);
                AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
                agendamentoDAO.inserirAgendamento(agendamento);
                textArea.setText("Agendamento inserido com sucesso!\n");
            } catch (IllegalArgumentException e) {
                textArea.setText("Erro: " + e.getMessage() + "\n");
            } catch (Exception e) {
                textArea.setText("Erro ao inserir agendamento: " + e.getMessage() + "\n");
            }
        }
    }

    public void deletarAgendamento() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    
        JTextField idField = new JTextField(10);
        panel.add(new JLabel("ID do Agendamento:"));
        panel.add(idField);
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Deletar Agendamento", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText());
                AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
                boolean sucesso = agendamentoDAO.excluirAgendamento(id);
                
                if (sucesso) {
                    // Mensagem de sucesso ao excluir
                    JOptionPane.showMessageDialog(null, "Agendamento deletado com sucesso! ID: " + id, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    textArea.setText("Agendamento deletado com sucesso! ID: " + id + "\n");
                } else {
                    // Mensagem de erro caso não tenha encontrado o agendamento
                    JOptionPane.showMessageDialog(null, "Agendamento não encontrado. ID: " + id, "Erro", JOptionPane.ERROR_MESSAGE);
                    textArea.setText("Erro ao deletar agendamento: Agendamento não encontrado. ID: " + id + "\n");
                }
            } catch (NumberFormatException e) {
                textArea.setText("Erro: O ID deve ser um número válido.\n");
            } catch (Exception e) {
                textArea.setText("Erro ao deletar agendamento: " + e.getMessage() + "\n");
            }
        }
    }

    public void atualizarAgendamento() throws SQLException {
        // Tela para inserção do ID
        JTextField idField = new JTextField(10);
        JPanel idPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        idPanel.add(new JLabel("ID do Agendamento a ser atualizado:"));
        idPanel.add(idField);

        int idResult = JOptionPane.showConfirmDialog(null, idPanel, "Inserir ID", JOptionPane.OK_CANCEL_OPTION);

        if (idResult == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
                Agendamento agendamento = agendamentoDAO.buscarAgendamentoPorId(id);

                if (agendamento == null) {
                    JOptionPane.showMessageDialog(null, "Agendamento não encontrado!");
                    return;
                }

                // Preencher o painel com os dados recuperados
                JPanel panel = criarPainelAgendamento();
                preencherPainelAgendamento(panel, agendamento);

                // Exibir o painel de atualização com dados preenchidos
                int result = JOptionPane.showConfirmDialog(null, panel, "Atualizar Agendamento",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    // Atualizar os dados do agendamento
                    atualizarDadosAgendamento(panel, agendamento);
                    agendamentoDAO.atualizarAgendamento(agendamento);
                    textArea.setText("Agendamento atualizado com sucesso!\n");
                }
            } catch (NumberFormatException e) {
                textArea.setText("ID inválido!\n");
            } catch (SQLException e) {
                textArea.setText("Erro ao atualizar agendamento: " + e.getMessage() + "\n");
            }
        }

    }

  private void preencherPainelAgendamento(JPanel panel, Agendamento agendamento) {
    JTextField notaFiscalField = (JTextField) panel.getComponent(1);
    JTextField transportadorField = (JTextField) panel.getComponent(3);
    JTextField fornecedorField = (JTextField) panel.getComponent(5);
    JTextField dataAgendamentoField = (JTextField) panel.getComponent(7);
    JTextField filialField = (JTextField) panel.getComponent(9);
    JComboBox<String> statusComboBox = (JComboBox<String>) panel.getComponent(11);
    JTextField horarioAgendamentoField = (JTextField) panel.getComponent(13);

    // Preenche cada campo com os valores do agendamento
    notaFiscalField.setText(String.join(",", agendamento.getNotasFiscais()));
    transportadorField.setText(agendamento.getTransportador());
    fornecedorField.setText(agendamento.getFornecedor());
    dataAgendamentoField.setText(agendamento.getDataAgendamento().toString());
    filialField.setText(agendamento.getFilial());
    statusComboBox.setSelectedItem(agendamento.getStatus());
    horarioAgendamentoField.setText(agendamento.getHorarioAgendamento().toString());
}


    private void atualizarDadosAgendamento(JPanel panel, Agendamento agendamento) {
        JTextField notaFiscalField = (JTextField) panel.getComponent(1);
        JTextField transportadorField = (JTextField) panel.getComponent(3);
        JTextField fornecedorField = (JTextField) panel.getComponent(5);
        JTextField dataAgendamentoField = (JTextField) panel.getComponent(7);
        JTextField filialField = (JTextField) panel.getComponent(9);
        JComboBox<String> statusComboBox = (JComboBox<String>) panel.getComponent(11);
        JTextField horarioAgendamentoField = (JTextField) panel.getComponent(13);

        if (!notaFiscalField.getText().isEmpty()) {
            agendamento.setNotasFiscais(Arrays.asList(notaFiscalField.getText().split(",")));
        }
        if (!transportadorField.getText().isEmpty()) {
            agendamento.setTransportador(transportadorField.getText());
        }
        if (!fornecedorField.getText().isEmpty()) {
            agendamento.setFornecedor(fornecedorField.getText());
        }
        if (!dataAgendamentoField.getText().isEmpty()) {
            agendamento.setDataAgendamento(Date.valueOf(dataAgendamentoField.getText()));
        }
        if (!filialField.getText().isEmpty()) {
            agendamento.setFilial(filialField.getText());
        }
        if (statusComboBox.getSelectedItem() != null) {
            agendamento.setStatus(statusComboBox.getSelectedItem().toString());
        }
        if (!horarioAgendamentoField.getText().isEmpty()) {
            agendamento.setHorarioAgendamento(Time.valueOf(horarioAgendamentoField.getText()));
        }
    }

    // Criação de um painel comum para inserção e atualização
    private JPanel criarPainelAgendamento() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10));

        JTextField notaFiscalField = new JTextField();
        JTextField transportadorField = new JTextField();
        JTextField fornecedorField = new JTextField();
        JTextField dataAgendamentoField = new JTextField("dd/MM/yyyy");
        JTextField filialField = new JTextField();
        JComboBox<String> statusComboBox = new JComboBox<>(new String[] { "Agendado", "Cancelado", "Concluído" });
        JTextField horarioAgendamentoField = new JTextField("HH:mm:ss");

        panel.add(new JLabel("Notas Fiscais (separadas por vírgula):"));
        panel.add(notaFiscalField);
        panel.add(new JLabel("Transportador:"));
        panel.add(transportadorField);
        panel.add(new JLabel("Fornecedor:"));
        panel.add(fornecedorField);
        panel.add(new JLabel("Data (dd/MM/yyyy):"));
        panel.add(dataAgendamentoField);
        panel.add(new JLabel("Filial:"));
        panel.add(filialField);
        panel.add(new JLabel("Status:"));
        panel.add(statusComboBox);
        panel.add(new JLabel("Horário (HH:mm:ss):"));
        panel.add(horarioAgendamentoField);

        return panel;
    }

    private Agendamento obterAgendamentoDoPanel(JPanel panel) {
        JTextField notaFiscalField = (JTextField) panel.getComponent(1);
        JTextField transportadorField = (JTextField) panel.getComponent(3);
        JTextField fornecedorField = (JTextField) panel.getComponent(5);
        JTextField dataAgendamentoField = (JTextField) panel.getComponent(7);
        JTextField filialField = (JTextField) panel.getComponent(9);
        JComboBox<String> statusComboBox = (JComboBox<String>) panel.getComponent(11);
        JTextField horarioAgendamentoField = (JTextField) panel.getComponent(13);

        Agendamento agendamento = new Agendamento();

        agendamento.setNotasFiscais(Arrays.asList(notaFiscalField.getText().split(",")));
        agendamento.setTransportador(transportadorField.getText());
        agendamento.setFornecedor(fornecedorField.getText());

        try {
            agendamento.setDataAgendamento(Date.valueOf(dataAgendamentoField.getText()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Data inválida! Use o formato yyyy-MM-dd.");
        }

        agendamento.setFilial(filialField.getText());
        agendamento.setStatus(statusComboBox.getSelectedItem().toString());

        try {
            agendamento.setHorarioAgendamento(Time.valueOf(horarioAgendamentoField.getText()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Horário inválido! Use o formato HH:mm:ss.");
        }

        return agendamento;
    }

    public class CriarUsuarioConsole {
        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);
            AutenticacaoDAO autenticacaoDAO = new AutenticacaoDAO();

            System.out.print("Digite o nome de usuário: ");
            String nomeUsuario = scanner.nextLine();

            System.out.print("Digite a senha: ");
            String senha = scanner.nextLine();

            boolean usuarioCriado = autenticacaoDAO.criarUsuario(nomeUsuario, senha);

            if (usuarioCriado) {
                System.out.println("Usuário criado com sucesso!");
            } else {
                System.out.println("Falha ao criar usuário.");
            }

            scanner.close();
        }
    }

    // Método principal para execução do sistema
    public static void main(String[] args) {
        SistemaAgendamento sistema = new SistemaAgendamento();
        sistema.mostrarTelaLogin();
    }
}