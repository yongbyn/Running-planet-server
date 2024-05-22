package clofi.runningplanet.member.dto;

public interface OAuth2Response {

	String getProvider();

	String getProviderId();

	String getEmail();

	String getName();

	String getProfileImage();
}