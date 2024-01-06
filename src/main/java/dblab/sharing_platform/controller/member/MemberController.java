package dblab.sharing_platform.controller.member;

import static dblab.sharing_platform.config.security.util.SecurityUtil.getCurrentUsernameCheck;
import static org.springframework.http.HttpStatus.OK;

import dblab.sharing_platform.dto.member.MemberPagingCondition;
import dblab.sharing_platform.dto.member.MemberUpdateRequest;
import dblab.sharing_platform.dto.member.OAuthMemberUpdateRequest;
import dblab.sharing_platform.service.member.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Member Controller", tags = "Member")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @ApiOperation(value = "전체 회원 조회", notes = "전체 회원을 조회합니다. (username 검색)")
    @GetMapping
    public ResponseEntity readAllMemberByCond(@Valid MemberPagingCondition condition) {
        return new ResponseEntity(memberService.readAllMemberByCond(condition), OK);
    }

    @ApiOperation(value = "나의 개인 정보 조회", notes = "현재 로그인한 회원 정보를 조회합니다.")
    @GetMapping("/my-profile")
    public ResponseEntity readInfoCurrentUser() {
        return new ResponseEntity(memberService.readCurrentUserInfoByUsername(getCurrentUsernameCheck()), OK);
    }

    @ApiOperation(value = "회원 프로필 정보 조회", notes = "회원의 프로필 정보를 조회합니다.")
    @GetMapping("/{username}")
    public ResponseEntity findMemberProfileByUsername(@ApiParam(name = "검색할 사용자 아이디", required = true)
                                                      @PathVariable String username) {
        return new ResponseEntity(memberService.readMemberProfileByNickname(username), OK);
    }

    @ApiOperation(value = "회원 삭제", notes = "관리자 또는 본인인 경우 사용자를 삭제한다.")
    @DeleteMapping
    public ResponseEntity deleteMemberByUsername() {
        memberService.deleteMemberByUsername(getCurrentUsernameCheck());
        return new ResponseEntity(OK);
    }

    @ApiOperation(value = "자신의 회원 정보 수정", notes = "본인인 경우 정보를 수정한다.")
    @PatchMapping
    public ResponseEntity updateMemberInfo(@Valid @ModelAttribute MemberUpdateRequest memberUpdateRequest) {
        return new ResponseEntity(memberService.updateMember(getCurrentUsernameCheck(), memberUpdateRequest), OK);
    }

    @ApiOperation(value = "OAuth 회원 추가 정보 등록(필수) / 수정 ", notes = "OAuth2 유저 최초 로그인 시 OAuth 회원 본인의 정보를 등록합니다. / OAuth2 회원의 정보를 수정합니다.")
    @PatchMapping("/oauth")
    public ResponseEntity updateOAuthMemberInfo(@Valid @ModelAttribute OAuthMemberUpdateRequest request) {
        return new ResponseEntity(memberService.oauthMemberUpdate(getCurrentUsernameCheck(), request), OK);
    }
}
