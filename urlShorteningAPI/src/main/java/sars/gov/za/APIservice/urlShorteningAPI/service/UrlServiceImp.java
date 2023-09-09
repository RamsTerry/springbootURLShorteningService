package sars.gov.za.APIservice.urlShorteningAPI.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.hash.Hashing;

import io.micrometer.common.util.StringUtils;
import sars.gov.za.APIservice.urlShorteningAPI.model.Url;
import sars.gov.za.APIservice.urlShorteningAPI.model.UrlDto;
import sars.gov.za.APIservice.urlShorteningAPI.repository.UrlRepository;

@Component
public class UrlServiceImp implements UrlService {

	@Autowired
	private UrlRepository urlRepository;

	// method to convert url to hashing encorded short url
	private String encodedUrl(String url) {
		String encodedUrl = "";
		LocalDateTime time = LocalDateTime.now();
		encodedUrl = Hashing.murmur3_32().hashString(url.concat(time.toString()), StandardCharsets.UTF_8).toString();

		return encodedUrl;
	}

	// control expiration date if user did not provide
	private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creationDate) {
		// if the user did not privide expiration date
		if (StringUtils.isBlank(expirationDate)) {
//			return creationDate.plusDays(2);
			return creationDate.plusSeconds(60);
		}

		// if user provide expiration date
		LocalDateTime expirationDateRet = LocalDateTime.parse(expirationDate);
		return expirationDateRet;
	}

	@Override
	public Url generateShortLink(UrlDto urlDto) {
		// if url is not empty
		if (StringUtils.isNotEmpty(urlDto.getUrl())) {
			String encodedUrl = encodedUrl(urlDto.getUrl());
			Url urlToPersist = new Url();
			urlToPersist.setCreationDate(LocalDateTime.now());
			urlToPersist.setOriginalUrl(urlDto.getUrl());
			urlToPersist.setShortLink(encodedUrl);
			urlToPersist
					.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(), urlToPersist.getCreationDate()));
			Url urlToReturn = persistShortLink(urlToPersist);

			if (urlToReturn != null) {
				return urlToReturn;
			}
			return null;
		}
		return null;
	}

	@Override
	public Url persistShortLink(Url url) {
		// this will save to database
		Url urlToRet = urlRepository.save(url);
		return urlToRet;
	}

	@Override
	public Url getEncodedUrl(String url) {
		// find url by shortlink
		Url urlToRet = urlRepository.findByShortLink(url);
		return urlToRet;
	}

	@Override
	public void deleteShortLink(Url url) {
		// detele url
		urlRepository.delete(url);
	}

}
