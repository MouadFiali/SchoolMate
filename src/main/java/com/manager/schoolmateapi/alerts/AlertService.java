package com.manager.schoolmateapi.alerts;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.alerts.dto.CreateAlertDto;
import com.manager.schoolmateapi.alerts.dto.EditAlertDto;
import com.manager.schoolmateapi.alerts.enumerations.AlertStatus;
import com.manager.schoolmateapi.mappers.AMapper;
import com.manager.schoolmateapi.users.models.User;

import jakarta.transaction.Transactional;

@Service

public class AlertService {
   private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
      return new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found");
   };

   @Autowired
   private AMapper dtoMapper;
   @Autowired
   private AlertRepository alertRepository;

   public Alert getAlertById(Long id, User user) {
      return alertRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER);
   }

   @Transactional
   public Iterable<Alert> getAllUserAlerts(User user) {
      return alertRepository.findByUser(user);
   }

   @Transactional
   public Page<Alert> getAllUserAlertsPaginated(User user, Pageable pageable) {
      return alertRepository.findByUser(user, pageable);
   }

   @Transactional
   
   public Page<Alert> getAllAlertsPaginated(Pageable pageable) {
      
      return alertRepository.findByStatus(AlertStatus.CONFIRMED,pageable);

   }

   @Transactional
   public Page<Alert> getPendingAlertsPaginated(Pageable pageable) {
      return alertRepository.findByStatus(AlertStatus.PENDING, pageable);
   }
   
   public Alert addUserAlert(CreateAlertDto createAlertDto, User user) {
      Alert alert = dtoMapper.createDtoToAlert(createAlertDto);
      alert.setUser(user);
      alert.setStatus(AlertStatus.PENDING);
      return alertRepository.save(alert);
   }

   public Alert editUserAlert(Long id, EditAlertDto editAlertDto, User user) {

      Alert alert = alertRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER);
      dtoMapper.updateAlertFromDto(editAlertDto, alert);
      alertRepository.save(alert);
      return alert;
   }

   public void deleteUserAlert(Long id, User user) {
      alertRepository.delete(alertRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER));
   }

   public Alert cancelUserAlert(Long id, User user) throws Exception {
      Alert alert = alertRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER);
      if (alert.getStatus() == AlertStatus.CANCELLED) {
         throw new ResponseStatusException(HttpStatus.CONFLICT);
      }
      alert.setStatus(AlertStatus.CANCELLED);
      alertRepository.save(alert);
      return alert;

   }

   public Alert confirmUserAlert(Long id, User user) {
      Alert alert = alertRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER);

      if (alert.getStatus() == AlertStatus.CONFIRMED) {
         throw new ResponseStatusException(HttpStatus.CONFLICT);
      }

      alert.setStatus(AlertStatus.CONFIRMED);

      return alertRepository.save(alert);

   }
}
