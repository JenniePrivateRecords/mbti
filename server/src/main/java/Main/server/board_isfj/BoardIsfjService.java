package Main.server.board_isfj;

import Main.server.advice.errors.DuplicateResourceException;
import Main.server.advice.errors.NotFoundException;
import Main.server.comment.Comment;
import Main.server.comment.CommentRepository;
import Main.server.like.Like;
import Main.server.like.LikeDto;
import Main.server.like.LikeRepository;
import Main.server.notification.SseService;
import Main.server.user.UserRepository;
import Main.server.user.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BoardIsfjService {
    private final BoardIsfjRepository boardIsfjRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final SseService sseService;

    public BoardIsfjService(BoardIsfjRepository boardIsfjRepository,
                            UserRepository userRepository,
                            LikeRepository likeRepository,
                            CommentRepository commentRepository, SseService sseService) {
        this.boardIsfjRepository = boardIsfjRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.sseService = sseService;
    }

    @Transactional
    public BoardIsfj createPost(BoardIsfj post, long userId) {
        Users findUser = getUserFromId(userId);

        if(findUser.getMbti().equalsIgnoreCase("isfj")) {
            post.setUsers(findUser);
            post.setViewCount(0L);
            post.setLikeCount(0L);
            post.setCommentCount(0L);
            post.setCategory("isfj");

            return boardIsfjRepository.save(post);
        }
        else {
            return null;
        }
    }

    @Transactional
    public BoardIsfj updatePost(BoardIsfj post) {
        BoardIsfj findPost = findPost(post.getId());

        Optional.ofNullable(post.getTitle()).ifPresent(title -> findPost.setTitle(title));
        Optional.ofNullable(post.getContent()).ifPresent(content -> findPost.setContent(content));
        Optional.ofNullable(post.getTag()).ifPresent(tag -> findPost.setTag(tag));

        findPost.setModifiedAt(LocalDateTime.now());

        return findPost;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BoardIsfj readPost(long id) {
        BoardIsfj post = findPost(id);
        post.setViewCount(post.getViewCount() + 1);
        return post;
    }

    @Transactional
    public Like insert(LikeDto likeDto) throws Exception {

        Users users = userRepository.findById(likeDto.getUserId())
                .orElseThrow(() -> new NotFoundException("회원 정보를 찾을 수 없습니다."));

        BoardIsfj post = boardIsfjRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));

        if(likeRepository.findByUsersAndBoardIsfj(users, post).isPresent()) {
            throw new DuplicateResourceException("이미 추천했습니다.");
        }

        if(users.getMbti().equalsIgnoreCase("isfj")) {
            Like boardIsfjLike = Like.builder()
                    .boardIsfj(post)
                    .users(users)
                    .category("isfj")
                    .build();

            return likeRepository.save(boardIsfjLike);
        }
        else {
            return null;
        }
    }

    public void delete(LikeDto likeDto) {

        Users users = userRepository.findById(likeDto.getUserId())
                .orElseThrow(() -> new NotFoundException("회원 정보를 찾을 수 없습니다."));

        BoardIsfj post = boardIsfjRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));

        Like boardIsfjLike = likeRepository.findByUsersAndBoardIsfj(users, post)
                .orElseThrow(() -> new NotFoundException("추천하지 않았습니다."));

        likeRepository.delete(boardIsfjLike);
    }

    public BoardIsfj addLike(long id) {
        BoardIsfj post = findPost(id);
        post.setLikeCount(post.getLikeCount() + 1);
        return boardIsfjRepository.save(post);
    }

    public BoardIsfj deleteLike(long id) {
        BoardIsfj post = findPost(id);
        post.setLikeCount(post.getLikeCount() - 1);
        return boardIsfjRepository.save(post);
    }

    public void deletePost(long id) {
        BoardIsfj post = findPost(id);

        List<Like> boardIsfjLikes = likeRepository.findAll();

        while (post.getLikeCount() != 0) {
            for(int i = 0; i < boardIsfjLikes.size(); i++) {
                Like boardIsfjLike = boardIsfjLikes.get(i);
                if(boardIsfjLike.getCategory().equals("isfj") && boardIsfjLike.getBoardIsfj().getId() == id) {
                    likeRepository.delete(boardIsfjLike);
                    deleteLike(id);
                }
            }
        }

        List<Comment> comments = commentRepository.findAll();

        while (post.getCommentCount() != 0) {
            for(int i = 0; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                if(comment.getCategory().equals("isfj") && comment.getBoardIsfj().getId() == id) {
                    commentRepository.delete(comment);
                    post.setCommentCount(post.getCommentCount() - 1);
                }
            }
        }

        boardIsfjRepository.deleteById(id);
    }

    // 댓글 작성
    @Transactional
    public Comment createComment(Comment comment, long userId, long postId) {
        Users findUser = getUserFromId(userId);
        BoardIsfj findPost = findPost(postId);

        if(findUser.getMbti().equalsIgnoreCase("isfj")) {
            findPost.setCommentCount(findPost.getCommentCount()+1);
            comment.setUser(findUser);
            comment.setBoardIsfj(findPost);
            comment.setCategory("isfj");

            sseService.AddCommentEvent(postId);

            return commentRepository.save(comment);
        }
        else {
            return null;
        }
    }

    // 댓글 수정
    @Transactional
    public Comment updateComment(Comment comment) {
        Comment findComment = findVerifiedComment(comment.getId());
        Optional.ofNullable(comment.getContent()).ifPresent(content -> findComment.setContent(content));

        findComment.setModifiedAt(LocalDateTime.now());
        return commentRepository.save(findComment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(long commentId) {
        Comment findComment = findVerifiedComment(commentId);
        BoardIsfj post = findComment.getBoardIsfj();
        commentRepository.deleteById(commentId);
        post.setCommentCount(post.getCommentCount()-1);
    }

    // 댓글 조회
    public Comment findVerifiedComment(long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    // 전체 댓글 조회
    public List<Comment> findComments() {
        return commentRepository.findAll();
    }

    public BoardIsfj findPost(long id) {
        return boardIsfjRepository.findById(id).orElse(null);
    }

    public Page<BoardIsfj> findAllPost(int page, int size) {
        return boardIsfjRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public Users getUserFromId(long userId) {
        return userRepository.findById(userId).get();
    }
}
