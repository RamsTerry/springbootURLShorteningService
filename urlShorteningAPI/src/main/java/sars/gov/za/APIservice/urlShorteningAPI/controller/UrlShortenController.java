package sars.gov.za.APIservice.urlShorteningAPI.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import sars.gov.za.APIservice.urlShorteningAPI.model.Url;
import sars.gov.za.APIservice.urlShorteningAPI.model.UrlDto;
import sars.gov.za.APIservice.urlShorteningAPI.model.UrlErrorResponseDto;
import sars.gov.za.APIservice.urlShorteningAPI.model.UrlResponseDto;
import sars.gov.za.APIservice.urlShorteningAPI.service.UrlService;

@RestController
public class UrlShortenController {

	@Autowired
	private UrlService urlService;

	@PostMapping("/genarate")
	public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto) {
		Url urlToReturn = urlService.generateShortLink(urlDto);

		if (urlToReturn != null) {
			UrlResponseDto urlResponseDto = new UrlResponseDto();
			urlResponseDto.setOriginalUrl(urlToReturn.getOriginalUrl());
			urlResponseDto.setExpirationDate(urlToReturn.getExpirationDate());
			urlResponseDto.setShortUrl(urlToReturn.getShortLink());
			// return response of type urlresponseDTO
			return new ResponseEntity<UrlResponseDto>(urlResponseDto, HttpStatus.OK);
		}
		UrlErrorResponseDto errorResponseDto = new UrlErrorResponseDto();
		errorResponseDto.setStatus("404");
		errorResponseDto.setError("There was an error processing your request, please try again.");
		// return response of type UrlErrorResponseDto
		return new ResponseEntity<UrlErrorResponseDto>(errorResponseDto, HttpStatus.OK);
	}

	@GetMapping("/{shortLink}")
	public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink, HttpServletResponse response)
			throws IOException {
		// check if that url exist or not
		if (StringUtils.isEmpty(shortLink)) {
			UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
			urlErrorResponseDto.setError("Invalid Url");
			urlErrorResponseDto.setStatus("400");
			return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
		}
		Url urlToRet = urlService.getEncodedUrl(shortLink);

		if (urlToRet == null) {
			UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
			urlErrorResponseDto.setError("Url does not exist or it might have expired!");
			urlErrorResponseDto.setStatus("400");
			return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
		}
		// if url expired or not
		if (urlToRet.getExpirationDate().isBefore(LocalDateTime.now())) {
			//if it expire it should delete it also on the database
			urlService.deleteShortLink(urlToRet);
			UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
			urlErrorResponseDto.setError("Url Expired. Please try generating a new one.");
			urlErrorResponseDto.setStatus("200");
			return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto, HttpStatus.OK);
		}

		response.sendRedirect(urlToRet.getOriginalUrl());
		return null;
	}
}
