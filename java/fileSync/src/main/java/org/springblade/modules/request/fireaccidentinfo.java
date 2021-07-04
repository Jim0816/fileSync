package org.springblade.modules.request;

import com.alibaba.fastjson.JSONObject;
import org.json.JSONArray;
import org.springblade.core.tool.api.R;
import org.springblade.modules.auth.utils.HttpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/fireaccidentinfo")
public class fireaccidentinfo {
	@GetMapping("/list")
	public R list(){
		String result = HttpUtil.sendGet("http://localhost:9901/blade-firereason/accidentinfo/showInfo","");
		JSONObject json = JSONObject.parseObject(result);
		List<JSONObject> data = json.getJSONArray("data").toJavaList(JSONObject.class);
		return R.data(data);
	}

}
