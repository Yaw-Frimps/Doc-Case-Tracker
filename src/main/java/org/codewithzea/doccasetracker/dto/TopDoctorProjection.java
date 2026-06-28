package org.codewithzea.doccasetracker.dto;

public interface TopDoctorProjection {
    String getDoctorId();
    String getDoctorName();
    Long getTotalTests();
    Long getTotalCases();
}
