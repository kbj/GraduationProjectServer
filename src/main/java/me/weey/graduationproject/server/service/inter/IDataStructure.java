package me.weey.graduationproject.server.service.inter;

/**
 * Created by WeiKai on 2018/01/27.
 */
public interface IDataStructure {

    /**
     *  根据传来的文本判断数据结构是否完成
     */
    boolean checkDataStructureIntact(String payload);
}
