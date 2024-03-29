package com.manager.schoolmateapi.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.dto.CreateDocumentTagDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentTagDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.documents.repositories.DocumentTagsRepository;

import org.springframework.web.server.ResponseStatusException;

@Mapper(componentModel = "spring")
public abstract class DocumentMapper {

  @Autowired
  private DocumentTagsRepository documentTagsRepository;

  @Mapping(source = "tags", target = "tags", qualifiedByName = "tagsIdsListToTags")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "file", ignore = true)
  @Mapping(target = "uploadedAt", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract Document createDtoToDocument(CreateDocumentDto createDocumentDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "tags", target = "tags", qualifiedByName = "tagsIdsListToTags")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "file", ignore = true)
  @Mapping(target = "uploadedAt", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract void updateDocumentFromDto(EditDocumentDto editDocumentDto,
      @MappingTarget Document document);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "documents", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract DocumentTag createDocumentTag(CreateDocumentTagDto createDocumentTagDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "documents", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract void updateDocumentTagFromDto(EditDocumentTagDto editDocumentTagDto,
      @MappingTarget DocumentTag documentTag);

  @Named("tagsIdsListToTags")
  public Set<DocumentTag> map(List<Long> ids) {
    if (ids == null)
      return null;

    return ids.stream().map(id -> {
      return documentTagsRepository.findById(id).orElseThrow(() -> {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Document tag #%d not found", id));
      });
    }).collect(Collectors.toSet());
  }
}
