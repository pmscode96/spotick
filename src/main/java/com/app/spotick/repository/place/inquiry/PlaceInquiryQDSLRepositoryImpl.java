package com.app.spotick.repository.place.inquiry;

import com.app.spotick.domain.dto.place.PlaceFileDto;
import com.app.spotick.domain.dto.place.PlaceInquiryListDto;
import com.app.spotick.domain.entity.place.PlaceInquiry;
import com.app.spotick.domain.type.post.PostStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.app.spotick.domain.entity.place.QPlace.place;
import static com.app.spotick.domain.entity.place.QPlaceBookmark.placeBookmark;
import static com.app.spotick.domain.entity.place.QPlaceFile.placeFile;
import static com.app.spotick.domain.entity.place.QPlaceInquiry.placeInquiry;
import static com.app.spotick.domain.entity.place.QPlaceReview.placeReview;

@RequiredArgsConstructor
public class PlaceInquiryQDSLRepositoryImpl implements PlaceInquiryQDSLRepository {
    private final JPAQueryFactory queryFactory;
    @Override
    public Page<PlaceInquiry> inquiryListWithPage(Long placeId, Pageable pageable) {

        List<PlaceInquiry> contents = queryFactory.selectFrom(placeInquiry)
                .join(placeInquiry.user).fetchJoin()
                .where(placeInquiry.place.id.eq(placeId))
                .orderBy(placeInquiry.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(placeInquiry.count())
                .from(placeInquiry)
                .where(placeInquiry.place.id.eq(placeId))
                .fetchOne();

        return new PageImpl<>(contents,pageable,totalCount);
    }

    @Override
    public Page<PlaceInquiryListDto> findInquiriesByUserId(Long userId, Pageable pageable) {

        JPQLQuery<Long> totalCountQuery = queryFactory.select(placeInquiry.count())
                .from(placeInquiry)
                .join(placeInquiry.place, place)
                .where(placeInquiry.user.id.eq(userId), place.placeStatus.eq(PostStatus.APPROVED));

        JPQLQuery<Double> reviewAvg = JPAExpressions.select(placeReview.score.avg())
                .from(placeReview)
                .where(placeReview.placeReservation.place.eq(place));

        JPQLQuery<Long> reviewCount = JPAExpressions.select(placeReview.count())
                .from(placeReview)
                .where(placeReview.placeReservation.place.eq(place));

        JPQLQuery<Long> bookmarkCount = JPAExpressions.select(placeBookmark.count())
                .from(placeBookmark)
                .where(placeBookmark.place.eq(place));

        List<PlaceInquiryListDto> placeInquiryListDtos = queryFactory
                .from(placeInquiry)
                .join(placeInquiry.place, place)
                .leftJoin(place.placeFileList, placeFile)
                .where(placeInquiry.user.id.eq(userId),
                        placeFile.id.eq(
                                JPAExpressions.select(placeFile.id.min())
                                        .from(placeFile)
                                        .where(placeFile.place.id.eq(place.id))
                        )
                )
                .orderBy(placeInquiry.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .select(
                        Projections.constructor(PlaceInquiryListDto.class,
                                place.id,
                                placeInquiry.id,
                                place.title,
                                place.price,
                                place.placeAddress,
                                Projections.constructor(PlaceFileDto.class,
                                        placeFile.id,
                                        placeFile.fileName,
                                        placeFile.uuid,
                                        placeFile.uploadPath,
                                        place.id
                                ),
                                reviewAvg,
                                reviewCount,
                                bookmarkCount,
                                placeInquiry.title,
                                placeInquiry.content,
                                placeInquiry.response
                        )
                )
                .fetch();

        placeInquiryListDtos.forEach(dto -> {
            dto.getPlaceAddress().cutAddress();
        });


        return PageableExecutionUtils.getPage(placeInquiryListDtos, pageable, totalCountQuery::fetchOne);
    }
}





















