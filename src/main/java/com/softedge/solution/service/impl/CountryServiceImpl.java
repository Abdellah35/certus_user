package com.softedge.solution.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.softedge.solution.contractmodels.CountryCM;
import com.softedge.solution.exceptionhandlers.custom.bean.CountryException;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeCommonEnum;
import com.softedge.solution.exceptionhandlers.errorcodes.ErrorCodeCountryEnum;
import com.softedge.solution.repomodels.Country;
import com.softedge.solution.repository.CountryRepository;
import com.softedge.solution.repository.impl.CountryRepositoryImpl;
import com.softedge.solution.service.CountryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CountryServiceImpl implements CountryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    CountryRepositoryImpl countryRepositoryImpl;

    @Override
    @Transactional(readOnly = true)
    public CountryCM getCountryByCountryCode(String countryCode) throws CountryException {
        if (StringUtils.isNotEmpty(countryCode)) {
            try {
                CountryCM countryCM = countryRepositoryImpl.getCountryByCountryCode(countryCode);
                if (countryCM == null) {
                    throw new CountryException(ErrorCodeCountryEnum.COUNTRY_CODE_INVALID, ErrorCodeCountryEnum.COUNTRY_CODE_INVALID.getName());
                }
                return countryCM;
            } catch (EmptyResultDataAccessException e) {
                logger.error("Empty access :: {}", e);
                throw new CountryException(ErrorCodeCountryEnum.COUNTRY_CODE_INVALID, ErrorCodeCountryEnum.COUNTRY_CODE_INVALID.getName());
            } catch (DataAccessException e) {
                logger.error("Data access :: {}", e);
                throw new CountryException(ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR, ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR.getName());
            } catch (Exception e) {
                logger.error("The error :: {}", e);
                throw new CountryException(ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR, ErrorCodeCommonEnum.INTERNAL_SERVER_ERROR.getName());
            }
        } else {
            throw new CountryException(ErrorCodeCountryEnum.COUNTRY_CODE_INVALID, ErrorCodeCountryEnum.COUNTRY_CODE_INVALID.getName());
        }
    }


    public ResponseEntity<?> getCountries() throws CountryException {
        return new ResponseEntity<>(countryRepositoryImpl.getAllCountries(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> loadCountries() throws CountryException, URISyntaxException, IOException {
        List<Country> countries = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(
                this.getClass().getClassLoader().getResource("json/country.json").getFile()
        ));
        for (JsonNode node : jsonNode) {
            ((ObjectNode) node).remove("unicode");
        }
//        objectMapper.writeValue(new File("data1.json"), jsonNode);
        countries = objectMapper.convertValue(jsonNode, new TypeReference<List<Country>>() {
        });
        logger.info("countries -> {}", countries.toString());
        File file = new File(
                this.getClass().getClassLoader().getResource("json/phone_code.json").getFile()
        );

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> phoneCodeMap = mapper.readValue(file, Map.class);
        logger.info("phoneCodeMap :: {}", phoneCodeMap);
        for (Country country : countries) {
          /*  if (country.getCountryName() != null)
                country.setCountryName(new String(country.getCountryName().getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8")));
            if (country.getCountryCode() != null)
                country.setCountryCode(new String(country.getCountryCode().getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8")));
            if (country.getCountryLogo() != null)
                country.setCountryLogo(new String(country.getCountryLogo().getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8")));
            if (phoneCodeMap.get(country.getCountryCode()) != null)
                country.setPhoneCode(new String(phoneCodeMap.get(country.getCountryCode()).getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8")));*/
            logger.info("country::{}", country.toString());
        }
        countryRepository.saveAll(countries);
        return new ResponseEntity<>("Success", HttpStatus.OK);

    }

    /*
        The resource URL is not working in the JAR
        If we try to access a file that is inside a JAR,
        It throws NoSuchFileException (linux), InvalidPathException (Windows)

        Resource URL Sample: file:java-io.jar!/json/file1.json
     */
    private File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }

    // print input stream
    private static void printInputStream(InputStream is) {

        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // print a file
    private static void printFile(File file) {

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
