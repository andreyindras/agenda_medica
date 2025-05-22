package com.example.agenda.util;

import com.example.agenda.model.Consulta;
import com.example.agenda.model.Paciente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultaHashMap {

    private final Map<Long, List<Consulta>> consultaPorPacientes;

    public ConsultaHashMap() {
        this.consultaPorPacientes = new HashMap<>();
    }

    public void adicionarConsulta(Consulta consulta) {
        Long IDpaciente = consulta.getPaciente().getId();

        if (!consultaPorPacientes.containsKey(IDpaciente)) {
            consultaPorPacientes.put(IDpaciente, new ArrayList<>());
        }

        consultaPorPacientes.get(IDpaciente).add(consulta);
    }


    public void removerConsulta(Consulta consulta) {
        Long IDpaciente = consulta.getPaciente().getId();

        if (consultaPorPacientes.containsKey(IDpaciente)) {
            consultaPorPacientes.get(IDpaciente).remove(consulta);

            if (consultaPorPacientes.get(IDpaciente).isEmpty()) {
                consultaPorPacientes.remove(IDpaciente);
            }
        }
    }

    public List<Consulta> buscarConsulta(Long IDpaciente) {
        return consultaPorPacientes.getOrDefault(IDpaciente, new ArrayList<>());
    }

    public List<Consulta> buscarConsulta(Paciente paciente) {
        return buscarConsulta(paciente.getId());
    }

    public void atualizarConsulta(Consulta consultaAntiga, Consulta consultaNova) {
        removerConsulta(consultaAntiga);
        adicionarConsulta(consultaNova);
    }

    public Map<Long, List<Consulta>> todasConsultar() {
        return new HashMap<>(consultaPorPacientes);
    }
}