package com.rightdata.explorer.service;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rightdata.explorer.exception.ItemAlreadyExistsException;
import com.rightdata.explorer.model.ExplorerItem;
import com.rightdata.explorer.model.ExplorerItemResponse;
import com.rightdata.explorer.repository.ExplorerItemRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class ExplorerServiceImpl implements IExplorerService {

	@Autowired
	private ExplorerItemRepository explorerItemRepository;

	private static final Logger log = LoggerFactory.getLogger(ExplorerServiceImpl.class);

	private static final String ROOT_DIRECTORY = System.getProperty("user.dir");
	private static final String MAIN_ROOT_FOLDER_NAME = "root";
	private static final String FOLDER = "folder";

	@PostConstruct
	public void initializeRootFolder() {
		// Check if a root folder already exists
		if (!isItemExists(MAIN_ROOT_FOLDER_NAME, null)) {
			// Create a new ExplorerItem for the root folder
			ExplorerItem rootFolder = new ExplorerItem(MAIN_ROOT_FOLDER_NAME, FOLDER);
			rootFolder.setParentFolderPath("root");
			// Save the root folder to the database
			rootFolder = explorerItemRepository.save(rootFolder);

			// Handle file system creation if needed
			createFolderInFileSystem(rootFolder, null);
		}
	}

	@Override
	@Transactional
	public ExplorerItem createItem(ExplorerItem explorerItem) {
		String itemName = explorerItem.getName();
		String itemType = explorerItem.getType();
		String parentFolderPath = explorerItem.getParentFolderPath();

		// Check if an item with the same name already exists
		if (isItemExists(itemName, parentFolderPath)) {
			throw new ItemAlreadyExistsException("Item with the same name already exists.");
		}

		// Save the item to the database
		explorerItem = explorerItemRepository.save(explorerItem);

		// Handle file system creation if needed
		if (FOLDER.equals(itemType)) {
			createFolderInFileSystem(explorerItem, parentFolderPath);
		} else {
			createFileInFileSystem(explorerItem, parentFolderPath);
		}

		return explorerItem;
	}

	private boolean isItemExists(String itemName, String parentFolderPath) {
		ArrayList<ExplorerItem> targetList = (parentFolderPath != null)
				? explorerItemRepository.findByParentFolderPath(parentFolderPath)
				: null;

		if (targetList == null || targetList.isEmpty()) {
			return false;
		}

		List<ExplorerItem> matchingItem = targetList.stream().filter(item -> item.getName().equals(itemName))
				.collect(Collectors.toList());

		return !matchingItem.isEmpty();
	}

	private void createFolderInFileSystem(ExplorerItem folder, String parentPath) {
		try {
			Path parentFolderPath = getParentFolderPath(folder, parentPath);

			if (!Files.exists(parentFolderPath)) {
				Files.createDirectories(parentFolderPath);
			} else {
				throw new ItemAlreadyExistsException("Folder already exists");
			}
		} catch (Exception e) {
			log.info("Error In createFolderInFileSystem: {}", e.getMessage());
		}
	}

	private Path getParentFolderPath(ExplorerItem folder, String parentPath) {
		Path parentFolderPath;
		if (parentPath != null) {
			parentFolderPath = Paths.get(ROOT_DIRECTORY, MAIN_ROOT_FOLDER_NAME, parentPath, folder.getName());
		} else {
			parentFolderPath = Paths.get(ROOT_DIRECTORY, MAIN_ROOT_FOLDER_NAME, folder.getName());
		}

		return parentFolderPath;
	}

	private void createFileInFileSystem(ExplorerItem file, String parentPath) {
		try {
			Path parentFolderPath = getParentFolderPath(file, parentPath);

			if (!Files.exists(parentFolderPath)) {
				Files.createFile(parentFolderPath);
			} else {
				throw new ItemAlreadyExistsException("File already exists");
			}
		} catch (Exception e) {
			log.info("Error In createFileInFileSystem: {}", e.getMessage());
		}
	}

	@Override
	@Transactional
	public void deleteItem(Long itemId) {
		deleteFolderRecursively(itemId);
		explorerItemRepository.deleteById(itemId);
	}

	@Override
	public ExplorerItem findItemById(Long itemId) {
		return explorerItemRepository.findById(itemId).orElse(null);
	}
	@Override
	public ExplorerItem deleteAllItemByParentId(Long itemId) {
		
		return explorerItemRepository.deleteAllItemByParentId(itemId).orElse(null);
	}
	
	
	@Override
	public List<ExplorerItemResponse> getExplorer() {
		List<ExplorerItem> topLevelFolders = explorerItemRepository.findByParentIdIsNull();
		ArrayList<ExplorerItemResponse> response = new ArrayList<>();
		List<ExplorerItem> nestedExplorer = new ArrayList<>();
		for (ExplorerItem folder : topLevelFolders) {
			ExplorerItemResponse obj = new ExplorerItemResponse();
			obj.setId(folder.getId());
			obj.setName(folder.getName());
			obj.setType(folder.getType());
			obj.setChildren(getChildrenRecursively(folder));
			response.add(obj);
		}
		
		return response;
	}

	// Completely Done
	private ArrayList<ExplorerItemResponse> getChildrenRecursively(ExplorerItem folder) {
        ArrayList<ExplorerItem> children = explorerItemRepository.findAllByParentId(folder.getId());
        ArrayList<ExplorerItemResponse> recursiveResponse = new ArrayList<>();

        if (children != null && !children.isEmpty()) {
            for (ExplorerItem child : children) {
                ExplorerItemResponse recursiveObj = new ExplorerItemResponse();
                recursiveObj.setId(child.getId());
                recursiveObj.setName(child.getName());
                recursiveObj.setType(child.getType());
                recursiveObj.setChildren(getChildrenRecursively(child));
                recursiveResponse.add(recursiveObj);
            }
        }

        return recursiveResponse;
    }

	@Override
	@Transactional
	public void deleteItemRecursively(Long itemId) {
		explorerItemRepository.deleteAllItemByParentId(itemId)
				.orElseThrow(() -> new ItemAlreadyExistsException("Item not found"));
	}
	
	private void deleteFolderRecursively(Long folderId) {
        try {
            // Assuming you have a method to get the ExplorerItem based on the LongId
            ExplorerItem folder = explorerItemRepository.findById(folderId).orElse(null);

            if (folder != null) {
                Path folderPath = getParentFolderPath(folder, folder.getParentFolderPath());

                if (Files.exists(folderPath)) {
                    Files.walkFileTree(folderPath, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws java.io.IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws java.io.IOException {
                            // Handle the exception during file visit failure if needed
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws java.io.IOException {
                            if (exc == null) {
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            } else {
                                // Directory iteration failed; propagate exception
                                throw exc;
                            }
                        }
                    });
                } else {
                    throw new NoSuchFileException("Folder does not exist");
                }
            } else {
                throw new NoSuchFileException("Folder not found with the given ID");
            }
        } catch (Exception e) {
            log.info("Error In deleteFolderRecursively: {}", e.getMessage());
        }
    }
}
