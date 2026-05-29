package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.service.HomeBannerService;
import com.youngman.hostel.service.HostelActivityService;
import com.youngman.hostel.service.MessageBoardService;
import com.youngman.hostel.vo.HomeIndexVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeBannerService homeBannerService;
    private final HostelActivityService hostelActivityService;
    private final MessageBoardService messageBoardService;

    @GetMapping("/index")
    public ApiResponse<HomeIndexVO> index(){
        try{
            HomeIndexVO vo=new HomeIndexVO();
            vo.setBanners(homeBannerService.listEnabled());
            vo.setRecommendActivities(hostelActivityService.listRecommend());
            vo.setLatestMessages(messageBoardService.listLatestPublished(6));
            return ApiResponse.success(vo);
        }catch (Exception e){
            return ApiResponse.fail(e.getMessage());
        }
    }
}
