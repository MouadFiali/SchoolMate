package com.manager.schoolmateapi.documents;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.dto.CreateDocumentTagDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentTagDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.documents.repositories.DocumentTagsRepository;
import com.manager.schoolmateapi.documents.repositories.DocumentsRepository;
import com.manager.schoolmateapi.mappers.DocumentMapper;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.models.User;
import com.manager.schoolmateapi.users.services.UserService;

@Service
public class DocumentsService {

  @Autowired
  DocumentsRepository documentsRepository;

  @Autowired
  DocumentTagsRepository documentTagsRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserService userService;

  @Autowired
  DocumentMapper documentMapper;

  private static Supplier<ResponseStatusException> DOCUMENT_NOT_FOUND_HANDLER = () -> {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
  };

  private static Supplier<ResponseStatusException> TAG_NOT_FOUND_HANDLER = () -> {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Document tag not found");
  };

  @Transactional
  public Document getDocumentById(long documentId, User user) {
    return documentsRepository.findByIdAndUser(documentId, user).orElseThrow(DOCUMENT_NOT_FOUND_HANDLER);
  }

  @Transactional
  public Document uploadDocumentForUser(User user, MultipartFile file, CreateDocumentDto createDocumentDto) {
    // Create the doc from DTO
    Document newDocument = documentMapper.createDtoToDocument(createDocumentDto);

    checkTagsOwnership(user, newDocument.getTags());

    try {
      // Save the file
      newDocument.setFile(file.getBytes());
    } catch (IOException exception) {
      exception.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error has occured while saving the file");
    }

    // Set the ownership to the user
    newDocument.setUser(user);

    // Persist in db
    documentsRepository.save(newDocument);
    return newDocument;
  }

  @Transactional
  public Iterable<Document> getAllUserDocuments(User user) {
    return documentsRepository.findByUser(user);
  }

  @Transactional
  public Page<Document> getAllUserDocumentsPaginated(User user, Pageable pageable, List<Long> tags) {
    if (tags != null && tags.size() > 0) {
      Iterable<DocumentTag> listTags = documentTagsRepository.findAllByIdInAndUser(tags, user);
      return documentsRepository.findByUserAndTagsIn(user, listTags, pageable);
    }
    return documentsRepository.findByUser(user, pageable);
  }

  @Transactional
  public Document editUserDocument(long id, EditDocumentDto editDocumentDto, User user) {
    Document document = documentsRepository.findByIdAndUser(id, user).orElseThrow(DOCUMENT_NOT_FOUND_HANDLER);

    checkTagsOwnership(user, document.getTags());

    documentMapper.updateDocumentFromDto(editDocumentDto, document);
    documentsRepository.save(document);

    return document;
  }

  public void deleteUserDocument(long id, User user) {
    documentsRepository.delete(
        documentsRepository.findById(id).orElseThrow(DOCUMENT_NOT_FOUND_HANDLER));
  }

  @Transactional
  public Page<Document> getPublicUserDocuments(long userId, Pageable pageable) {
    userService.getUser(userId);
    return documentsRepository.findByUserIdAndSharedTrue(userId, pageable);
  }

  // ------ Document Tags ------ //

  public Iterable<DocumentTag> getUserDocumentTags(User user) {
    return documentTagsRepository.findByUser(user);
  }

  public DocumentTag addDocumentTag(CreateDocumentTagDto createDocumentTagDto, User user) {
    DocumentTag documentTag = documentMapper.createDocumentTag(createDocumentTagDto);
    documentTag.setUser(user);
    return documentTagsRepository.save(documentTag);
  }

  public DocumentTag editDocumentTag(long id, EditDocumentTagDto editDocumentTagDto, User user) {
    DocumentTag documentTag = documentTagsRepository.findByIdAndUser(id, user).orElseThrow(TAG_NOT_FOUND_HANDLER);
    documentMapper.updateDocumentTagFromDto(editDocumentTagDto, documentTag);
    return documentTagsRepository.save(documentTag);
  }

  public void deleteDocumentTag(long id, User user) {
    documentTagsRepository.delete(documentTagsRepository.findById(id).orElseThrow(TAG_NOT_FOUND_HANDLER));
  }

  public void checkTagsOwnership(User user, Set<DocumentTag> tags) {
    tags.forEach(tag -> {
      if (tag.getUser() == null || !tag.getUser().getId().equals(user.getId())) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document tag not found");
      }
    });
  }

}
