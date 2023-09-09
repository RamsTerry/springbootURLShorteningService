package sars.gov.za.APIservice.urlShorteningAPI.service;

import sars.gov.za.APIservice.urlShorteningAPI.model.Url;
import sars.gov.za.APIservice.urlShorteningAPI.model.UrlDto;

public interface UrlService {
	
	public Url generateShortLink(UrlDto urlDto);
	
	public Url persistShortLink(Url url);
	
	public Url getEncodedUrl(String url);
	
	public void deleteShortLink(Url url);

}
