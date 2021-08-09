package com.example.football.models.dto.playersDto;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "player")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatPlayerDto {

    @XmlElement(name = "id")
    private Long id;

    public StatPlayerDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
