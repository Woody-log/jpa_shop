package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)     // 검색기능 최적화.
@RequiredArgsConstructor            // final이 있는 필드에만 생성자 자동생성.
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    /**
     *  중복 회원 검사
     */
    private void validateDuplicateMember(Member member) {
        // EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    /**
     *  전체 히원 검색
     */
    public List<Member> findMembers() {
        return memberRepository.fidnAll();
    }

    /**
     *  회원 meberId로 검색
     */
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

}
