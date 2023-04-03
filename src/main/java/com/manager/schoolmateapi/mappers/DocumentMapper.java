package com.manager.schoolmateapi.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.documents.repositories.DocumentTagsRepository;

import org.springframework.web.server.ResponseStatusException;

@Mapper(componentModel = "spring")
public abstract class DocumentMapper {

  @Autowired
  private DocumentTagsRepository documentTagsRepository;

  @Mapping(target = "tags", qualifiedByName = "tagsIdsListToTags")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "file", ignore = true)
  @Mapping(target = "uploadedAt", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract Document createDtoToDocument(CreateDocumentDto createDocumentDto);

  @Named("tagsIdsListToTags")
  public Set<DocumentTag> map(List<Long> ids) {
    return ids.stream().map(id -> {
      return documentTagsRepository.findById(id).orElseThrow(() -> {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Document tag #%d not found", id));
      });
    }).collect(Collectors.toSet());
  }
}
