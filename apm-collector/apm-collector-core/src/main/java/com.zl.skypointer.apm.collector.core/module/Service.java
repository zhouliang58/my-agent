package com.zl.skypointer.apm.collector.core.module;

/**
 * 服务接口。通过实现 Service 接口，实现不同功能的服务。
 *
 * Module 是与 Service “直接” 一对多的关系。中间 有一层 ModuleProvider 存在的原因是，
 * 相同 Module 可以有多种 ModuleProvider 实现，而 ModuleProvider 提供提供相同功能的 Service ，但是实现不同。
 *
 * Created by zhouliang
 * 2018-02-07 10:11
 */
public interface Service {
}