package com.cnbi.util.inter;

import com.cnbi.util.entry.Data;

import java.util.List;
import java.util.Map;

public interface SpecialProcessor {

    void process(Map<String, String> param, String cubeId, List<Data> datas);
}
