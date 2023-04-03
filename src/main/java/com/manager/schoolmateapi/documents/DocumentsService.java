package com.manager.schoolmateapi.documents;

import java.io.IOException;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.repositories.DocumentsRepository;
import com.manager.schoolmateapi.mappers.DocumentMapper;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.models.User;

@Service
public class DocumentsService {

  @Autowired
  DocumentsRepository documentsRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  DocumentMapper documentMapper;

  private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
  };

  public Document getDocumentById(Long documentId, User user) {
    return documentsRepository.findByIdAndUser(documentId, user).orElseThrow(NOT_FOUND_HANDLER);
  }

  public Document uploadDocumentForUser(User user, MultipartFile file, CreateDocumentDto createDocumentDto) {
    // Create the doc from DTO
    Document newDocument = documentMapper.createDtoToDocument(createDocumentDto);

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

  public Iterable<Document> getAllUserDocuments(User user) {
    return documentsRepository.findByUser(user);
  }

}
