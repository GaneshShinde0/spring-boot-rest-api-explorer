package com.rightdata.explorer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rightdata.explorer.model.ExplorerItem;
import com.rightdata.explorer.model.ExplorerItemResponse;
import com.rightdata.explorer.service.IExplorerService;

@RestController
@RequestMapping("/api/explorer")
public class ExplorerController {

    @Autowired
    private IExplorerService explorerService;

    @GetMapping()
    public ResponseEntity<List<ExplorerItemResponse>> getExplorerItems() {
        List<ExplorerItemResponse> explorerItems = explorerService.getExplorer();
        return new ResponseEntity<>(explorerItems, HttpStatus.OK);
    }

    @PostMapping("/items")
    public ResponseEntity<ExplorerItem> createItem(
            @RequestBody ExplorerItem explorerItem
    ) {
        ExplorerItem newItem = explorerService.createItem(explorerItem);
        return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        explorerService.deleteItemRecursively(itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
