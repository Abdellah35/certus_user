package com.softedge.solution.repository;

import com.softedge.solution.repomodels.NotificationDetails;

import java.util.List;

public interface NotificationRepository {

    List<NotificationDetails> getNotificationsByRequestee(Long userId);

    List<NotificationDetails> getNotificationsByRequestor(Long userId);

    Long getNotificationsCountForRequestor(Long userId);

    Long getNotificationsCountForRequestee(Long userId);

    Long updateMarkedAsReadNotification(Long id);

    Long updateMarkedAsReadRequestorNotification(Long requestorUserId);

    Long updateMarkedAsReadRequesteeNotification(Long requesteeUserId);
}
