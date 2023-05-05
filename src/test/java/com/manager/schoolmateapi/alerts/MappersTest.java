package com.manager.schoolmateapi.alerts;

import static org.hamcrest.MatcherAssert.*;
import java.util.List;
import static com.manager.schoolmateapi.mappers.AMapper.listToPoint;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;

import com.manager.schoolmateapi.alerts.dto.CreateAlertDto;
import com.manager.schoolmateapi.alerts.dto.EditAlertDto;
import com.manager.schoolmateapi.alerts.enumerations.AlertStatus;
import com.manager.schoolmateapi.alerts.enumerations.AlertType;
import com.manager.schoolmateapi.mappers.AMapper;

@SpringBootTest
public class MappersTest {

    @Autowired
    AMapper alertMapper;

    @Test
    public void testCreateAlertFromDto_shouldReturnAlertSuccesfully() {
        CreateAlertDto createAlertDto = new CreateAlertDto()
                .builder()
                .title("title")
                .description("description")
                .type(AlertType.ROBBERY)
                .coordinates(List.of(1.0, 1.0))
               // .status(AlertStatus.PENDING)
                .build();
        Alert alert = alertMapper.createDtoToAlert(createAlertDto);
        assert (alert.getTitle().equals(createAlertDto.getTitle()));
        assert (alert.getDescription().equals(createAlertDto.getDescription()));
        assert (alert.getType().equals(createAlertDto.getType()));
        assertThat(alert.getCoordinates(), Matchers.is(listToPoint(createAlertDto.getCoordinates())));
        //assert (alert.getStatus().equals(createAlertDto.getStatus()));

    }

    @Test
    public void testEditAlertFromDto_shouldReturnChagedAlert() {
        EditAlertDto editAlertDto = new EditAlertDto()
                .builder()
                .title("title")
                .description("description")
                .type(AlertType.ROBBERY)
                .coordinates(new Point(1, 1))
                .status(AlertStatus.PENDING)
                .build();
        // create an alert
        Alert alert = new Alert();
        alert.setTitle("old title");
        alert.setDescription("old description");
        alert.setType(AlertType.ROBBERY);
        alert.setCoordinates(new Point(2, 2));
        alert.setStatus(AlertStatus.PENDING);
        alertMapper.updateAlertFromDto(editAlertDto, alert);
        assert (alert.getTitle().equals(editAlertDto.getTitle()));
        assert (alert.getDescription().equals(editAlertDto.getDescription()));
        assert (alert.getType().equals(editAlertDto.getType()));
        assertThat(alert.getCoordinates(), Matchers.is(editAlertDto.getCoordinates()));
        assert (alert.getStatus().equals(editAlertDto.getStatus()));

    }
}
