package Main.server.board_isfj;

import Main.server.comment.Comment;
import Main.server.comment.CommentDto;
import Main.server.comment.CommentMapper;
import Main.server.like.Like;
import Main.server.like.LikeDto;
import Main.server.like.LikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/board/isfj")
public class BoardIsfjController {
    private final BoardIsfjService service;
    private final CustomBoardIsfjMapper mapper;
    private final LikeRepository likeRepository;
    private final CommentMapper commentMapper;

    public BoardIsfjController(BoardIsfjService service,
                               CustomBoardIsfjMapper mapper,
                               LikeRepository likeRepository,
                               CommentMapper commentMapper) {
        this.service = service;
        this.mapper = mapper;
        this.likeRepository = likeRepository;
        this.commentMapper = commentMapper;
    }

    //게시글 등록
    @PostMapping
    public ResponseEntity postPost(@RequestBody BoardIsfjDto.Post postDto) {
        BoardIsfj createdPost = service.createPost(mapper.postDtoToBoardIsfj(postDto), postDto.getUserId());

        if(createdPost != null) {
            BoardIsfjDto.Response result = mapper.boardIsfjToResponseDto(createdPost);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //전체 게시글 조회
    @GetMapping
    public ResponseEntity getPosts(@Positive @RequestParam(defaultValue = "1") int page,
                                   @Positive @RequestParam(defaultValue = "100") int size) {

        Page<BoardIsfj> pagePosts = service.findAllPost(page - 1, size);
        List<BoardIsfj> posts = pagePosts.getContent();
        List<BoardIsfjDto.Response> result = mapper.boardIsfjToResponseDtos(posts);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //게시글 조회
    @GetMapping("/{post-id}")
    public ResponseEntity getPost(@PathVariable("post-id") long postId) {

        BoardIsfj post = service.readPost(postId);
        BoardIsfjDto.Response result = mapper.boardIsfjToResponseDto(post);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //게시글 수정
    @PatchMapping("/{post-id}")
    public ResponseEntity patchPost(@PathVariable("post-id") long postId,
                                    @RequestBody BoardIsfjDto.Patch patchDto) {

        BoardIsfj post = mapper.patchDtoToBoardIsfj(patchDto);
        post.setId(postId);

        BoardIsfj modifiedPost = service.updatePost(post);
        BoardIsfjDto.Response result = mapper.boardIsfjToResponseDto(modifiedPost);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //게시글 삭제
    @DeleteMapping("/{post-id}")
    public ResponseEntity deletePost(@PathVariable("post-id") long postId) {

        service.deletePost(postId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //추천 등록
    @PostMapping("/{post-id}/like")
    public ResponseEntity insert(@PathVariable("post-id") long postId,
                                 @RequestBody LikeDto likeDto) throws Exception {


        if(postId == likeDto.getPostId()) {
            Like like = service.insert(likeDto);

            if(like != null) {
                service.addLike(postId);

                return new ResponseEntity(HttpStatus.OK);
            }
            else {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //추천 취소
    @DeleteMapping("/{post-id}/like")
    public ResponseEntity delete(@PathVariable("post-id") long postId,
                                 @RequestBody @Valid LikeDto likeDto) {

        if(postId == likeDto.getPostId()) {

            service.delete(likeDto);
            service.deleteLike(postId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //댓글 등록
    @PostMapping("/{post-id}")
    public ResponseEntity postComment(@PathVariable("post-id") Long postId,
                                      @RequestBody CommentDto.Post postDto) {

        if(postId == postDto.getPostId()) {
            Comment createComment = service.createComment(commentMapper.commentPostDtoToComment(postDto), postDto.getUserId(), postId);
            if(createComment != null) {
                CommentDto.Response result = commentMapper.commentToCommentResponseDto(createComment);
                result.setUserId(createComment.getUser().getUserId());
                result.setUsername(createComment.getUser().getNickName());
                result.setBoardId(createComment.getBoardIsfj().getId());

                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //전체 댓글 조회
    @GetMapping("/{post-id}/comment")
    public ResponseEntity getComments() {
        List<Comment> comments = service.findComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    //댓글 조회
    @GetMapping("/{post-id}/comment/{comment-id}")
    public ResponseEntity getComment(@PathVariable("post-id") long postId,
                                     @PathVariable("comment-id") long commentId) {

        Comment comment = service.findVerifiedComment(commentId);
        CommentDto.Response result = commentMapper.commentToCommentResponseDto(comment);
        result.setUserId(comment.getUser().getUserId());
        result.setUsername(comment.getUser().getNickName());
        result.setBoardId(comment.getBoardIsfj().getId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //댓글 수정
    @PatchMapping("/{post-id}/comment/{comment-id}")
    public ResponseEntity patchComment(@PathVariable("post-id") Long postId,
                                       @PathVariable("comment-id") Long commentId,
                                       @RequestBody CommentDto.Patch patchDto) {
        Comment comment = commentMapper.commentPatchDtoToComment(patchDto);
        comment.setId(commentId);

        Comment patchComment = service.updateComment(comment);
        CommentDto.Response result = commentMapper.commentToCommentResponseDto(patchComment);
        result.setUserId(patchComment.getUser().getUserId());
        result.setUsername(patchComment.getUser().getNickName());
        result.setBoardId(postId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //댓글 삭제
    @DeleteMapping("/{post-id}/comment/{comment-id}")
    public ResponseEntity deleteComment(@PathVariable("post-id") long postId,
                                        @PathVariable("comment-id") long commentId) {

        service.deleteComment(commentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
