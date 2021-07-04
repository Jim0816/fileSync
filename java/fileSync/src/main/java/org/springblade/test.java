package org.springblade;

import org.flowable.common.engine.api.io.InputStreamProvider;
import org.springblade.modules.auth.utils.HttpUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class test {
	public static void main(String[] args) {
		String result = HttpUtil.sendGet("http://localhost:9900/api/blade-firereason/accidentInfoDeal/untreated", "phone=13985175699");
		System.out.println(result);
	}
}
