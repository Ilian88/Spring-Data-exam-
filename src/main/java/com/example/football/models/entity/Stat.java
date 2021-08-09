package com.example.football.models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;

@Entity
@Table(name = "stats")
public class Stat extends BaseEntity{

    private Double shooting;

    private Double passing;

    private Double endurance;

    public Stat() {
    }

    @Column(name = "shooting",nullable = false,scale = 2)
    @Min(value = 1)
    public Double getShooting() {
        return shooting;
    }

    public void setShooting(Double shooting) {
        this.shooting = shooting;
    }

    @Column(name = "passing",nullable = false,scale = 2)
    @Min(value = 1)
    public Double getPassing() {
        return passing;
    }

    public void setPassing(Double passing) {
        this.passing = passing;
    }

    @Column(name = "endurance",nullable = false,scale = 2)
    @Min(value = 1)
    public Double getEndurance() {
        return endurance;
    }

    public void setEndurance(Double endurance) {
        this.endurance = endurance;
    }
}
