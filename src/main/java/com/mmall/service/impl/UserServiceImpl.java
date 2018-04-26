package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

// 注释注入服务
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("No such user");
        }

        //login MD5(to do)
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username,md5Password );
        if(user == null){
            return ServerResponse.createByErrorMessage("wrong password");
        }
        //
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("login successfully",user);
    }

    public ServerResponse<String> register(User user){
        //check username
        ServerResponse<String> resultResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!resultResponse.isSuccess())return resultResponse;

        //check Email
        resultResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!resultResponse.isSuccess())return resultResponse;

        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD 5 coding(to do)
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount ==0 ){
            return ServerResponse.createByErrorMessage("fail to register");
        }

        return ServerResponse.createBySuccessMessage("success to register");

    }

    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            //starting checking
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("such user Existed");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("Email used by others");
                }
            }
        }
        else{
            return ServerResponse.createByErrorMessage("Wrong paramters");
        }
        return ServerResponse.createBySuccessMessage("success validation!");
    }

    public ServerResponse<String> selectQuestion(String username){
        ServerResponse validResonse = this.checkValid(username,Const.USERNAME);
        if(validResonse.isSuccess()){
            //not Exist!
            return ServerResponse.createByErrorMessage("user not existed!");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("No question for this user!");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            RedisPoolUtil.setEx(TokenCache.TOKEN_PREFIX+username,forgetToken,60*60*12);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("Wrong Answer!!");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("please answer the question first!");
        }
        //check if user valid!
        ServerResponse validResonse = this.checkValid(username,Const.USERNAME);
        if(validResonse.isSuccess()){
            //not Exist!
            return ServerResponse.createByErrorMessage("user not existed!");
        }
        String token = RedisPoolUtil.get(TokenCache.TOKEN_PREFIX+username);

        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("Token expired or invalid!");
        }

        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("successfully changed your password!");
            }
        }
        else{
            return ServerResponse.createByErrorMessage("somethins is wrong ~~~");
        }
        return ServerResponse.createByErrorMessage("failing changing your password!");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){
        //防止横向越权，要校验一下这个用户的旧密码,一定要指定userId
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("Wrong password!");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("successfully updating yoru password!");
        }
        return ServerResponse.createByErrorMessage("failing changing your password!");

    }

    public ServerResponse<User> updateInformation(User user){
        //username cant be updated,
        //email should be validated,
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email used by other user!");
        }
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPhone(user.getPhone());
        updatedUser.setQuestion(user.getQuestion());
        updatedUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updatedUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("successfully updating!",updatedUser);
        }
        return ServerResponse.createByErrorMessage("failing updating!");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("can't find this user");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("find the exact user",user);
    }


    //backend
    public ServerResponse<String> checkAdminRole(User user){
        if(user != null && user.getRole()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
