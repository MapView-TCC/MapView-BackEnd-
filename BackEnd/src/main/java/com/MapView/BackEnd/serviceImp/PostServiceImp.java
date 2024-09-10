package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.PostRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.PostService;
import com.MapView.BackEnd.dtos.Post.PostCreateDTO;
import com.MapView.BackEnd.dtos.Post.PostDetailDTO;
import com.MapView.BackEnd.dtos.Post.PostUpdateDTO;
import com.MapView.BackEnd.entities.Post;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImp implements PostService {
    private final PostRepository postRepository;
    private final UserLogRepository userLogRepository;
    private final UserRepository userRepository;

    public PostServiceImp(PostRepository postRepository, UserLogRepository userLogRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PostDetailDTO getPost(Long id_post,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        Post post = this.postRepository.findById(id_post).orElseThrow(() -> new NotFoundException("Post id Not Found"));

        if(!post.isOperative()){
            throw new OperativeFalseException("The inactive post cannot be accessed.");
        }

        var userLog = new UserLog(user,"Post",id_post.toString(),"Read Post", EnumAction.READ);
        userLogRepository.save(userLog);

        return new PostDetailDTO(post);
    }

    @Override
    public List<PostDetailDTO> getAllPost(int page, int itens,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Post","Read All Post", EnumAction.READ);
        userLogRepository.save(userLog);

        return this.postRepository.findByOperativeTrue(PageRequest.of(page, itens)).stream().map(PostDetailDTO::new).toList();


    }

    @Override
    public PostDetailDTO createPost(PostCreateDTO data,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var post = new Post(data);
        Long post_id = postRepository.save(post).getId_post();

        var userLog = new UserLog(user,"Area",post_id.toString(),"Create new Area", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new PostDetailDTO(post);
    }

    @Override
    public PostDetailDTO updatePost(Long id_post, PostUpdateDTO data,Long user_id) {
        var post = this.postRepository.findById(id_post).orElseThrow(() ->new NotFoundException("Post Id Not Found"));
        if(!post.isOperative()){
            throw new OperativeFalseException("The inactive post cannot be accessed.");
        }

        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(user,"Area", id_post.toString(),null,"Infos update",EnumAction.UPDATE);

        if(data.post() != null){
            post.setPost(data.post());
            userlog.setField("Post to: "+data.post());
        }

        postRepository.save(post);
        userLogRepository.save(userlog);

        return new PostDetailDTO(post);
    }

    @Override
    public  void activePost(Long id_post,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));


        Post post = this.postRepository.findById(id_post).orElseThrow(() -> new NotFoundException("Post Id Not Found"));
        post.setOperative(true);
        postRepository.save(post);

        var userLog = new UserLog(user,"Post",id_post.toString(),"Operative","Activated Post",EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }

    @Override
    public void inactivatePost(Long post_id,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        Post post = this.postRepository.findById(post_id).orElseThrow(() -> new NotFoundException("Post Id Not Found"));
        post.setOperative(false);
        postRepository.save(post);

        var userLog = new UserLog(user,"Post",post_id.toString(),"Operative","Activated Post",EnumAction.UPDATE);
        userLogRepository.save(userLog);


    }
}
