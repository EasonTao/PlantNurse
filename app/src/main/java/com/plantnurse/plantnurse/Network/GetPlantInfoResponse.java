package com.plantnurse.plantnurse.Network;

import com.kot32.ksimplelibrary.model.response.BaseResponse;

/**
 * Created by Cookie_D on 2016/9/2.
 */

public class GetPlantInfoResponse extends BaseResponse{
    public int responseCode;
    public String species;
    public int sun;
    public int water;
    public int cold;
    public int difficulty;
    public String introduction;
}