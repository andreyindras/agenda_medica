package com.example.agenda.util;

import com.example.agenda.model.Consulta;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ArvoreRubroNegra {

    private final TreeMap<LocalDateTime, Consulta> consultaTreeMap;

    public ArvoreRubroNegra() {
        this.consultaTreeMap = new TreeMap<>();
    }

    public void adicionarConsulta(Consulta consulta) {
        consultaTreeMap.put(consulta.getDataHora(), consulta);
    }

    public void removerConsulta(LocalDateTime dataHora) {
        consultaTreeMap.remove(dataHora);
    }

    public Consulta buscarConsulta(LocalDateTime dataHora) {
        return consultaTreeMap.get(dataHora);
    }

    public Consulta proximaConsulta() {
        Map.Entry<LocalDateTime, Consulta> entry = consultaTreeMap.firstEntry();

        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

    public Collection<Consulta> buscarPorHorario(LocalDateTime inicio, LocalDateTime fim) {
        return consultaTreeMap.subMap(inicio, true, fim, true).values();
    }

    public boolean verifacaHorarioDisponivel(LocalDateTime dataHora, LocalDateTime fimConsulta) {
        return consultaTreeMap.subMap(dataHora, true, fimConsulta, false).isEmpty();
    }

    public Collection<Consulta> todasConsultas() {
        return consultaTreeMap.values();
    }

}