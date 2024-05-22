package clofi.runningplanet.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Member findByNickname(String nickName);
}
