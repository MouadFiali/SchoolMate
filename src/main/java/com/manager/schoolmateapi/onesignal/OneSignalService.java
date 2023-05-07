package com.manager.schoolmateapi.onesignal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.manager.schoolmateapi.complaints.enumerations.ComplaintStatus;
import com.manager.schoolmateapi.onesignal.data.AlertData;
import com.manager.schoolmateapi.onesignal.data.ComplaintData;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.onesignal.client.ApiClient;
import com.onesignal.client.ApiException;
import com.onesignal.client.Configuration;
import com.onesignal.client.auth.*;
import com.onesignal.client.model.Notification;
import com.onesignal.client.model.StringMap;

import jakarta.annotation.Nullable;

import com.onesignal.client.api.DefaultApi;

@Service
public class OneSignalService {

  private final Logger logger = LoggerFactory.getLogger(OneSignalService.class);

  private static final String STUDENTS_SEGMENT = "Students";
  private static final String ADEI_MEMBERS_SEGMENT = "ADEI Members";
  private static final String MODERATORS_SEGMENT = "Moderators";

  private static Map<UserRole, List<String>> segmentMap;

  static {
    segmentMap = new HashMap<>();
    segmentMap.put(UserRole.STUDENT, List.of(STUDENTS_SEGMENT));
    segmentMap.put(UserRole.ADEI, List.of(ADEI_MEMBERS_SEGMENT));
    segmentMap.put(UserRole.MODERATOR, List.of(MODERATORS_SEGMENT));
    segmentMap.put(null, List.of(STUDENTS_SEGMENT, ADEI_MEMBERS_SEGMENT, MODERATORS_SEGMENT));
  }

  private DefaultApi apiClient;

  private String ONESIGNAL_APP_ID;

  @Autowired
  public OneSignalService(@Value("${onesignal.app.id}") String appId, @Value("${onesignal.user.key}") String userKey,
      @Value("${onesignal.app.key}") String appKey) {
    this.ONESIGNAL_APP_ID = appId;
    apiClient = initializeApiClient(appKey, userKey);
  }

  private DefaultApi initializeApiClient(String appKey, String userKey) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    HttpBearerAuth appKeyBearer = (HttpBearerAuth) defaultClient.getAuthentication("app_key");
    appKeyBearer.setBearerToken(appKey);
    HttpBearerAuth userKeyBearer = (HttpBearerAuth) defaultClient.getAuthentication("user_key");
    userKeyBearer.setBearerToken(userKey);
    return new DefaultApi(defaultClient);
  }

  /**
   * Creates a notification to a segment of users by role
   * 
   * @param title   Title of the notification
   * @param message Message of the notification
   * @param target  Target users segment. If null, sends to all users
   * @return The notification object
   */
  private Notification createSegmentedNotification(String title, String message, @Nullable UserRole target) {

    StringMap contents = new StringMap();
    contents.en(message);

    StringMap headings = new StringMap();
    headings.en(title);

    Notification notification = new Notification();
    notification.setContents(contents);
    notification.setHeadings(headings);
    notification.setIncludedSegments(segmentMap.get(target));
    notification.setAppId(ONESIGNAL_APP_ID);

    return notification;
  }

  /**
   * Creates a notification to a specific user by email
   * 
   * @param title       Title of the notification
   * @param message     Message of the notification
   * @param targetEmail Target users emails
   * @return The notification object
   */
  private Notification createTargetedNotification(String title, String message, String... targetEmails) {

    StringMap contents = new StringMap();
    contents.en(message);

    StringMap headings = new StringMap();
    headings.en(title);

    Notification notification = new Notification();
    notification.setContents(contents);
    notification.setHeadings(headings);
    notification.setIncludeExternalUserIds(Arrays.asList(targetEmails));
    notification.setAppId(ONESIGNAL_APP_ID);
    notification.setChannelForExternalUserIds("push");

    return notification;
  }

  /**
   * Sends notification to all users when the alert is confirmed
   * 
   * @param alertId          ID of the alert
   * @param alertTitle       Title of the alert
   * @param alertDescription Description of the alert
   */
  public void notifyUsersAboutAlert(long alertId, String alertTitle, String alertDescription) {
    Notification notification = createSegmentedNotification(alertTitle, alertDescription, null);

    notification.setData(new AlertData(alertId));

    try {
      apiClient.createNotification(notification);
    } catch (ApiException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends notification to moderators when a new alert is created by a student
   * 
   * @param alertId             ID of the alert
   * @param alertAuthorFullName Full name of the student who created the alert
   */
  public void notifyModeratorsAboutNewAlert(long alertId, String alertAuthorFullName) {
    Notification notification = createSegmentedNotification(
        "New Alert",
        alertAuthorFullName + " has created a new alert",
        UserRole.MODERATOR);

    notification.setData(new AlertData(alertId));

    try {
      apiClient.createNotification(notification);
    } catch (ApiException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends notification to ADEI members when a new complaint is created by a
   * student
   */
  public void notifyAdeiMembersAboutNewComplaint(long complaintId, String complaintAuthorFullName) {
    Notification notification = createSegmentedNotification(
        "New complaint",
        complaintAuthorFullName + " has created a new complaint",
        UserRole.ADEI);

    notification.setData(new ComplaintData(complaintId));

    try {
      apiClient.createNotification(notification);
    } catch (ApiException e) {
      e.printStackTrace();
    }
  }

  /**
   * Same as
   * {@link OneSignalService#notifyComplainantAboutComplaintStatusChange(long, String,
   * String, ComplaintStatus)} but sends the notification to a handler
   * 
   * IMPORTANT: Use this only if the new status is ASSIGNED. Else, use the
   * overload without {@param handlerEmail}
   */
  public void notifyComplainantAboutComplaintStatusChange(long complaintId, String complaintTitle,
      String complainantEmail, ComplaintStatus newStatus, String handlerEmail) {

    List<String> notifiedUsers = new ArrayList<>();
    notifiedUsers.add(complainantEmail);

    if (handlerEmail == null && newStatus == ComplaintStatus.ASSIGNED
        || handlerEmail != null && newStatus != ComplaintStatus.ASSIGNED) {
      throw new IllegalArgumentException("Handler email must be provided only when the status is ASSIGNED");
    }

    if (handlerEmail != null) {
      notifiedUsers.add(handlerEmail);
    }

    Notification notification = createTargetedNotification(
        "Your complaint is " + newStatus.toString().toLowerCase(),
        complaintTitle,
        complainantEmail);

    notification.setData(new ComplaintData(complaintId));

    try {
      apiClient.createNotification(notification);
    } catch (ApiException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends notification to a complainant when the complaint has change status
   * 
   * @param complaintId      ID of the complaint
   * @param complainantTitle Title of the complainant
   * @param complainantEmail Email of the complainant
   * @param newStatus        New status of the complaint
   */
  public void notifyComplainantAboutComplaintStatusChange(long complaintId, String complaintTitle,
      String complainantEmail, ComplaintStatus newStatus) {

    notifyComplainantAboutComplaintStatusChange(complaintId, complaintTitle, complainantEmail, null);
  }

  /**
   * Sends notification to a handler when a complaint is deleted by the
   * complainant
   * 
   * @param complaintTitle Title of the complaint
   * @param handlerEmail   Email of the handler
   */
  public void notifyHandlerAboutComplaintDeleted(String complaintTitle, String handlerEmail) {
    Notification notification = createTargetedNotification(
        "Complaint deleted",
        complaintTitle,
        handlerEmail);

    try {
      apiClient.createNotification(notification);
    } catch (ApiException e) {
      e.printStackTrace();
    }
  }
}
