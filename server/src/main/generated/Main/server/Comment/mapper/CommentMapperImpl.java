package Main.server.Comment.mapper;

import Main.server.Comment.dto.CommentPatchDto;
import Main.server.Comment.dto.CommentPostDto;
import Main.server.Comment.dto.CommentResponseDto;
import Main.server.Comment.entity.Comment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-03-17T16:54:46+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.17 (Azul Systems, Inc.)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment commentPostDtoToComment(CommentPostDto postDto) {
        if ( postDto == null ) {
            return null;
        }

        Comment comment = new Comment();

        comment.setContent( postDto.getContent() );

        return comment;
    }

    @Override
    public Comment commentPatchDtoToComment(CommentPatchDto patchDto) {
        if ( patchDto == null ) {
            return null;
        }

        Comment comment = new Comment();

        comment.setCommentId( patchDto.getCommentId() );
        comment.setContent( patchDto.getContent() );

        return comment;
    }

    @Override
    public CommentResponseDto commentToCommentResponseDto(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponseDto commentResponseDto = new CommentResponseDto();

        commentResponseDto.setCommentId( comment.getCommentId() );
        commentResponseDto.setContent( comment.getContent() );

        return commentResponseDto;
    }
}
