package com.app.spotick.domain.entity.ticket;


import com.app.spotick.domain.base.post.PostBase;
import com.app.spotick.domain.embedded.post.PostAddress;
import com.app.spotick.domain.entity.user.User;
import com.app.spotick.domain.type.post.PostStatus;
import com.app.spotick.domain.type.ticket.TicketCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity @Table(name = "TBL_TICKET_EVENT")
@SequenceGenerator(name = "SEQ_TICKET_EVENT_GENERATOR", sequenceName = "SEQ_TICKET_EVENT",allocationSize = 1)
@Getter @ToString(callSuper = true) @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends PostBase {
    @Id @GeneratedValue(generator = "SEQ_TICKET_EVENT_GENERATOR")
    @Column(name = "TICKET_EVENT_ID")
    private Long id;
    @Column(length = 2000)
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private TicketCategory ticketCategory;
    private String bankName;
    private String accountNumber; //계좌번호
    private String accountHolder; //예금주

    @Enumerated(EnumType.STRING)
    private PostAddress ticketEventAddress;
    @Enumerated(EnumType.STRING)
    private PostStatus ticketEventStatus;
    @Enumerated(EnumType.STRING)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;


    @Builder
    public Ticket(String title, int viewCount, Double lat, Double lng, Long id, String content, LocalDate startDate, LocalDate endDate, PostAddress ticketEventAddress, PostStatus ticketEventStatus, TicketCategory ticketCategory, String bankName, String accountNumber, String accountHolder, User user) {
        super(title, viewCount, lat, lng);
        this.id = id;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ticketEventAddress = ticketEventAddress;
        this.ticketEventStatus = ticketEventStatus;
        this.ticketCategory = ticketCategory;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.user = user;
    }

    public void setUser(User user){
        this.user = user;
    }
}











