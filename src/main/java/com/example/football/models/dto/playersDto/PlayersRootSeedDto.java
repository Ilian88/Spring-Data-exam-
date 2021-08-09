package com.example.football.models.dto.playersDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "players")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayersRootSeedDto {

    @XmlElement(name = "player")
    private List<PlayerSeedDto> playerSeedDtos;

    public PlayersRootSeedDto() {
    }

    public List<PlayerSeedDto> getPlayerSeedDtos() {
        return playerSeedDtos;
    }

    public void setPlayerSeedDtos(List<PlayerSeedDto> playerSeedDtos) {
        this.playerSeedDtos = playerSeedDtos;
    }
}
