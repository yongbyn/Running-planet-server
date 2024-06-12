package clofi.runningplanet.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByNickname(String nickName);

	Optional<Member> findByIdAndNickname(Long memberId, String nickName);
}
