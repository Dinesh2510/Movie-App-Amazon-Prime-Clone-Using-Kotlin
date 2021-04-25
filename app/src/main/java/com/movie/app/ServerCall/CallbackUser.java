package com.movie.app.ServerCall;



import com.movie.app.Model.User;

import java.io.Serializable;

public class CallbackUser implements Serializable {
    public String status = "";
    public User response = new User();
}
