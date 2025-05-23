package com.example.agenda.util;

import com.example.agenda.enums.Prioridade;
import com.example.agenda.model.Consulta;

import java.util.Comparator;
import java.util.PriorityQueue;

public class FilaPrioridade {

    private final PriorityQueue<Consulta> filaConsulatas;

    public FilaPrioridade() {
        Comparator<Consulta> comparador = (c1, c2) -> {
            int comparaPrioridade = c2.getPrioridade().ordinal() - c1.getPrioridade().ordinal();

            if (comparaPrioridade == 0) {
                return c1.getDataHora().compareTo(c2.getDataHora());
            }
            return comparaPrioridade;
        };
        this.filaConsulatas = new PriorityQueue<>(comparador);
    }

    public void adicionarConsulta(Consulta consulta) {
        filaConsulatas.offer(consulta);
    }

    public void removeConsulta(Consulta consulta) {
        filaConsulatas.remove(consulta);
    }

    public Consulta visulaizarProximaConsulta() {
        return filaConsulatas.peek();
    }

    public Consulta atenderRemoverProximaConsulta() {
        return filaConsulatas.poll();
    }

    public int quantidadeConsultas() {
        return filaConsulatas.size();
    }

    public boolean existeConsultaEmergencia() {
        Consulta prox = visulaizarProximaConsulta();
        return prox != null && prox.getPrioridade() == Prioridade.EMERGENCIA;
    }

    public PriorityQueue<Consulta> filtrarPorPrioridade(Prioridade prioridade) {
        PriorityQueue<Consulta> filtro = new PriorityQueue<>(
                (c1, c2) -> c1.getDataHora().compareTo(c2.getDataHora())
        );

        for (Consulta c : filaConsulatas) {
            if (c.getPrioridade() == prioridade) {
                filtro.offer(c);
            }
        }
        return filtro;
    }

}
