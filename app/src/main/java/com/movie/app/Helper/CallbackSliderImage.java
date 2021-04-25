package com.movie.app.Helper;


import com.movie.app.Model.NewsInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallbackSliderImage implements Serializable {

    public String status = "";
    public List<NewsInfo> banner_info = new ArrayList<>();

}