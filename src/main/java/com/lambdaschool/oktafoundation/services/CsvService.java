package com.lambdaschool.oktafoundation.services;


import org.springframework.web.multipart.MultipartFile;


public interface CsvService {
	void save(MultipartFile file);
}
