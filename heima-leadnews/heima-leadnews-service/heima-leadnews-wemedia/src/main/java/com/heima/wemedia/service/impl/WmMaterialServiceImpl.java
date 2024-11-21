package com.heima.wemedia.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.thread.WmThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;


    @Override
    public ResponseResult findList(WmMaterialDto dto) {
        // 1. 检查参数
        dto.checkParam();
// 调用传入的 `dto` 对象的 `checkParam()` 方法，用于检查传入的参数是否合法或符合预期条件。

// 2. 分页查询
        IPage page = new Page(dto.getPage(), dto.getSize());
// 创建一个分页对象 `page`，使用传入的 `dto` 中的 `page` 和 `size` 参数初始化分页信息。
// `IPage` 是一个接口，`Page` 是它的一个具体实现类，代表分页的页码、大小等信息。

        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
// 创建一个 Lambda 查询包装器 `lambdaQueryWrapper`，用于构建查询条件。
// `LambdaQueryWrapper` 是 MyBatis-Plus 提供的一个类，用于简化查询条件的构建。

// 是否收藏
        if(dto.getIsCollection() != null && dto.getIsCollection() == 1){
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }
// 如果 `dto` 中的 `isCollection` 参数不为空且等于 1，则将其作为查询条件之一。
// 这里使用 `lambdaQueryWrapper.eq` 方法，将 `isCollection` 作为等值查询条件之一。
// 即：查询用户收藏的素材。

// 按照用户查询
        System.out.println(WmThreadLocalUtils.getUser());
        System.out.println(WmThreadLocalUtils.getUser().getId());
        lambdaQueryWrapper.eq(WmMaterial::getUserId, WmThreadLocalUtils.getUser().getId());
// 添加查询条件：根据当前登录用户的 `userId` 进行筛选。
// 这里通过 `WmThreadLocalUtil.getUser().getId()` 获取当前用户的 `id`，并作为查询条件。

// 按照时间倒序
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);
// 添加排序条件：根据素材的创建时间 `createdTime` 进行倒序排列。

// 执行分页查询
        page = page(page, lambdaQueryWrapper);
// 调用分页查询方法 `page`，传入分页对象 `page` 和查询条件 `lambdaQueryWrapper`，得到分页查询结果并赋值给 `page`。
// 该方法会返回符合条件的分页数据（包含总记录数和当前页数据）。

// 3. 结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
// 创建一个响应结果对象 `responseResult`，并初始化分页信息：当前页、页大小和总记录数。
// `PageResponseResult` 是一种自定义响应结果类，通常用于封装分页数据的返回结果。

        responseResult.setData(page.getRecords());
// 设置响应数据，将分页查询得到的记录列表（当前页的数据）作为响应内容。

        return responseResult;
// 返回封装好的响应结果 `responseResult`，供前端或调用方使用。

    }

    @Override
    public ResponseResult cancelcollect(int i) {
        UpdateWrapper<WmMaterial> updateWrapper = new UpdateWrapper<>();

        // 设置条件：根据 ID 进行更新
        updateWrapper.eq("id", i) // ID 等于 userId
                .set("is_collection", 0); // 设置要更新的字段 is_collection

        // 执行更新
        boolean update = update(null, updateWrapper);// 第一个参数为 null，表示使用 UpdateWrapper 中的条件
        if (update) {
            System.out.println("Update successful, rows affected: ");
            return ResponseResult.okResult(200,"操作成功");
        } else {
            System.out.println("No rows updated, please check the conditions.");
            return ResponseResult.errorResult(501,"操作失败");
        }
    }

    @Override
    public ResponseResult collect(int i) {
        UpdateWrapper<WmMaterial> updateWrapper = new UpdateWrapper<>();

        // 设置条件：根据 ID 进行更新
        updateWrapper.eq("id", i) // ID 等于 userId
                .set("is_collection", 1); // 设置要更新的字段 is_collection

        // 执行更新
        boolean update = update(null, updateWrapper);// 第一个参数为 null，表示使用 UpdateWrapper 中的条件
        if (update) {
            System.out.println("Update successful, rows affected: ");
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        } else {
            System.out.println("No rows updated, please check the conditions.");
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
    }

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {

        //1.检查参数
        if(multipartFile == null || multipartFile.getSize() == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.上传图片到minIO中
        String fileName = UUID.randomUUID().toString().replace("-", "");
        //aa.jpg
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileId = null;
        try {
            fileId = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId:{}",fileId);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("WmMaterialServiceImpl-上传文件失败");
        }

        //3.保存到数据库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtils.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setType((short)0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        //4.返回结果

        return ResponseResult.okResult(wmMaterial);
    }

}