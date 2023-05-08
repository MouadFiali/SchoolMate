package com.manager.schoolmateapi.documents;

import java.io.InputStream;
import java.util.List;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.dto.CreateDocumentTagDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentTagDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.utils.MessageResponse;
import com.manager.schoolmateapi.utils.StringUtils;
import com.manager.schoolmateapi.utils.dto.PaginatedResponse;

import jakarta.validation.Valid;

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
      @Valid @RequestPart CreateDocumentDto data) {
    return documentsService.uploadDocumentForUser(userDetails.getUser(), file, data);
  }

  @GetMapping(path = "")
  public PaginatedResponse<Document> getAllUserDocuments(
      @AuthenticationPrincipal MyUserDetails userDetails,
      Pageable pageable,
      @RequestParam(required = false, value = "tags") List<Long> tags) {
    
    Page<Document> results = documentsService.getAllUserDocumentsPaginated(
        userDetails.getUser(),
        pageable,
        tags);

    PaginatedResponse<Document> response = PaginatedResponse.<Document>builder()
        .results(results.getContent())
        .page(results.getNumber())
        .totalPages(results.getTotalPages())
        .count(results.getNumberOfElements())
        .totalItems(results.getTotalElements())
        .last(results.isLast())
        .build();

    return response;
  }

  @GetMapping("/{id}")
  public Document getUserDocument(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable long id) {
    return documentsService.getDocumentByIdAndUser(id, userDetails.getUser());
  }

  @PatchMapping("/{id}")
  public Document editUserDocument(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable long id,
      @Valid @RequestBody EditDocumentDto editDocumentDto) {
    return documentsService.editUserDocument(id, editDocumentDto, userDetails.getUser());
  }

  @DeleteMapping("/{id}")
  public MessageResponse deleteUserDocument(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable long id) {
    documentsService.deleteUserDocument(id, userDetails.getUser());
    return MessageResponse.builder().message("Document deleted successfully").build();
  }

  @GetMapping("/{id}/file")
  public ResponseEntity<byte[]> downloadUserDocument(
      @AuthenticationPrincipal MyUserDetails userDetails,
      @PathVariable long id) {
    Document document = documentsService.getDocumentForDownload(id, userDetails.getUser());

    byte[] file = document.getFile();

    // Detect the file mime/type for extension
    TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
    Metadata meta = new Metadata();
    InputStream inputStream = TikaInputStream.get(file, meta);
    try {
      org.apache.tika.mime.MediaType mediaType = tikaConfig.getMimeRepository().detect(inputStream, meta);
      MimeType mimeType = tikaConfig.getMimeRepository().forName(mediaType.toString());
      String extension = mimeType.getExtension();

      // Return the file bytes
      return ResponseEntity
          .ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
              String.format("attachment; filename=\"%s%s\"", StringUtils.slugify(document.getName()), extension))
          .body(file);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occured while processing your file");
    }
  }

  @GetMapping("/user/{id}")
  public PaginatedResponse<Document> getOtherUserDocuments(
    @AuthenticationPrincipal MyUserDetails userDetails,
    Pageable pageable,
    @PathVariable Long id) {

    Page<Document> results = documentsService.getPublicUserDocuments(id, pageable);

    PaginatedResponse<Document> response = PaginatedResponse.<Document>builder()
        .results(results.getContent())
        .page(results.getNumber())
        .totalPages(results.getTotalPages())
        .count(results.getNumberOfElements())
        .totalItems(results.getTotalElements())
        .last(results.isLast())
        .build();

    return response;
  }

  // ------ Document Tags ------ //

  @GetMapping("/tags")
  public Iterable<DocumentTag> getUserDocumentTags(@AuthenticationPrincipal MyUserDetails userDetails) {
    return documentsService.getUserDocumentTags(userDetails.getUser());
  }

  @PostMapping("/tags")
  @ResponseStatus(HttpStatus.CREATED)
  public DocumentTag addDocumentTag(
      @AuthenticationPrincipal MyUserDetails userDetails,
      @Valid @RequestBody CreateDocumentTagDto createDocumentTagDto) {
    return documentsService.addDocumentTag(createDocumentTagDto, userDetails.getUser());
  }

  @PatchMapping("/tags/{id}")
  public DocumentTag editDocumentTag(
      @AuthenticationPrincipal MyUserDetails userDetails,
      @PathVariable long id,
      @Valid @RequestBody EditDocumentTagDto editDocumentTagDto) {
    return documentsService.editDocumentTag(id, editDocumentTagDto, userDetails.getUser());
  }

  @DeleteMapping("/tags/{id}")
  public MessageResponse deleteDocumentTag(
      @AuthenticationPrincipal MyUserDetails userDetails,
      @PathVariable long id) {
    documentsService.deleteDocumentTag(id, userDetails.getUser());
    return MessageResponse.builder().message("Document tag deleted successfully").build();
  }
}
