package com.awashbank.supply_chain.service;

import com.awashbank.supply_chain.jwt.TokenManager;
import com.awashbank.supply_chain.response.response;
import com.awashbank.supply_chain.response.serviceResponse;
import com.awashbank.supply_chain.user.model.UserDetailModel;
import com.awashbank.supply_chain.user.model.UserDetailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class Validation {

    private static final Logger log = LoggerFactory.getLogger(Validation.class);

    @Value("${file_upload_location}")
    private String path;

    @Autowired
    restRequest requestR;
    private final TokenManager tokenManager;
    private final UserDetailRepository userDetailRepository;

    public Validation(TokenManager tokenManager, UserDetailRepository userDetailRepository) {
        this.tokenManager = tokenManager;
        this.userDetailRepository = userDetailRepository;
    }


    public boolean checkForXssAttack(String value) {
        return value.trim().contains("&#") || value.trim().contains("document.") || value.trim().contains("alert(") || value.trim().contains("';") || value.trim().contains("<script") || value.trim().contains("/**/");
    }

    public String quetRemoverFromString(String s){
        String res = StringUtils.stripStart(s, "\"");
        return StringUtils.stripEnd(res, "\"");
    }

    public String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    public response xssResponse(){
        response rps = new response();
        rps.setStatusCode(400);
        rps.setMessage("The Parameter has XSS issues!!!");

        return rps;

    }

    public <T> JsonNode convertObjectToJsonNode(T object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(object);
            return mapper.readTree(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JsonNode", e);
        }
    }

    public <T> JsonNode convertObjectListToJsonNode(List<T> object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(object);
            return mapper.readTree(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JsonNode", e);
        }
    }

    public Long getUserId(String token){
        return Long.valueOf(tokenManager.extractUserId(token));
    }

   /* public String getName(String token){
        return tokenManager.getNameFromToken(token);
    }*/

    public String getUsername(String token){
        return tokenManager.extractUsername(token);
    }

    public <T> ResponseEntity<?> response(T object,serviceResponse resp) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        if (resp.getStatusCode() == 200){
            log.info("Request : " + mapper.writeValueAsString(object));
            log.info("Response : " + mapper.writeValueAsString(resp));
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }
        else{
            log.warn("Request : " + mapper.writeValueAsString(object));
            log.warn("Response : " + mapper.writeValueAsString(resp));
            return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> response(String urPlusRequest,serviceResponse resp) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        if (resp.getStatusCode() == 200){
            log.info("Request : " + urPlusRequest);
            log.info("Response : " + mapper.writeValueAsString(resp));
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }
        else{
            log.warn("Request : " + urPlusRequest);
            log.warn("Response : " + mapper.writeValueAsString(resp));
            return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
        }

    }

    public String phoneStandard(String phone) {
        try {
            if (phone.startsWith("0")) {
                phone = "251" + phone.substring(1, phone.length());
            } else if (phone.startsWith("9") || phone.startsWith("7")) {
                phone = "251" + phone;
            } else if (phone.startsWith("251")) {
                phone = "251" + phone.substring(3, phone.length());
            } else if (phone.startsWith("+251")) {
                phone = "251" + phone.substring(4, phone.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return phone;
    }

    public Optional<UserDetailModel> getUserDetailByToken(String token){
        return userDetailRepository.findById(getUserId(token));
    }

    public Optional<UserDetailModel> getUserDetailByUserId(Long userId){
        return userDetailRepository.findById(userId);
    }

    // Helper method to upload the file
    public String uploadFile(MultipartFile file,String location,String fileName) {
        try {
            // Define the upload directory (customize as needed)
            String uploadDir = path + "/" + location;
            Path uploadPath = Paths.get(uploadDir);

            // Create directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath,
                        PosixFilePermissions.asFileAttribute(
                                PosixFilePermissions.fromString("rwxrwxrwx")
                        ));
            }

            Path filePath = uploadPath.resolve(fileName);

            // Save the file
            file.transferTo(filePath.toFile());

            // Return the file path
            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Indicate failure
        }
    }

    public String saveFile(MultipartFile file,String location,String fileName) {
        String UPLOAD_DIR = path + "/" + location;;
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                // Create directory with 777 permissions (rwxrwxrwx)
                Files.createDirectories(uploadPath,
                        PosixFilePermissions.asFileAttribute(
                                PosixFilePermissions.fromString("rwxrwxrwx")
                        )
                );
            }

            //String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            return "PDF file uploaded successfully: " + fileName;

        } catch (IOException e) {
            return null; // Return null if upload fails
        }
    }

    public ResponseEntity<?> response(serviceResponse resp){

        if (resp.getStatusCode() == 200) return new ResponseEntity<>(resp, HttpStatus.OK);
        else return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<Resource> downloadFile(String filePaths) {
        try {
            Path filePath = Path.of(filePaths); //Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Set headers to display PDF in browser (inline) or force download (attachment)
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + resource.getFilename()); // Use "attachment" to force download
                headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
                headers.add(HttpHeaders.PRAGMA, "no-cache");
                headers.add(HttpHeaders.EXPIRES, "0");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
