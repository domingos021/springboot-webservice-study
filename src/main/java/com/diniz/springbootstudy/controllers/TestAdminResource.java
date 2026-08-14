package com.diniz.springbootstudy.resources;


import com.diniz.springbootstudy.services.reset.DatabaseResetService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping(value = "/test")
public class TestAdminResource {

    private final DatabaseResetService databaseResetService;

    public TestAdminResource(DatabaseResetService databaseResetService) {
        this.databaseResetService = databaseResetService;
    }

    @DeleteMapping(value = "/reset") // <-- Exige o verbo DELETE no Postman
    public ResponseEntity<String> resetDatabase() throws Exception {
        databaseResetService.resetDatabase();
        return ResponseEntity.ok("Database successfully reset and re-seeded!");
    }
}