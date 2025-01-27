package com.app.spotick.controller.place;

import com.app.spotick.domain.dto.place.PlaceDetailDto;
import com.app.spotick.domain.dto.place.PlaceListDto;
import com.app.spotick.domain.dto.place.PlaceRegisterDto;
import com.app.spotick.domain.dto.place.reservation.PlaceReserveBasicInfoDto;
import com.app.spotick.domain.dto.place.reservation.PlaceReserveRegisterDto;
import com.app.spotick.domain.dto.user.UserDetailsDto;
import com.app.spotick.service.place.PlaceService;
import com.app.spotick.service.place.reservation.PlaceReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/place/*")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;
    private final PlaceReservationService reservationService;

    @GetMapping("/detail/{placeId}")
    public String placeDetail(@PathVariable("placeId")Long placeId,
                              @AuthenticationPrincipal UserDetailsDto userDetailsDto,
                              Model model) {
        Long userId = userDetailsDto==null? null: userDetailsDto.getId();
        PlaceDetailDto place = placeService.findPlaceDetailById(placeId, userId);

        model.addAttribute("place", place);
        return "place/detail";
    }

    @GetMapping("/list")
    public String placeList(Model model,@AuthenticationPrincipal UserDetailsDto userDetailsDto){
        Long userId = userDetailsDto==null? null: userDetailsDto.getId();

        List<PlaceListDto> placeList = placeService.findPlaceListPagination(0,userId);
        model.addAttribute("placeList",placeList);
        return "place/list";
    }

    @GetMapping("/register")
    public String placeRegister(@ModelAttribute PlaceRegisterDto placeRegisterDto){
        return "place/register";
    }

    @PostMapping("/register")
    public String placeRegister(@Valid PlaceRegisterDto placeRegisterDto,
                                      BindingResult result,
                                @AuthenticationPrincipal UserDetailsDto userDetailsDto){

        if (result.hasErrors()){
            return "place/register";
        }

        try {
            placeService.registerPlace(placeRegisterDto,userDetailsDto.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return  "place/register";
        }

        return "redirect:/place/list";
    }

    @GetMapping("/check/reserve")
    public String placeReserve(@ModelAttribute PlaceReserveRegisterDto registerDto,Model model){
        PlaceReserveBasicInfoDto placeReserveDefaultInfo = placeService
                .findPlaceReserveDefaultInfo(registerDto.getPlaceId());

        model.addAttribute("place",placeReserveDefaultInfo);
        return "place/reserve";
    }

    @PostMapping("/reserve/register")
    public String placeReservationRegister(@ModelAttribute PlaceReserveRegisterDto placeReserveRegisterDto,
                       @AuthenticationPrincipal UserDetailsDto userDetailsDto,Model model){

        if(!reservationService.isReservationAvailable(placeReserveRegisterDto)){
            PlaceReserveBasicInfoDto placeReserveDefaultInfo = placeService
                    .findPlaceReserveDefaultInfo(placeReserveRegisterDto.getPlaceId());
            model.addAttribute("place",placeReserveDefaultInfo);
            model.addAttribute("invalidReservation",true);
            return "place/reserve";
        }

        reservationService.registerPlaceReservation(
                placeReserveRegisterDto,userDetailsDto.getId());
        return "redirect:/place/detail/"+placeReserveRegisterDto.getPlaceId();
    }

}










