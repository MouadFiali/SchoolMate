package com.manager.schoolmateapi.documents;

import static org.hamcrest.MatcherAssert.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.dto.CreateDocumentTagDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentTagDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.mappers.DocumentMapper;

@SpringBootTest
public class MappersTest {

  @Autowired
  DocumentMapper documentMapper;

  @Test
  public void testCreateDocumentFromDto_shouldReturnDocumentSuccessfully() {
    CreateDocumentDto createDocumentDto = CreateDocumentDto
        .builder()
        .name("Plateformes de développement")
        .shared(true)
        .build(); // Tags cannot be tested since they need the data source

    Document doc = documentMapper.createDtoToDocument(createDocumentDto);
    assertThat(doc.getName(), Matchers.is(createDocumentDto.getName()));
    assertThat(doc.isShared(), Matchers.is(createDocumentDto.isShared()));
    assertThat(doc.getTags(), Matchers.nullValue());
    assertThat(doc.getFile(), Matchers.nullValue());
  }

  @Test
  public void testEditDocumentFromDto_shouldReturnChangedDocument() {
    EditDocumentDto editDocumentDto = EditDocumentDto
        .builder()
        .name("Plateformes de développement")
        .shared(true)
        .build();

    Document doc = Document
        .builder()
        .name("HTML Basics")
        .shared(false)
        .build();

    documentMapper.updateDocumentFromDto(editDocumentDto, doc);

    assertThat(doc.getName(), Matchers.is(editDocumentDto.getName()));
    assertThat(doc.isShared(), Matchers.is(editDocumentDto.isShared()));
    assertThat(doc.getTags(), Matchers.nullValue());
    assertThat(doc.getFile(), Matchers.nullValue());
  }

  @Test
  public void testCreateDocumentTagMapper_shouldReturnDocumentTag() {
    CreateDocumentTagDto createDocumentTagDto = CreateDocumentTagDto
        .builder()
        .name("1A")
        .build();

    DocumentTag docTag = documentMapper.createDocumentTag(createDocumentTagDto);

    assertThat(docTag.getName(), Matchers.is(createDocumentTagDto.getName()));
  }

  @Test
  public void testEditDocumentTagMapper_shouldReturnDocumentTag() {
    EditDocumentTagDto editDocumentTagDto = EditDocumentTagDto
        .builder()
        .name("1A")
        .build();

    DocumentTag docTag = DocumentTag.builder().name("2A").build();

    documentMapper.updateDocumentTagFromDto(editDocumentTagDto, docTag);

    assertThat(docTag.getName(), Matchers.is(editDocumentTagDto.getName()));
  }

}
