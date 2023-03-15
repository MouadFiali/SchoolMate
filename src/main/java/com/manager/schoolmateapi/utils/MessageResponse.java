package com.manager.schoolmateapi.utils;

import lombok.Data;
import lombok.NonNull;

@Data
public class MessageResponse {
  @NonNull
  private String message;
}
