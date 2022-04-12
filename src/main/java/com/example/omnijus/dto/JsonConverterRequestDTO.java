package com.example.omnijus.dto;

public class JsonConverterRequestDTO {
	
	private String fileName;
	private String filePathOrigin;
	private String filePathDestination;
	
	
	public String getFilePathOrigin() {
		return filePathOrigin;
	}

	public void setFilePathOrigin(String filePathOrigin) {
		this.filePathOrigin = filePathOrigin;
	}

	public String getFilePathDestination() {
		return filePathDestination;
	}

	public void setFilePathDestination(String filePathDestination) {
		this.filePathDestination = filePathDestination;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
