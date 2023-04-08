package com.manager.schoolmateapi.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.utils.MessageResponse;

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

  @GetMapping("")
  public Iterable<Document> getAllUserDocuments(@AuthenticationPrincipal MyUserDetails userDetails) {
    return documentsService.getAllUserDocuments(userDetails.getUser());
  }

  @GetMapping("/{id}")
  public Document getUserDocument(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable long id) {
    return documentsService.getDocumentById(id, userDetails.getUser());
  }

  @PatchMapping("/{id}")
  public Document editUserDocument(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable long id,
      @RequestBody EditDocumentDto editDocumentDto) {
    return documentsService.editUserDocument(id, editDocumentDto, userDetails.getUser());
  }

  @DeleteMapping("/{id}")
  public MessageResponse deleteUserDocument(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable long id) {
    documentsService.deleteUserDocument(id, userDetails.getUser());
    return MessageResponse.builder().message("Document deleted successfully").build();
  }
}
