package dblab.sharing_flatform.controller.member;

import dblab.sharing_flatform.dto.member.MemberResponseDto;
import dblab.sharing_flatform.dto.member.MemberUpdateRequestDto;
import dblab.sharing_flatform.dto.response.Response;
import dblab.sharing_flatform.service.member.MemberService;
import dblab.sharing_flatform.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(value = "Member Controller", tags = "Member")
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @ApiOperation(value = "현재 사용자 조회", notes = "현재 사용자를 조회한다.")
    @GetMapping("/user")
    public Response currentUser(){
        String currentUsername = SecurityUtil.getCurrentUsername().get();

        log.info("currentUsername = {}", currentUsername);
        MemberResponseDto currentMember = memberService.getMemberInfo(currentUsername);

        return Response.success(currentMember);
    }

    @ApiOperation(value = "특정 사용자 조회", notes = "ADMIN 권한이 있는 경우 특정 사용자를 조회한다.")
    @GetMapping("/admin/{username}")
    public Response findMemberByAdmin(@ApiParam(name="조회할 사용자 이름" , required = true) @PathVariable String username){
        MemberResponseDto memberInfo = memberService.getMemberInfo(username);
        return Response.success(memberInfo);
    }

    @ApiOperation(value = "사용자 삭제", notes = "관리자 또는 본인인 경우 사용자를 삭제한다.")
    @DeleteMapping("/{id}")
    public Response deleteMember(@ApiParam(name = "삭제할 사용자 아이디", required = true) @PathVariable Long id){
        memberService.delete(id);
        return Response.success();
    }


    @ApiOperation(value = "회원 정보 수정", notes = "본인인 경우 정보를 수정한다.")
    @PatchMapping("/{id}")
    public Response updateMember(@ApiParam(name = "수정할 사용자 아이디", required = true) @PathVariable Long id, @RequestBody MemberUpdateRequestDto memberUpdateRequestDto){
        MemberResponseDto updateMember = memberService.update(id, memberUpdateRequestDto);
        return Response.success(updateMember);
    }
}
