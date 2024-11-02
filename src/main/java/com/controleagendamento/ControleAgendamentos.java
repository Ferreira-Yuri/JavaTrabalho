package com.controleagendamento;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ControleAgendamentos {

    public void inserirAgendamento() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Digite o número da nota fiscal: ");
            String notaFiscal = scanner.nextLine(); // Uma nota fiscal para o primeiro agendamento

            System.out.print("Digite o nome do transportador: ");
            String transportador = scanner.nextLine();

            System.out.print("Digite o nome do fornecedor: ");
            String fornecedor = scanner.nextLine();

            System.out.print("Digite a data do agendamento (yyyy-MM-dd): ");
            String dataAgendamentoString = scanner.nextLine();

            System.out.print("Digite a filial: ");
            String filial = scanner.nextLine();

            System.out.print("Digite o status: ");
            String status = scanner.nextLine();

            System.out.print("Digite o horário do agendamento (HH:mm:ss): ");
            String horarioAgendamentoString = scanner.nextLine();

            Date dataAgendamento = Date.valueOf(dataAgendamentoString);
            Time horarioAgendamento = Time.valueOf(horarioAgendamentoString);

            Agendamento agendamento = new Agendamento();
            agendamento.setTransportador(transportador);
            agendamento.setDataAgendamento(dataAgendamento);
            agendamento.setFilial(filial);
            agendamento.setStatus(status);
            agendamento.setHorarioAgendamento(horarioAgendamento);
            agendamento.setFornecedor(fornecedor); // Adicionando o fornecedor

            List<String> notasFiscais = new ArrayList<>();
            notasFiscais.add(notaFiscal); // Adiciona a nota fiscal inicial

            System.out.print("Deseja adicionar uma nota fiscal adicional? (s/n): ");
            String resposta = scanner.nextLine();
            while (resposta.equalsIgnoreCase("s")) {
                System.out.print("Digite o número da nota fiscal: ");
                String notaFiscalAdicional = scanner.nextLine();
                notasFiscais.add(notaFiscalAdicional); // Adiciona notas fiscais adicionais
                System.out.print("Deseja adicionar outra nota fiscal? (s/n): ");
                resposta = scanner.nextLine();
            }

            agendamento.setNotasFiscais(notasFiscais); // Definindo a lista de notas fiscais

            AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
            agendamentoDAO.inserirAgendamento(agendamento);

            System.out.println("Agendamento inserido com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao inserir agendamento: " + e.getMessage());
        }
    }

    public List<Agendamento> listarAgendamentos() {
        List<Agendamento> agendamentos = new ArrayList<>();
        try {
            AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
            agendamentos = agendamentoDAO.listarAgendamentos();
            if (agendamentos.isEmpty()) {
                System.out.println("Nenhum agendamento encontrado.");
            } else {
                System.out.println("Lista de Agendamentos:");
                for (Agendamento agendamento : agendamentos) {
                    System.out.println("-------------------------------");
                    System.out.println("Notas Fiscais: " + agendamento.getNotasFiscais());
                    System.out.println("Transportador: " + agendamento.getTransportador());
                    System.out.println("Data Agendamento: " + agendamento.getDataAgendamento());
                    System.out.println("Filial: " + agendamento.getFilial());
                    System.out.println("Status: " + agendamento.getStatus());
                    System.out.println("Horário Agendamento: " + agendamento.getHorarioAgendamento());
                    System.out.println("Fornecedor: " + agendamento.getFornecedor());
                    System.out.println("-------------------------------");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar agendamentos: " + e.getMessage());
        }
        return agendamentos;
    }

    public void atualizarAgendamento() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Digite o ID do agendamento a ser atualizado: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Digite o novo número da nota fiscal: ");
            String notaFiscal = scanner.nextLine(); // Usando uma única nota fiscal

            System.out.print("Digite o novo nome do transportador: ");
            String transportador = scanner.nextLine();

            System.out.print("Digite a nova data do agendamento (DIA/MÊS/ANO): ");
            String dataAgendamentoString = scanner.nextLine();

            System.out.print("Digite a nova filial: ");
            String filial = scanner.nextLine();

            System.out.print("Digite o novo status: ");
            String status = scanner.nextLine();

            System.out.print("Digite o novo horário do agendamento (HH:mm:ss): ");
            String horarioAgendamentoString = scanner.nextLine();

            Date dataAgendamento = Date.valueOf(dataAgendamentoString);
            Time horarioAgendamento = Time.valueOf(horarioAgendamentoString);

            Agendamento agendamento = new Agendamento();
            agendamento.setId(id);
            agendamento.setNotasFiscais(List.of(notaFiscal));
            agendamento.setTransportador(transportador);
            agendamento.setDataAgendamento(dataAgendamento);
            agendamento.setFilial(filial);
            agendamento.setStatus(status);
            agendamento.setHorarioAgendamento(horarioAgendamento);

            AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
            agendamentoDAO.atualizarAgendamento(agendamento); // Atualização sem notas fiscais adicionais

            System.out.println("Agendamento atualizado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar agendamento: " + e.getMessage());
        }
    }

    public void excluirAgendamento() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Digite o ID do agendamento a ser deletado: ");
            int id = Integer.parseInt(scanner.nextLine());

            AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
            agendamentoDAO.excluirAgendamento(id);

            System.out.println("Agendamento deletado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar agendamento: " + e.getMessage());
        }
    }
}
