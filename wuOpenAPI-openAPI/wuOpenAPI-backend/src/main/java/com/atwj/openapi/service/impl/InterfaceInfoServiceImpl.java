package com.atwj.openapi.service.impl;



import com.atwj.openapi.common.ErrorCode;
import com.atwj.openapi.exception.BusinessException;
import com.atwj.openapi.mapper.InterfaceInfoMapper;
import com.atwj.apicommon.model.entity.InterfaceInfo;
import com.atwj.openapi.service.InterfaceInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author blablablala
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-07-26 20:11:46
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }
}




