package com.example.omnijus.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Component
public class JsonConverterUtils {
	public Set<String> filesList(String filePath) {
		Set<String> nameList = Stream.of(new File(filePath).listFiles()).filter(file -> !file.isDirectory())
				.map(File::getName).collect(Collectors.toSet());
		return nameList;
	}

	public boolean isJsonValid(Gson gson, String jsonString) {
		try {
			gson.fromJson(jsonString, Object.class);
			Object jsonObjType = gson.fromJson(jsonString, Object.class).getClass();
			if (jsonObjType.equals(String.class)) {
				return false;
			}
			return true;
		} catch (JsonSyntaxException ex) {
			return false;
		}
	}
	
	public void convertToCsv(String json, String filePath, String fileName) throws FileNotFoundException {
		String jsonFormatted = json.substring(1, json.length() -1);
		jsonFormatted = jsonFormatted.trim();
		jsonFormatted = jsonFormatted.replace(":", ",");
		jsonFormatted = jsonFormatted.replace("\"", "");
		jsonFormatted = jsonFormatted.replace("[", "");
		jsonFormatted = jsonFormatted.replace("]", "");
		
		String[] jsonSplitted = jsonFormatted.split(",");
		
		List<String[]> csvFields = new ArrayList<>();
		csvFields.add(jsonSplitted);
		
	    File csvOutputFile = new File(filePath + "\\" + fileName);
	    try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
	    	csvFields.stream()
	          .map(this::convertToCSV)
	          .forEach(pw::println);
	    }
	}
	
	
	private String convertToCSV(String[] data) {
	    return Stream.of(data)
	      .map(this::formattingEscapes)
	      .collect(Collectors.joining(","));
	}
	
	//Removendo \ invertida
	private String formattingEscapes(String formattedJson) {
	    String jsonFieldToFormat = formattedJson.replaceAll("\\R", " ");
	    jsonFieldToFormat = jsonFieldToFormat.trim();
	    if (formattedJson.contains(",") || formattedJson.contains("\"") || formattedJson.contains("'")) {
	    	formattedJson = formattedJson.replace("\"", "\"\"");
	        jsonFieldToFormat = "\"" + formattedJson + "\"";
	    }
	    return jsonFieldToFormat;
	}
	
	public boolean isPathValid(String path) {

		try {
            Paths.get(path);
        } catch (InvalidPathException ex) {
            return false;
        }
        return true;
    }

}
