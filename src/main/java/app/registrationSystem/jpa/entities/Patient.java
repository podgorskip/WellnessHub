package app.registrationSystem.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "patient", schema = "registration_system")
@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Patient {
    @Id
    @Setter(value = AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "patient_illness",
            joinColumns = {@JoinColumn(name = "patient_id")},
            inverseJoinColumns = {@JoinColumn(name = "illness_id")}
    )
    private Set<Illness> illnesses;

    @OneToMany(mappedBy = "patient")
    private List<ScheduledVisit> scheduledVisits;

    @OneToMany(mappedBy = "patient")
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "patient")
    private List<Question> questions;

    @OneToMany(mappedBy = "patient")
    private List<VisitHistory> visitHistories;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        return Objects.equals(id, patient.id) && user.equals(patient.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user);
    }
}
