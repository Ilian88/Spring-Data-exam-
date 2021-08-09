package com.example.football.service.impl;

import com.example.football.models.dto.playersDto.PlayersRootSeedDto;
import com.example.football.models.entity.Player;
import com.example.football.models.entity.Stat;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.models.enums.Position;
import com.example.football.repository.PlayerRepository;
import com.example.football.service.PlayerService;
import com.example.football.service.StatService;
import com.example.football.service.TeamService;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final String PLAYERS_FILE_PATH = "src/main/resources/files/xml/players.xml";

    private final PlayerRepository playerRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final TeamService teamService;
    private final TownService townService;
    private final StatService statService;

    public PlayerServiceImpl(PlayerRepository playerRepository, XmlParser xmlParser, ModelMapper modelMapper,
                             ValidationUtil validationUtil, TeamService teamService, TownService townService,
                             StatService statService) {
        this.playerRepository = playerRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.teamService = teamService;
        this.townService = townService;

        this.statService = statService;
    }

    @Override
    public boolean areImported() {
        return this.playerRepository.count() > 0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(Path.of(PLAYERS_FILE_PATH));
    }

    @Override
    public String importPlayers() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        PlayersRootSeedDto playersRootSeedDto = xmlParser.fromFile(PLAYERS_FILE_PATH, PlayersRootSeedDto.class);

        playersRootSeedDto.getPlayerSeedDtos()
                .stream()
                .filter(dto -> {
                    boolean isValid = validationUtil.isValid(dto)
                            && !checkIfPlayerEmailExist(dto.getEmail())
                            && checkIfTownExists(dto.getTown().getName())
                            && checkIfTeamExist(dto.getTeam().getName());

                    if (isValid) {
                        sb.append(String.format("Successfully imported Player %s %s - %s",
                                dto.getFirstName(),
                                dto.getLastName(),
                                dto.getPosition()));
                    } else {
                        sb.append("Invalid Player");
                    }

                    sb.append(System.lineSeparator());

                    return isValid;
                })
                .map(dto -> {
                    Player player = modelMapper.map(dto, Player.class);
                    player.setPosition(Position.valueOf(dto.getPosition()));
                    player.setBirthDate(LocalDate.parse(dto.getBirthDate(),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    Town town = this.townService.findByName(dto.getTown().getName());
                    Team team = this.teamService.findTeamByName(dto.getTeam().getName());
                    Stat stat = this.statService.findById(dto.getStat().getId());

                    player.setTown(town);
                    player.setTeam(team);
                    player.setStat(stat);

                    return player;
                })
                .forEach(playerRepository::save);

        return sb.toString();
    }
    private boolean checkIfTeamExist(String team) {
        return this.teamService.checkIfTeamExistsInDb(team);
    }

    private boolean checkIfTownExists(String town) {
        return this.townService.ifTownExistInDB(town);
    }

    private boolean checkIfPlayerEmailExist(String email) {
        return this.playerRepository.existsByEmail(email);
    }

    @Override
    public String exportBestPlayers() {
        StringBuilder sb = new StringBuilder();

        this.playerRepository.findAllPlayersOrderByShootingDescThenPassingDescThanEnduranceThenLastName()
                .forEach(p -> {
                    sb.append(String.format( "Player - %s %s\n" +
                            "\tPosition - %s\n" +
                            "\tTeam - %s\n" +
                            "\tStadium - %s",
                            p.getFirstName(),
                            p.getLastName(),
                            p.getPosition(),
                            p.getTeam().getName(),
                            p.getTeam().getStadiumName()))
                            .append(System.lineSeparator());
                });

        return sb.toString();
    }
}
