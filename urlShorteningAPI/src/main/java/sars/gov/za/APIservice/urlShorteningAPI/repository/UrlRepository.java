package sars.gov.za.APIservice.urlShorteningAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sars.gov.za.APIservice.urlShorteningAPI.model.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

	public Url findByShortLink(String shortLink);
}
