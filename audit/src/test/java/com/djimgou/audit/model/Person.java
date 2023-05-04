package com.djimgou.audit.model;

import com.djimgou.audit.annotations.IgnoreOnAudit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person {
    @IgnoreOnAudit
    String nom;
    @IgnoreOnAudit
    String prenom;
    @IgnoreOnAudit
    int age;
    String pays;
}
