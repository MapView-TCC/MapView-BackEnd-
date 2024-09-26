package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.repository.AccessHistoryRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.AccessHistoryService;
import com.MapView.BackEnd.dtos.AccessHistory.AccessHistoryCreateDTO;
import com.MapView.BackEnd.dtos.AccessHistory.AccessHistoryDetailsDTO;
import com.MapView.BackEnd.entities.AccessHistory;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.infra.Exceptions.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessHistoryServiceImp implements AccessHistoryService {



    private final AccessHistoryRepository accessHistoryRepository;
    private final UserRepository userRepository;

    public AccessHistoryServiceImp(AccessHistoryRepository accessHistoryRepository, UserRepository userRepository) {
        this.accessHistoryRepository = accessHistoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccessHistoryDetailsDTO getAccessHistory(Long id_history) {
        AccessHistory accessHistory = this.accessHistoryRepository.findById(id_history)
                .orElseThrow(() -> new NotFoundException("Id not found"));

        return new AccessHistoryDetailsDTO(accessHistory);
    }

    @Override
    public List<AccessHistoryDetailsDTO> getAllAccessHistory(int page, int itens) {
        return accessHistoryRepository.findAll(PageRequest.of(page, itens)).stream().map(AccessHistoryDetailsDTO::new).toList();
    }

    @Override
    public AccessHistoryDetailsDTO createAccessHistory(AccessHistoryCreateDTO dados) {
        Users user = userRepository.findById(dados.user_id())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        AccessHistory history = new AccessHistory();
        history.setUser(user);

        // O campo login_datetime será preenchido automaticamente
        accessHistoryRepository.save(history);

        return new AccessHistoryDetailsDTO(history);
    }

    @Override
    public AccessHistoryDetailsDTO updateAccessHistory(Long id_history) {
        var history = accessHistoryRepository.findById(id_history).orElseThrow(() -> new NotFoundException("Id not found"));

        // Atualiza a data de logout no objeto history
        history.setLogout_datetime(LocalDateTime.now());

        accessHistoryRepository.save(history);
        return new AccessHistoryDetailsDTO(history);
    }
}
