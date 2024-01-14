package com.rightdata.explorer.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rightdata.explorer.model.ExplorerItem;

public interface ExplorerItemRepository extends JpaRepository<ExplorerItem, Long> {
	ArrayList<ExplorerItem> findByParentIdIsNull();
	ArrayList<ExplorerItem> findByParentFolderPath(String parentFolderPath);
	ArrayList<ExplorerItem> findAllByParentId(Long itemId);
	Optional<ExplorerItem> deleteAllItemByParentId(Long itemId);
}