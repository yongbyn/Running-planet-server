package clofi.runningplanet.member.service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.common.service.S3StorageManagerUseCase;
import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.domain.OAuthType;
import clofi.runningplanet.member.domain.SocialLogin;
import clofi.runningplanet.member.dto.CustomOAuth2User;
import clofi.runningplanet.member.dto.request.CreateOnboardingRequest;
import clofi.runningplanet.member.dto.request.UpdateProfileRequest;
import clofi.runningplanet.member.dto.response.GoogleResponse;
import clofi.runningplanet.member.dto.response.KakaoResponse;
import clofi.runningplanet.member.dto.response.NaverResponse;
import clofi.runningplanet.member.dto.response.OAuth2Response;
import clofi.runningplanet.member.dto.response.ProfileResponse;
import clofi.runningplanet.member.dto.response.SelfProfileResponse;
import clofi.runningplanet.member.dto.response.UpdateProfileResponse;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.member.repository.SocialLoginRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService extends DefaultOAuth2UserService {
	private final MemberRepository memberRepository;
	private final CrewMemberRepository crewMemberRepository;
	private final SocialLoginRepository socialLoginRepository;
	private final S3StorageManagerUseCase s3StorageManagerUseCase;

	@Value("${spring.profiles.default}")
	private String activeProfile;

	@Value("${default.profile.image}")
	private String defaultProfileImage;

	@PostConstruct
	public void init() {
		if ("prod".equals(activeProfile)) {
			SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
			factory.setProxy(proxy);
			RestTemplate proxyRestTemplate = new RestTemplate(factory);
			proxyRestTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
			this.setRestOperations(proxyRestTemplate);
		}
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		OAuth2Response oAuth2Response;
		if (registrationId.equals("kakao")) {

			oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

		} else if (registrationId.equals("naver")){

			oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

		} else if (registrationId.equals("google")){

			oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
		}
		else {
			//로그인 id가 일치하지 않을 경우
			OAuth2Error oauth2Error = new OAuth2Error("invalid_registration_id",
				"The registration id is invalid: " + registrationId, null);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.getDescription());
		}

		validateOAuth2Response(oAuth2Response);

		String oAuthType = oAuth2Response.getProvider();
		String oAuthId = oAuth2Response.getProviderId();

		String profileImage = oAuth2Response.getProfileImage() != null ? oAuth2Response.getProfileImage() : defaultProfileImage;


		if (!socialLoginRepository.existsByOauthTypeAndOauthId(OAuthType.valueOf(oAuthType.toUpperCase()), oAuthId)){

			Member member = Member.builder()
				.nickname(oAuth2Response.getName())
				.profileImg(profileImage)
				.build();
			Member savedMember = memberRepository.save(member);

			SocialLogin socialLogin = SocialLogin.builder()
				.member(savedMember)
				.oauthId(oAuth2Response.getProviderId())
				.oauthType(OAuthType.valueOf(oAuth2Response.getProvider().toUpperCase()))
				.externalEmail(oAuth2Response.getEmail())
				.build();
			socialLoginRepository.save(socialLogin);

			return new CustomOAuth2User(savedMember);

		} else {

			SocialLogin socialLogin = socialLoginRepository.
				findByOauthTypeAndOauthIdWithMember(OAuthType.valueOf(oAuthType.toUpperCase()),oAuthId);
			Member member = socialLogin.getMember();

			return new CustomOAuth2User(member);

		}

	}

	public void createOnboarding(Long memberId, CreateOnboardingRequest request) {
		Member member = getMember(memberId);
		member.onboarding(request.gender(),request.age(),request.weight());

		memberRepository.save(member);
	}

	public ProfileResponse getProfile(Long memberId) {
		Member member = getMember(memberId);
		CrewMember crewMember = getCrewMember(memberId);

		return new ProfileResponse(member, crewMember);
	}

	public SelfProfileResponse getSelfProfile(Long memberId) {
		Member member = getMember(memberId);
		CrewMember crewMember = getCrewMember(memberId);

		return new SelfProfileResponse(member, crewMember);
	}

	public UpdateProfileResponse updateProfile(Long memberId, UpdateProfileRequest request, MultipartFile imageFile) {
		Member member = getMember(memberId);

		String updatedProfileImgUrl = imageFile != null ? updateProfileWithImage(member, request, imageFile)
			: updateProfileWithoutImage(member, request);

		return new UpdateProfileResponse(member.getNickname(), member.getWeight(), member.getGender(), member.getAge(),
			updatedProfileImgUrl);
	}


	private void validateOAuth2Response(OAuth2Response oAuth2Response) {
		if (oAuth2Response.getName() == null) {
			throw new OAuth2AuthenticationException(new OAuth2Error(
				"invalid_response", "유효하지 않은 정보입니다.", null));
		}
	}

	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	}

	private CrewMember getCrewMember(Long memberId) {
		return crewMemberRepository.findByMemberId(memberId)
			.orElse(null);
	}

	private String updateProfileWithImage(Member member, UpdateProfileRequest request, MultipartFile imageFile) {
		s3StorageManagerUseCase.deleteImages(member.getProfileImg());

		List<String> updatedProfileImageUrl = s3StorageManagerUseCase.uploadImages(Collections.singletonList(imageFile));

		member.update(request.nickname(), request.weight(), request.gender(), request.age(), updatedProfileImageUrl.get(0));
		memberRepository.save(member);

		return updatedProfileImageUrl.get(0);
	}

	private String updateProfileWithoutImage(Member member, UpdateProfileRequest request) {
		member.update(request.nickname(), request.weight(), request.gender(), request.age(), member.getProfileImg());
		memberRepository.save(member);

		return member.getProfileImg();
	}
}
