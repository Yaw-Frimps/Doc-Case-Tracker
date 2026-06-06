package org.codewithzea.doccasetracker.util;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.entity.Specialty;
import org.codewithzea.doccasetracker.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public Specialty getOrCreateSpecialty(String name) {

        return specialtyRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> {
                    Specialty specialty = Specialty.builder()
                            .name(name.trim())
                            .build();
                    return specialtyRepository.save(specialty);
                });
    }
}
