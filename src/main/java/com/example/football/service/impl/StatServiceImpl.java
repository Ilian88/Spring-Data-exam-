package com.example.football.service.impl;

import com.example.football.models.dto.StatSeedRootDto;
import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class StatServiceImpl implements StatService {

    private static final String STATS_FILE_PATH = "src/main/resources/files/xml/stats.xml";

    private final StatRepository statRepository;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    public StatServiceImpl(StatRepository statRepository, XmlParser xmlParser, ValidationUtil validationUtil,
                           ModelMapper modelMapper) {
        this.statRepository = statRepository;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.statRepository.count() > 0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        return Files.readString(Path.of(STATS_FILE_PATH));
    }

    @Override
    public String importStats() throws JAXBException, FileNotFoundException {
        StatSeedRootDto statSeedRootDto = xmlParser.fromFile(STATS_FILE_PATH,StatSeedRootDto.class);
        StringBuilder sb = new StringBuilder();

        statSeedRootDto.getStatSeedDtoList()
                .stream()
                .filter(dto -> {
                    boolean isValid = validationUtil.isValid(dto)
                            && !statsExistInDb(dto.getPassing(),dto.getShooting(),dto.getEndurance());

                    if (isValid) {
                        sb.append(String.format("Successfully imported Stat %.2f - %.2f - %.2f",
                                dto.getPassing(),
                                dto.getShooting(),
                                dto.getEndurance()));
                    } else {
                        sb.append("Invalid Stat");
                    }

                    sb.append(System.lineSeparator());

                    return isValid;
                })
                .map(dto -> modelMapper.map(dto, Stat.class))
                .forEach(statRepository::save);

        return sb.toString();
    }


    @Override
    public Stat findById(Long id) {
        return this.statRepository.getById(id);
    }

    private boolean statsExistInDb(Double endurance, Double passing, Double shooting) {
        return this.statRepository.existsByPassingAndShootingAndEndurance(endurance,passing,shooting);
    }
}
