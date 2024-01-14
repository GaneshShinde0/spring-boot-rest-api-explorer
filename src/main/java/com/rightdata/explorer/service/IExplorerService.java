package com.rightdata.explorer.service;

import java.util.List;

import com.rightdata.explorer.model.ExplorerItem;
import com.rightdata.explorer.model.ExplorerItemResponse;

public interface IExplorerService {

    ExplorerItem createItem(ExplorerItem explorerItem);

    void deleteItem(Long itemId);

    List<ExplorerItemResponse> getExplorer();

    ExplorerItem findItemById(Long itemId);

    void deleteItemRecursively(Long itemId);

//	ExplorerItem findItemByParentId(Long itemId);

	ExplorerItem deleteAllItemByParentId(Long itemId);
}
