package dblab.sharing_flatform.controller.category;

import dblab.sharing_flatform.dto.category.CategoryRequestDto;
import dblab.sharing_flatform.dto.response.Response;
import dblab.sharing_flatform.service.category.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "Category Controller", tags = "Category")
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @ApiOperation(value = "카테고리 전체 조회", notes = "카테고리를 계층형으로 조회")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response readAll() {
        return Response.success(categoryService.readAll());
    }


    @ApiOperation(value = "카테고리 생성", notes = "카테고리를 생성 / ADMIN 전용")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        Long createdId = categoryService.create(categoryRequestDto);
        return Response.success(createdId);
    }


    @ApiOperation(value = "카테고리 삭제", notes = "카테고리를 삭제 / ADMIN 전용")
    @DeleteMapping("/{categoryName}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@ApiParam(name="삭제할 카테고리 이름" , required = true) @PathVariable String categoryName) {
        categoryService.delete(categoryName);
        return Response.success();
    }

}
