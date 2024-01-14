package com.rightdata.explorer.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ExplorerItemResponse {
	
	private Long id;
    private String name;
    private String type;
    private ArrayList<ExplorerItemResponse> children;

}
