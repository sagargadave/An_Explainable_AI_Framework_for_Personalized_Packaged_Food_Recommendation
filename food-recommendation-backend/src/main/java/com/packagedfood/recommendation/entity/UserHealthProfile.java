package com.packagedfood.recommendation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_health_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserHealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Double weightKg;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_health_conditions",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "health_condition_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_profile_condition",
                    columnNames = {
                            "profile_id",
                            "health_condition_id"
                    }
            )
    )
    private Set<HealthCondition> healthConditions = new HashSet<>();

    public UserHealthProfile(
            Integer age,
            Double weightKg,
            Set<HealthCondition> healthConditions) {

        this.age = age;
        this.weightKg = weightKg;
        this.healthConditions = healthConditions;
    }
}