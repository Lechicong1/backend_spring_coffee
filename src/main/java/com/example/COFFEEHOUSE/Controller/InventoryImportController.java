package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.InventoryImportReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.InventoryImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory-imports")
@RequiredArgsConstructor
public class InventoryImportController {

    private final InventoryImportService inventoryImportService;

    @PostMapping
    public ResponseEntity<ResponseData> create(@RequestBody InventoryImportReq request) {
        inventoryImportService.createImport(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Import created successfully")
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> update(@PathVariable Long id, @RequestBody InventoryImportReq request) {
        inventoryImportService.updateImport(id, request);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Import updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        inventoryImportService.deleteImport(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Import deleted successfully")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Import retrieved successfully")
                .data(inventoryImportService.findById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAll() {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Imports retrieved successfully")
                .data(inventoryImportService.findAll())
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Imports retrieved successfully")
                .data(inventoryImportService.search(keyword))
                .build());
    }
}
