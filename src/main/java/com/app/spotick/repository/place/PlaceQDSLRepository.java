package com.app.spotick.repository.place;

import com.app.spotick.domain.dto.place.PlaceDetailDto;
import com.app.spotick.domain.dto.place.PlaceListDto;
import com.app.spotick.domain.dto.place.PlaceManageListDto;
import com.app.spotick.domain.dto.place.reservation.PlaceReserveBasicInfoDto;
import com.app.spotick.domain.dto.place.reservation.PlaceReservedNotReviewedDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface PlaceQDSLRepository {
//    메인화면에 뿌려줄 게시글 리스트
    List<PlaceListDto> findPlaceListPaging(Pageable pageable, Long userId);
//    장소 상세보기
    Optional<PlaceDetailDto> findPlaceDetailById(Long placeId, Long userId);

//    장소예약페이지에서  장소에대한 기본정보
    Optional<PlaceReserveBasicInfoDto> findPlaceReserveBasicInfo(Long placeId);

    Page<PlaceReservedNotReviewedDto> findPlaceListNotRelatedToReview(Long userId, Pageable pageable);

    Page<PlaceManageListDto> findHostPlaceListByUserId(Long userId, Pageable pageable);

}
