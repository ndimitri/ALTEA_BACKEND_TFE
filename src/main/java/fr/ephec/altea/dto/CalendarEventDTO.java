package fr.ephec.altea.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// FullCalendar event format
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarEventDTO {
    private Long id;
    private String title;
    private String start;  // ISO datetime
    private String end;    // ISO datetime
    private String color;
    private Boolean allDay;
    private CalendarEventExtendedProps extendedProps;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CalendarEventExtendedProps {
        private Long patientId;
        private String patientNom;
        private String lieu;
        private String statut;
        private String commentaire;
    }
}
