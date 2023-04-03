package com.manager.schoolmateapi.documents;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.users.models.MyUserDetails;

@RestController
@RequestMapping("/documents")
public class DocumentsController {

  @Autowired
  DocumentsService documentsService;

  @PostMapping(path = "", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @ResponseStatus(HttpStatus.CREATED)
  public Document uploadNewDocument(
      @AuthenticationPrincipal MyUserDetails userDetails,
      @RequestPart(name = "file", required = true) MultipartFile file,
      @RequestPart CreateDocumentDto data) {
    return documentsService.uploadDocumentForUser(userDetails.getUser(), file, data);
  }
}
