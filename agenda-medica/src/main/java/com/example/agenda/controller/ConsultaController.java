package com.example.agenda.controller;

import com.example.agenda.enums.Prioridade;
import com.example.agenda.model.Consulta;
import com.example.agenda.model.Medico;
import com.example.agenda.model.Paciente;
import com.example.agenda.service.ConsultaService;
import com.example.agenda.service.GerenciamentoConsultasService;
import com.example.agenda.service.MedicoService;
import com.example.agenda.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final GerenciamentoConsultasService gerenciamentoConsultasService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    @Autowired
    public ConsultaController(ConsultaService consultaService, GerenciamentoConsultasService gerenciamentoConsultasService, PacienteService pacienteService, MedicoService medicoService) {
        this.consultaService = consultaService;
        this.gerenciamentoConsultasService = gerenciamentoConsultasService;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
    }

    @GetMapping
    public ResponseEntity<List<Consulta>> listarTodas() {
        List<Consulta> consultas = consultaService.listarTodas();
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consulta> buscarPorId(@PathVariable Long id) {
        Optional<Consulta> consulta = consultaService.buscarPorId(id);
        return consulta.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Consulta>> buscarPorPaciente(@PathVariable Long pacienteId) {
        List<Consulta> consultas = consultaService.buscarPorPaciente(pacienteId);
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<Consulta>> buscarPorMedico(@PathVariable Long medicoId) {
        List<Consulta> consultas = consultaService.buscarPorMedico(medicoId);
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Consulta>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<Consulta> consultas = consultaService.buscarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/gerenciamento/proxima")
    public ResponseEntity<Consulta> proximaConsulta() {
        Consulta consulta = gerenciamentoConsultasService.proximaConsulta();

        if (consulta != null) {
            return ResponseEntity.ok(consulta);
        } else {
            return ResponseEntity.noContent().build();
        }
    }


    @GetMapping("/gerenciamento/proxima-prioridade")
    public ResponseEntity<Consulta> proximaConsultaComPrioridade() {
        Consulta consulta = gerenciamentoConsultasService.proximaConsultaComPrioridade();

        if (consulta != null) {
            return ResponseEntity.ok(consulta);
        } else {
            return ResponseEntity.noContent().build();
        }
    }


    @GetMapping("/gerenciamento/periodo")
    public ResponseEntity<Collection<Consulta>> consultasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        Collection<Consulta> consultas = gerenciamentoConsultasService.consultasPorHorario(inicio, fim);
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/gerenciamento/paciente/{pacienteId}")
    public ResponseEntity<List<Consulta>> consultasPorPaciente(@PathVariable Long pacienteId) {
        List<Consulta> consultas = gerenciamentoConsultasService.consultasPorPacientes(pacienteId);
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/gerenciamento/emergencia")
    public ResponseEntity<Boolean> verificaConsultaEmergencial() {
        boolean temEmergencia = gerenciamentoConsultasService.verificaConsultaEmergencial();
        return ResponseEntity.ok(temEmergencia);
    }

    @PostMapping
    public ResponseEntity<Consulta> agendar(@RequestParam Long pacienteId, @RequestParam Long medicoId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataHora, @RequestParam String motivo, @RequestParam Prioridade prioridade) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));

        Medico medico = medicoService.buscarPorId(medicoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico não encontrado"));

        try {
            Consulta consulta = gerenciamentoConsultasService.agendarConsulta(
                    paciente, medico, dataHora, motivo, prioridade);
            return ResponseEntity.status(HttpStatus.CREATED).body(consulta);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}/alterar")
    public ResponseEntity<Consulta> alterarConsulta(@PathVariable Long id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime novaDataHora, @RequestParam Prioridade novaPrioridade) {

        try {
            Consulta consulta = gerenciamentoConsultasService.alterarConsulta(id, novaDataHora, novaPrioridade);
            return ResponseEntity.ok(consulta);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarConsulta(@PathVariable Long id) {
        try {
            gerenciamentoConsultasService.cancelarConsulta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmarConsulta(@PathVariable Long id) {
        try {
            gerenciamentoConsultasService.confirmarConsulta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Void> iniciarConsulta(@PathVariable Long id) {
        try {
            gerenciamentoConsultasService.iniciarConsulta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/concluir")
    public ResponseEntity<Void> concluirConsulta(@PathVariable Long id) {
        try {
            gerenciamentoConsultasService.concluirConsulta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}