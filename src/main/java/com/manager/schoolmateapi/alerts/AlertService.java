package com.manager.schoolmateapi.alerts;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.alerts.dto.CreateAlertDto;
import com.manager.schoolmateapi.alerts.dto.EditAlertDto;
import com.manager.schoolmateapi.alerts.enumerations.AlertStatus;
import com.manager.schoolmateapi.mappers.AMapper;

@Service

public class AlertService {
    private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found");
      };

      @Autowired
      private AMapper dtoMapper;
      @Autowired
     private AlertRepository alertRepository;


     public Alert getAlertById(Long id){
        return alertRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
     }

     public Iterable<Alert> getAllAlerts(){
        return alertRepository.findAll();
     }

     public Alert addAlert(CreateAlertDto createAlertDto ){
      
        return alertRepository.save(dtoMapper.createDtoToAlert(createAlertDto));
     }

     public Alert editAlert(Long id , EditAlertDto editAlertDto){
       
       Alert alert = alertRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
       dtoMapper.updateAlertFromDto(editAlertDto, alert);
       alertRepository.save(alert);
       return alert;
     }

     public void deleteAlert(Long id){
        alertRepository.delete( alertRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER));
     }
       
    public Alert cancelAlert(Long id) throws Exception {
      Alert alert = getAlertById(id);
      
      if (alert == null) {
          try {
            throw new Exception("Alert not found with id: " + id);
         } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      
      if (alert.getStatus() == AlertStatus.CANCELLED) {
          throw new Exception("Alert is already cancelled");
      }
      
      alert.setStatus(AlertStatus.CANCELLED);
      
      return alertRepository.save(alert);
  }
  
  public Alert confirmAlert(Long id) throws Exception {
      Alert alert = getAlertById(id);
      
      if (alert == null) {
          throw new Exception("Alert not found with id: " + id);
         // return new ResponseStatusException(Htt )
      }
      
      if (alert.getStatus() == AlertStatus.CONFIRMED) {
          throw new Exception("Alert is already confirmed");
      }
      
      alert.setStatus(AlertStatus.CONFIRMED);
      
      return alertRepository.save(alert);
  }
}

