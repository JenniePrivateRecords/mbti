package Main.server.like;

import Main.server.board_integrated.BoardIntegrated;
import Main.server.user.entity.Users;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "LIKE_TABLE")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private BoardIntegrated boardIntegrated;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id")
//    @JsonBackReference
//    private BoardInfj boardInfjPost;

    private String category;

//    public BoardIntegratedLike(Users users, BoardIntegrated post, String category) {
//        this.users = users;
//        this.post = post;
//        this.category = category;
//    }
}
