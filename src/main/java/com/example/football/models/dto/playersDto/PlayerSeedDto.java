package com.example.football.models.dto.playersDto;

import com.example.football.models.entity.Stat;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.models.enums.Position;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement(name = "player")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerSeedDto {

    @XmlElement(name = "first-name")
    private String firstName;

    @XmlElement(name = "last-name")
    private String lastName;

    @XmlElement(name = "email")
    private String email;

    @XmlElement(name = "birth-date")
    private String birthDate;

    @XmlElement(name = "position")
    private String position;

    @XmlElement(name = "team")
    private TeamPlayerDto team;

    @XmlElement(name = "town")
    private TownPlayerDto town;

    @XmlElement(name = "stat")
    private StatPlayerDto stat;

    public PlayerSeedDto() {
    }

    @Size(min = 2)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Size(min = 2)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public TeamPlayerDto getTeam() {
        return team;
    }

    public void setTeam(TeamPlayerDto team) {
        this.team = team;
    }

    public TownPlayerDto getTown() {
        return town;
    }

    public void setTown(TownPlayerDto town) {
        this.town = town;
    }

    public StatPlayerDto getStat() {
        return stat;
    }

    public void setStat(StatPlayerDto stat) {
        this.stat = stat;
    }
}
