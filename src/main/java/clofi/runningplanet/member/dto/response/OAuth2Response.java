package clofi.runningplanet.member.dto.response;

public interface OAuth2Response {

	String getProvider();

	String getProviderId();

	String getEmail();

	String getName();

	String getProfileImage();
}