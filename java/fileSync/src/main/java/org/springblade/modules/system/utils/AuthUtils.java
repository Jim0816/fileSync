package org.springblade.modules.system.utils;

import org.json.JSONObject;
import org.springblade.modules.system.entity.Menu;

import java.util.*;

public class AuthUtils {
//    public static HashMap getAuth(List<Menu> old, List<JSONObject> news){
//        Collections.sort(old,new Comparator<Menu>() {
//            @Override
//            public int compare(Menu j1, Menu j2) {
//                int i1 = Integer.parseInt(j1.getIsSon().toString());
//                int i2 = Integer.parseInt(j2.getIsSon().toString());
//                return i1-i2;
//            }
//        });
//        Collections.sort(news,new Comparator<JSONObject>() {
//            @Override
//            public int compare(JSONObject j1, JSONObject j2) {
//                int i1 = Integer.parseInt(j1.get("id").toString());
//                int i2 = Integer.parseInt(j2.get("id").toString());
//                return i1-i2;
//            }
//        });
//        HashMap map = new HashMap();
//        List<JSONObject> add = new ArrayList();
//        List<Menu> update = new ArrayList();
//        List<Long> del = new ArrayList();
//        int i=0,j=0;
//        while(true) {
//            int id1 = Integer.parseInt(news.get(i).get("id").toString());
//            int id2 = Integer.parseInt(old.get(j).getIsSon().toString());
//            if (id1 < id2) {
//                System.out.println(news.get(i));
//                add.add(news.get(i));
//                i++;
//            } else if (id1 == id2) {
//                old.get(j).setName(news.get(i).get("name").toString());
//                update.add(old.get(j));
//                i++;
//                j++;
//            } else {
//                System.out.println(old.get(j));
//                del.add(old.get(j).getId());
//                j++;
//            }
//            if (news.size() == i) {
//                for (int x = j; x < old.size(); x++) {
//                    del.add(old.get(x).getId());
//                }
//                map.put("add",add);
//                map.put("update",update);
//                map.put("del",del);
//                break;
//            }
//            if (old.size() == j) {
//                for (int x = i; x < news.size(); x++) {
//                    add.add(news.get(x));
//                }
//                map.put("add",add);
//                map.put("update",update);
//                map.put("del",del);
//                break;
//            }
//        }
//        return map;
//    }
}
