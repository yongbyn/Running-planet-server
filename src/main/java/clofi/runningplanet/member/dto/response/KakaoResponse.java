package clofi.runningplanet.member.dto.response;

import java.util.Map;


public class KakaoResponse implements OAuth2Response{

	private final Map<String, Object> attributes;
	private final Map<String, Object> properties;
	private final Map<String, Object> kakaoAccount;

	public KakaoResponse(Map<String, Object> attributes) {
		this.attributes = attributes;
		this.properties = (Map<String, Object>)attributes.get("properties");
		this.kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getProviderId() {
		return attributes.get("id").toString();
	}

	@Override
	public String getEmail() {
		if (kakaoAccount.containsKey("email")) {
			return kakaoAccount.get("email").toString();
		}
		return null;
	}

	@Override
	public String getName() {
		if (properties.containsKey("nickname")) {
			return properties.get("nickname").toString();
		}
		return null;
	}

	@Override
	public String getProfileImage() {
		Object profileImage = properties.get("profile_image");
		return profileImage != null ? profileImage.toString() : null;
	}
}
