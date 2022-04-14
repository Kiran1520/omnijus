package com.example.omnijus.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.omnijus.dto.JsonConverterRequestDTO;
import com.example.omnijus.dto.JsonConverterResponseDTO;
import com.example.omnijus.dto.JsonFilesListRequestDTO;
import com.example.omnijus.dto.JsonFilesListResponseDTO;
import com.example.omnijus.utils.JsonConverterUtils;
import com.google.gson.Gson;

@RestController
public class JsonConverterController {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonConverterController.class);

	@Autowired
	private JsonConverterUtils jsonConverterUtils;

	@PostMapping("/files")
	public ResponseEntity<?> listJsonFiles(@RequestBody JsonFilesListRequestDTO request) {
		JsonFilesListResponseDTO response = new JsonFilesListResponseDTO();
		//validando se caminho inserido é válido
		logger.info("Validando caminho inserido na lista de arquivos: " + request.getFilePath());
		request.setFilePath(request.getFilePath().replace("\\", "/"));
		if(!jsonConverterUtils.isPathValid(request.getFilePath())) {
			response.setMessage("Caminho inválido ou não encontrado");
			return new ResponseEntity<JsonFilesListResponseDTO>(response, HttpStatus.NOT_FOUND);
		}
		logger.info("Retornando lista de arquivos json");
		Set<String> nameList = jsonConverterUtils.filesList(request.getFilePath());
		
		List<String> fileNameList = new ArrayList<>();
		for (String fileName : nameList) {
			if (fileName.contains(".json")) {
				fileNameList.add(fileName);
			}
		}
		response.setFileName(fileNameList);
		response.setFilePath(request.getFilePath());
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/conversions")
	public ResponseEntity<?> convertJsonFiles(@RequestBody JsonConverterRequestDTO request) {
		JsonConverterResponseDTO response = new JsonConverterResponseDTO();
		
		logger.info("Validando caminho inserido de origem de arquivo e destino: " + request.getFilePathOrigin() + " --- " + request.getFilePathDestination());
		//validando se caminho inserido é válido
		if(!jsonConverterUtils.isPathValid(request.getFilePathDestination()) || !jsonConverterUtils.isPathValid(request.getFilePathOrigin())) {
			response.setMessage("Caminho inválido ou não encontrado");
			return new ResponseEntity<JsonConverterResponseDTO>(response, HttpStatus.NOT_FOUND);
		}
		request.setFilePathDestination(request.getFilePathDestination().replace("\\", "/"));
		request.setFilePathOrigin(request.getFilePathOrigin().replace("\\", "/"));
		Set<String> nameList = jsonConverterUtils.filesList(request.getFilePathOrigin());
		Gson gson = new Gson();
		boolean fileFound = false;
		// isso pode ser otimizado salvando em sessão a lista de arquivos (em um redis,
		// mongo, ou na propria memoria do spring boot)
		logger.info("Validando se o arquivo inserido existe: " + request.getFileName());
		for (String fileName : nameList) {
			if (fileName.equals(request.getFileName())) {
				fileFound = true;
				break;
			}
		}
		
		if (!fileFound) {
			response.setMessage("Arquivo não encontrado");
			return new ResponseEntity<JsonConverterResponseDTO>(response, HttpStatus.NOT_FOUND);
		}

		try {
			
			String jsonString = new String(Files.readAllBytes(Paths.get(request.getFilePathOrigin() + "/" + request.getFileName())));
			logger.info("Resgatando informações do json do arquivo");
			jsonString = jsonString.replace("\n", "");
			jsonString = jsonString.replace("\r", "");
			
			logger.info("Validação de json");
			if(!jsonConverterUtils.isJsonValid(gson, jsonString)) {
				response.setMessage("Arquivo JSON inválido");
				return new ResponseEntity<JsonConverterResponseDTO>(response, HttpStatus.NOT_ACCEPTABLE);
			}
			String csvFileType = request.getFileName().replace(".json", ".csv");
			logger.info("Convertendo json em csv");
			jsonConverterUtils.convertToCsv(jsonString, request.getFilePathDestination(), csvFileType);
			response.setMessage("Arquivo convertido com sucesso!!");
			response.setFilePathDestination(request.getFilePathDestination());
			response.setFileName(csvFileType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().body(response);
	}

}
