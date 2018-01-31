package com.zl.skypointer.apm.network.trace.component;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 10:27
 * @since JDK1.8
 */
public class OfficialComponent implements Component {
    private int id;
    private String name;

    public OfficialComponent(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
