package com.example.agenda.service;

import com.example.agenda.enums.Prioridade;
import com.example.agenda.enums.Status;
import com.example.agenda.model.Consulta;
import com.example.agenda.model.Medico;
import com.example.agenda.model.Paciente;
import com.example.agenda.repository.ConsultaRepository;
import com.example.agenda.util.ArvoreRubroNegra;
import com.example.agenda.util.ConsultaHashMap;
import com.example.agenda.util.FilaPrioridade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class GerenciamentoConsultasService {

    private final ArvoreRubroNegra arvoreConsultas;
    private final ConsultaHashMap hashMapConsultas;
    private final FilaPrioridade filaPrioridadeConsultas;

    private final ConsultaRepository consultaRepository;

    @Autowired
    public GerenciamentoConsultasService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
        this.arvoreConsultas = new ArvoreRubroNegra();
        this.hashMapConsultas = new ConsultaHashMap();
        this.filaPrioridadeConsultas = new FilaPrioridade();

        carregarConsultas();
    }

    private void carregarConsultas() {
        List<Consulta> consultas = consultaRepository.findAll();

        for (Consulta c : consultas) {
            if (c.getStatus() != Status.CANCELADA && c.getStatus() != Status.CONCLUIDA) {
                adicionarConsultaNasEstruturas(c);
            }
        }
    }

    public void adicionarConsultaNasEstruturas(Consulta consulta) {
        arvoreConsultas.adicionarConsulta(consulta);
        hashMapConsultas.adicionarConsulta(consulta);
        filaPrioridadeConsultas.adicionarConsulta(consulta);
    }

    public void removerConsultasDasEstruturas(Consulta consulta) {
        arvoreConsultas.removerConsulta(consulta.getDataHora());
        hashMapConsultas.removerConsulta(consulta);
        filaPrioridadeConsultas.removeConsulta(consulta);
    }

    public Consulta agendarConsulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String motivo, Prioridade prioridade) {
        LocalDateTime fimConsulta = dataHora.plusMinutes(30);

        if (!arvoreConsultas.verifacaHorarioDisponivel(dataHora, fimConsulta)) {
            throw new RuntimeException("Horário indisponível!");
        }

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setDataHora(dataHora);
        consulta.setMotivo(motivo);
        consulta.setPrioridade(prioridade);
        consulta.setStatus(Status.AGENDADA);

        Consulta salvarConsulta = consultaRepository.save(consulta);
        adicionarConsultaNasEstruturas(salvarConsulta);

        return salvarConsulta;
    }

    public void cancelarConsulta(Long IDconsulta) {
        Consulta consulta = consultaRepository.findById(IDconsulta)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.setStatus(Status.CANCELADA);
        consultaRepository.save(consulta);

        removerConsultasDasEstruturas(consulta);
    }

    public void confirmarConsulta(Long IDconsulta) {
        Consulta consulta = consultaRepository.findById(IDconsulta)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.setStatus(Status.CONFIRMADA);
        consultaRepository.save(consulta);
    }

    public void iniciarConsulta(Long IDconsulta) {
        Consulta consulta = consultaRepository.findById(IDconsulta)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.setStatus(Status.EM_ANDAMENTO);
        consultaRepository.save(consulta);
    }

    public void concluirConsulta(Long IDconsulta) {
        Consulta consulta = consultaRepository.findById(IDconsulta)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.setStatus(Status.CONCLUIDA);
        consultaRepository.save(consulta);

        removerConsultasDasEstruturas(consulta);
    }

    public Consulta alterarConsulta(Long IDconsulta, LocalDateTime novaDataHora, Prioridade novaPrioridade) {
        Consulta consulta = consultaRepository.findById(IDconsulta)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        removerConsultasDasEstruturas(consulta);

        LocalDateTime fimConsulta = novaDataHora.plusMinutes(30);
        if (!arvoreConsultas.verifacaHorarioDisponivel(novaDataHora, fimConsulta)) {
            adicionarConsultaNasEstruturas(consulta);
            throw new RuntimeException("Novo horário indisponivel para agendamento");
        }

        consulta.setDataHora(novaDataHora);
        consulta.setPrioridade(novaPrioridade);

        Consulta consultaAtualizada = consultaRepository.save(consulta);
        adicionarConsultaNasEstruturas(consultaAtualizada);

        return consultaAtualizada;
    }

    public Consulta proximaConsulta() {
        return arvoreConsultas.proximaConsulta();
    }

    public Consulta proximaConsultaComPrioridade() {
        return filaPrioridadeConsultas.visulaizarProximaConsulta();
    }

    public Collection<Consulta> consultasPorHorario(LocalDateTime inicio, LocalDateTime fim) {
        return arvoreConsultas.buscarPorHorario(inicio, fim);
    }

    public List<Consulta> consultasPorPacientes(Long IDpaciente) {
        return hashMapConsultas.buscarConsulta(IDpaciente);
    }

    public boolean verificaConsultaEmergencial() {
        return filaPrioridadeConsultas.existeConsultaEmergencia();
    }
}