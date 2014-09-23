package dao;

public enum BucketType {
	COUNTER("counter", ""),
	USER("user", ""),
	ARTICLE("article", ""),
	LIKE("like", ""),
	
	;
	
	private String code;
	
	private String password;
	
	private BucketType(String code, String password) {
		this.code = code;
		this.password = password;
	}
	
	public String code() {
		return code;
	}
	
	public String password() {
		return password;
	}
}
