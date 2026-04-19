package com.devsuv.customer.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, unique = true)
    private String identification;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;
}
