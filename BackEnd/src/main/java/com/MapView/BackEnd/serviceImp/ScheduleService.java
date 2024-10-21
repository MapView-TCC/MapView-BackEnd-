package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.Notification.NotificationCreateDTO;
import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.Notification;
import com.MapView.BackEnd.entities.TrackingHistory;
import com.MapView.BackEnd.repository.EquipmentRepository;
import com.MapView.BackEnd.repository.NotificationRepository;
import com.MapView.BackEnd.repository.TrackingHistoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ScheduleService {

    private final EquipmentRepository equipmentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationServiceImp notificationServiceImp;
    private final TrackingHistoryServiceImp trackingHistoryServiceImp;
    private final TrackingHistoryRepository trackingHistoryRepository;

    public ScheduleService(EquipmentRepository equipmentRepository, NotificationRepository notificationRepository, NotificationServiceImp notificationServiceImp, TrackingHistoryServiceImp trackingHistoryServiceImp, TrackingHistoryRepository trackingHistoryRepository) {
        this.equipmentRepository = equipmentRepository;
        this.notificationRepository = notificationRepository;
        this.notificationServiceImp = notificationServiceImp;
        this.trackingHistoryServiceImp = trackingHistoryServiceImp;
        this.trackingHistoryRepository = trackingHistoryRepository;
    }

    // toda a segunda feira ele vai fazer essa função
    @Scheduled(cron = "0 0 0 * * MON")
    public void createNotification() {
        List<Equipment> equipmentList = equipmentRepository.findAllByOperativeTrue();

        for (Equipment e : equipmentList) {
            LocalDate validity = e.getValidity();

            if (validity != null && LocalDate.now().getYear() == validity.getYear()) {

                if (getTrimestre(validity.getMonthValue()) == getTrimestre(LocalDate.now().getMonthValue())) {

                    TrackingHistory trackingHistory = new TrackingHistory();

                    NotificationCreateDTO notification = new NotificationCreateDTO(e.getName_equipment());
                    notificationServiceImp.createNotification(notification);
                }
            }
        }
    }

    // Função para excluir as notificações depois de um certo periodo
    // @Scheduled(cron = "0/1 * * * * *") // executa cada segundo, usar para teste

    @Scheduled(cron = "0 0 0 1 1,4,7,10 *") // Executa à meia-noite no dia 1 dos meses 1, 4, 7 e 10, a cada 3 meses
    public void deleteNotification() {
        List<Notification> notificationList = notificationRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        for (Notification notification : notificationList) {
            LocalDate notificationDate = notification.getDate_notification();

            // Verifica se a notificação está há 3 meses
            // if (!notificationDate.plusMonths(3).isAfter(currentDate)) {
            if (notificationDate.plusMonths(3).isBefore(currentDate) || notificationDate.plusMonths(3).isEqual(currentDate)) {
                //System.out.println("Notificação deletada: " + notification.getId_notification());
                notificationServiceImp.deleteNotificationById(notification.getId_notification());
            }
        }
    }

    @Scheduled(cron = "0 0 0 1 1 *") // No início do minuto e hora, à meia-noite, no dia 1 de janeiro, qualquer dia da semana
    //@Scheduled(cron = "0 0 0 1 1,7 *") // no inicio do minuto e hora, a meia noite, no dia 1 do mês, em janeiro(1) e julho(7), qualquer dia da semana
    public void deleteTrackingHistory(){
        List<TrackingHistory> trackingHistoriesList = trackingHistoryRepository.findAll();
        Instant oneYearAgo = Instant.now().minus(365, ChronoUnit.DAYS); // Calcula a data de um ano atrás

        for (TrackingHistory trackingHistory : trackingHistoriesList) {
            Instant trackingDateTime = trackingHistory.getDatetime(); // Obtem o Instant

            if (trackingDateTime.isBefore(oneYearAgo)) { // Verifica se a data é anterior a um ano
                //System.out.println("Deletando tracking history com o ID: " + trackingHistory.getId_tracking());
                trackingHistoryServiceImp.deleteTrackingById(trackingHistory.getId()); // Deleta
            }
        }
    }

    // Função para calcular o trimestre baseado no mês
    public static int getTrimestre(int month) {
        return (month - 1) / 3 + 1;
    }

}
