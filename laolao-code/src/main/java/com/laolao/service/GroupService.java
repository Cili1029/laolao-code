package com.laolao.service;

import com.laolao.common.result.Result;
import com.laolao.pojo.vo.GroupVO;

import java.util.List;

public interface GroupService {
    Result<List<GroupVO>> getSimpleGroup();
}
