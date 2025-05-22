package com.example.agenda.service;

import com.example.agenda.model.Medico;
import com.example.agenda.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    @Autowired
    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> buscarPorId(Long id) {
        return medicoRepository.findById(id);
    }

    public Medico buscarPorCrm(String crm) {
        return medicoRepository.findByCrm(crm);
    }

    public Medico salvar(Medico medico) {
        return medicoRepository.save(medico);
    }

    public void excluir(Long id) {
        medicoRepository.deleteById(id);
    }
}