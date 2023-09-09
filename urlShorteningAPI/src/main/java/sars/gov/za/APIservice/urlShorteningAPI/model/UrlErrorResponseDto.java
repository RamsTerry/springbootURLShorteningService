package sars.gov.za.APIservice.urlShorteningAPI.model;

public class UrlErrorResponseDto {
// this class will help if there is a failer in url shortening or response to user
	
	private String status;
	private String error;
	
	
	public UrlErrorResponseDto(String status, String error) {
		super();
		this.status = status;
		this.error = error;
	}
	
	public UrlErrorResponseDto() {
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "UrlErrorResponseDto [status=" + status + ", error=" + error + "]";
	}
		
}
