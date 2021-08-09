package com.example.football.service.impl;

import com.example.football.models.dto.TownSeedDto;
import com.example.football.models.entity.Town;
import com.example.football.repository.TownRepository;
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
public class TownServiceImpl implements TownService {

    private static final String TOWNS_FILE_PATH = "src/main/resources/files/json/towns.json";

    private final TownRepository townRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public TownServiceImpl(TownRepository townRepository, Gson gson, ModelMapper modelMapper,
                           ValidationUtil validationUtil) {
        this.townRepository = townRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }


    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(Path.of(TOWNS_FILE_PATH));
    }

    @Override
    public String importTowns() throws IOException {
        StringBuilder sb = new StringBuilder();
        TownSeedDto[] townSeedDtos = gson.fromJson(readTownsFileContent(), TownSeedDto[].class);

        Arrays.stream(townSeedDtos)
                .filter(dto -> {
                    boolean isValid = validationUtil.isValid(dto)
                            && !ifTownExistInDB(dto.getName());

                    if (isValid) {
                        sb.append(String.format("Successfully imported Town %s - %d",
                                dto.getName(),
                                dto.getPopulation()));
                    } else {
                        sb.append("Invalid Town ");
                    }

                    sb.append(System.lineSeparator());

                    return isValid;
                })
                .map(dto -> modelMapper.map(dto, Town.class))
                .forEach(townRepository::save);

        return sb.toString();
    }

    @Override
    public boolean ifTownExistInDB(String townName) {
        return this.townRepository.existsByName(townName);
    }

    @Override
    public Town findByName(String name) {
        return this.townRepository.findByName(name);
    }

}
