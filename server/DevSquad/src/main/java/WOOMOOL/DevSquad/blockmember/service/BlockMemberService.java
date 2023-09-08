package WOOMOOL.DevSquad.blockmember.service;

import WOOMOOL.DevSquad.blockmember.entity.BlockMember;
import WOOMOOL.DevSquad.blockmember.repository.BlockMemberRepository;
import WOOMOOL.DevSquad.exception.BusinessLogicException;
import WOOMOOL.DevSquad.exception.ExceptionCode;
import WOOMOOL.DevSquad.member.entity.Member;
import WOOMOOL.DevSquad.member.entity.MemberProfile;
import WOOMOOL.DevSquad.member.repository.MemberRepository;
import WOOMOOL.DevSquad.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static WOOMOOL.DevSquad.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
public class BlockMemberService {

    private final MemberService memberService;
    private final BlockMemberRepository blockMemberRepository;


    public void setBlockMember(long blockMemberId, String reportContent) {

        // 현재 로그인한 회원의 프로필
        MemberProfile loginMemberProfile = findloginMemberProfile();

        // 자신을 차단할 수 는 없음 혹시 모르니 추가
        if (loginMemberProfile.getMemberProfileId() == blockMemberId) throw new BusinessLogicException(CANT_SELF_BLOCKING);
        // 블랙 리스트 확인하고 중복 차단이 되지 않게
        List<Long> blockMemberLIdList = loginMemberProfile.getBlockMemberList().stream()
                .map(blockMember -> blockMember.getBlockId())
                .collect(Collectors.toList());
        // Id로 변환한 블랙리스트에 같은 id 값 있으면 exception
        if (blockMemberLIdList.contains(blockMemberId)) throw new BusinessLogicException(DUPLICATE_BLOCKING);

        // 차단할 멤버 닉네임 값을 얻기 위해 멤버 객체 생성..
        Member findMember = memberService.findMember(blockMemberId);
        // 차단 멤버 객체 생성
        BlockMember blockMember = new BlockMember(blockMemberId, reportContent, findMember.getNickname());
        // 현재 로그인한 회원 정보에 차단 멤버 객체 넣고
        loginMemberProfile.addBlockMember(blockMember);
        // 차단 멤버 객체에도 현재 로그인 회원 정보 넣어주기
        blockMember.setMemberProfile(loginMemberProfile);

        blockMemberRepository.save(blockMember);
    }

    public void deleteBlockMember(Long blockMemberId) {

        // 현재 로그인한 회원의 프로필
        MemberProfile loginMemberProfile = findloginMemberProfile();

//        Long memberProfileId = loginMemberProfile.getMemberProfileId();

        // 삭제할 차단 멤버 객체 찾기
        Optional<BlockMember> optionalBlockMember = blockMemberRepository.findByBlockId(blockMemberId);
        BlockMember findBlockMember = optionalBlockMember.orElseThrow(() -> new BusinessLogicException(NOT_BLOCKED_MEMBER));

        loginMemberProfile.getBlockMemberList().remove(findBlockMember);

        // DB 에 차단 멤버 객체 삭제
        blockMemberRepository.delete(findBlockMember);

//        // 현재 로그인한 회원의 블랙리스트 필터링
//        List<BlockMember> blockMemberList = blockMemberRepository.findByMemberProfileId(memberProfileId)
//                .stream().filter(blockMember -> !(blockMember.getBlockMemberId() == blockMemberId))
//                        .collect(Collectors.toList());
//        // 다시 할당
//        loginMemberProfile.setBlockMemberList(blockMemberList);


    }

    // 현재 로그인 중인 회원 프로필 찾기
    private MemberProfile findloginMemberProfile() {

        Member loginMember = memberService.findMemberFromToken();
        MemberProfile loginMemberProfile = loginMember.getMemberProfile();

        return loginMemberProfile;
    }
}
