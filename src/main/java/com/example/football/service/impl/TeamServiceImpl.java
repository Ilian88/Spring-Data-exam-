package com.example.football.service.impl;

import com.example.football.models.dto.TeamSeedDto;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.TeamRepository;
import com.example.football.service.TeamService;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class TeamServiceImpl implements TeamService {

    private static final String TEAMS_FILE_PATH = "src/main/resources/files/json/teams.json";

    private final TeamRepository teamRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final TownService townService;

    public TeamServiceImpl(TeamRepository teamRepository, Gson gson, ModelMapper modelMapper,
                           ValidationUtil validationUtil, TownService townService) {
        this.teamRepository = teamRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.townService = townService;
    }

    @Override
    public boolean areImported() {
        return this.teamRepository.count() > 0;
    }

    @Override
    public String readTeamsFileContent() throws IOException {
        return Files.readString(Path.of(TEAMS_FILE_PATH));
    }

    @Override
    public String importTeams() throws IOException {
        StringBuilder sb = new StringBuilder();

        TeamSeedDto[] teamSeedDtos = gson.fromJson(readTeamsFileContent(),TeamSeedDto[].class);

        Arrays.stream(teamSeedDtos)
                .filter(dto -> {
                    boolean isValid = validationUtil.isValid(dto)
                            && !checkIfTeamExistsInDb(dto.getName())
                            && checkIfTownExistInDb(dto.getTownName());

                    if (isValid) {
                        sb.append(String.format("Successfully imported Team %s - %d",
                                dto.getName(),
                                dto.getFanBase()));
                    }
                    else {
                        sb.append("Invalid Team");
                    }
                    sb.append(System.lineSeparator());

                    return isValid;
                })
                .map(dto -> {
                    Team team = modelMapper.map(dto,Team.class);
                    Town town = this.townService.findByName(dto.getTownName());

                    team.setTown(town);

                    return team;
                })
                .forEach(teamRepository::save);

        return sb.toString();
    }

    private boolean checkIfTownExistInDb(String town) {
        return this.townService.ifTownExistInDB(town);
    }

    @Override
    public boolean checkIfTeamExistsInDb(String name) {
        return this.teamRepository.existsByName(name);
    }

    @Override
    public Team findTeamByName(String name) {
        return this.teamRepository.findByName(name);
    }
}
