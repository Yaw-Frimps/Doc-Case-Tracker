package org.codewithzea.doccasetracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "specialties",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_specialty_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "specialty_id")
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}