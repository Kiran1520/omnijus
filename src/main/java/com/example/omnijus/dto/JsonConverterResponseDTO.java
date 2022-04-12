package com.example.omnijus.dto;

public class JsonConverterResponseDTO {
	
	private String message;
	private String fileName;
	private String filePathDestination;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePathDestination() {
		return filePathDestination;
	}
	public void setFilePathDestination(String filePathDestination) {
		this.filePathDestination = filePathDestination;
	}
}
