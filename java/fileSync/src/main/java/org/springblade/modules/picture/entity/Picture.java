package org.springblade.modules.picture.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;

@Data
@TableName("tb_picture")
public class Picture implements	Serializable{
    private	static final long serialVersionUID = 1L;
    @TableId(value = "id", type	= IdType.ID_WORKER)
    private Long id;
    private String url;
}
