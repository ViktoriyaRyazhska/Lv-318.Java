package org.uatransport.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uatransport.entity.Comment;
import org.uatransport.exception.MaxLevelCommentException;
import org.uatransport.exception.ResourceNotFoundException;
import org.uatransport.exception.TimeExpiredException;
import org.uatransport.repository.CommentRepository;
import org.uatransport.repository.TransitRepository;
import org.uatransport.repository.UserRepository;
import org.uatransport.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TransitRepository transitRepository;
    private final UserRepository userRepository;

    private static final int MAX_COMMENT_LEVEL = 5;

    @Override
    @Transactional
    public Comment add(Comment comment, Integer transitId, Integer userId, Integer parentId) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment object should not be null");
        }

        comment.setLevel(1);

        if (parentId != null) {
            Comment parentComment = getById(parentId);
            int parentLevel = parentComment.getLevel();

            if (parentLevel == MAX_COMMENT_LEVEL) {
                throw new MaxLevelCommentException(String.format("Reached max comment level %d", MAX_COMMENT_LEVEL));
            } else {
                comment.setLevel(parentLevel + 1);
            }

            comment.setParentComment(parentComment);
        }

        comment.setCreatedDate(LocalDateTime.now());
        comment.setTransit(transitRepository.getOne(transitId));
        comment.setUser(userRepository.getOne(userId));

        commentRepository.save(comment);

        return comment;
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getById(Integer id) {
        return commentRepository.getOne(id);
//        return commentRepository.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment with id '%s' not found", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllByUserId(Integer userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllTopLevel(Integer transitId) {
        return commentRepository.findByTransitIdAndLevel(transitId, 1);
    }

    @Override
    public List<Comment> getAllByParentId(Integer parentId) {
        return commentRepository.findByParentCommentId(parentId);
    }

    @Override
    @Transactional
    public Comment update(Comment newComment, Integer commentId) {
        if (newComment == null) {
            throw new IllegalArgumentException("Comment object should not be null");
        }

        Comment comment;

        if (commentRepository.existsById(commentId)) {
            comment = getById(commentId);
        } else {
            throw new ResourceNotFoundException(String.format("Comment with id '%s' not found", newComment.getId()));
        }

        //check if admin or owner

        if (!comment.canEdit()) {
            throw new TimeExpiredException(String.format("Time for updating comment with id '%s' expired", commentId));
        }

        comment.setCommentText(newComment.getCommentText());
        comment.setModifiedDate(LocalDateTime.now());

        return commentRepository.save(comment);

    }

    @Override
    @Transactional
    public void delete(Integer commentId) {

        Comment comment = getById(commentId);

        if (!comment.canDelete()) {
            throw new TimeExpiredException(String.format("Time for updating comment with id '%s' expired", commentId));
        }

        try {
            commentRepository.deleteById(commentId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(String.format("Comment with id '%s' not found", commentId));
        }

    }
}
