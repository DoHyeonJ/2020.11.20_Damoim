package com.damoim.modules.event;


import com.damoim.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "Enrollment.withEventAndClub",
        attributeNodes = {
                @NamedAttributeNode(value = "event", subgraph = "club")
        },
        subgraphs = @NamedSubgraph(name = "club", attributeNodes = @NamedAttributeNode("club"))
)

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

}
