package com.awashbank.supply_chain.user;

import com.awashbank.supply_chain.response.serviceResponse;
import com.awashbank.supply_chain.service.Validation;
import com.awashbank.supply_chain.user.model.RegistrationModel;
import com.awashbank.supply_chain.user.model.UserDetailModel;
import com.awashbank.supply_chain.user.model.UserDetailRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserDetailRepository userDetailRepository;
    private final Validation val;

    public UserService(UserDetailRepository userDetailRepository,Validation val) {
        this.userDetailRepository = userDetailRepository;
        this.val = val;
    }

  /*  public serviceResponse userRegistration(String token, RegistrationModel reg) {
        serviceResponse srs = new serviceResponse();

        Optional<UserDetailModel> userDetailModel = val.getUserDetailByToken(token);

        if (userDetailModel.isPresent()){

                UserDetailModel detail = userDetailModel.get();

                detail.setDesignation(reg.getDesignation());
                detail.setDob(reg.getDob());
                detail.setName(reg.getName());
                detail.setCreate_date(LocalDateTime.now().toString());
                detail.setGender(reg.getGender().toString());
                detail.setPhone(reg.getPhone());
                detail.setRoom(reg.getRoom());

               userDetailRepository.save(detail);
                    srs.setStatusCode(200);
                    srs.setMessage("Successful Created!!!");

        }else {
            srs.setStatusCode(400);
            srs.setMessage("Couldn't find any user with provided info");
        }

        return srs;

    }*/

    public serviceResponse getUserInfo(String token){
        serviceResponse srs = new serviceResponse();

        Optional<UserDetailModel> userDetailModel = val.getUserDetailByToken(token);

        if (userDetailModel.isPresent()){
            srs.setStatusCode(200);
            srs.setMessage("Successful!!!");
            srs.setData(val.convertObjectToJsonNode(userDetailModel.get()));
        }else {
            srs.setStatusCode(400);
            srs.setMessage("Couldn't find any user with provided info");
        }

        return srs;

    }

   /* public serviceResponse getUsers(String token,String username){
        serviceResponse srs = new serviceResponse();

        Optional<UserDetailModel> userDetailModel = val.getUserDetailByToken(token);

        if (userDetailModel.isPresent()){

            List<UserDetailModel> ls  = userDetailRepository.getSearchedUser(userDetailModel.get().getUsername(),username + "%");
            srs.setStatusCode(200);
            srs.setMessage("Successful!!!");
            srs.setData(val.convertObjectListToJsonNode(ls));
        }else {
            srs.setStatusCode(400);
            srs.setMessage("Couldn't find any user with provided info");
        }

        return srs;

    }*/
}
